package org.amalgama.network.web;

import org.amalgama.network.ConnectionController;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.*;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class WebSocketHandshakeHandler extends SimpleChannelUpstreamHandler {
    private final String websocketPath;
    private final ConnectionController controller;
    private WebSocketServerHandshaker handshaker;
    private boolean websocketAccepted = false;

    public WebSocketHandshakeHandler(String websocketPath, ConnectionController controller) {
        this.websocketPath = websocketPath;
        this.controller = controller;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object msg = e.getMessage();

        if (msg instanceof HttpRequest) {
            handleHttpRequest(ctx, (HttpRequest) msg);
            return;
        }

        if (msg instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) msg;

            if (frame instanceof CloseWebSocketFrame) {
                if (handshaker != null) {
                    handshaker.close(ctx.getChannel(), (CloseWebSocketFrame) frame);
                } else {
                    ctx.getChannel().close();
                }
                return;
            }

            if (frame instanceof PingWebSocketFrame) {
                ctx.getChannel().write(new PongWebSocketFrame(frame.getBinaryData()));
                return;
            }

            if (frame instanceof PongWebSocketFrame) {
                return;
            }

            // Binary frame передаём дальше в decoder
            super.messageReceived(ctx, e);
            return;
        }

        super.messageReceived(ctx, e);
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) {
        if (req.getMethod() != GET) {
            sendHttpResponse(ctx, req,
                    new DefaultHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }

        if (!websocketPath.equals(req.getUri())) {
            sendHttpResponse(ctx, req,
                    new DefaultHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }

        String location = getWebSocketLocation(req, websocketPath);

        WebSocketServerHandshakerFactory wsFactory =
                new WebSocketServerHandshakerFactory(location, null, false);

        handshaker = wsFactory.newHandshaker(req);

        if (handshaker == null) {
            wsFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
            return;
        }

        handshaker.handshake(ctx.getChannel(), req);
        websocketAccepted = true;
        controller.newConnection(ctx);

        java.util.logging.Logger.getGlobal().info(
                "New WS connection: '" + ctx.getChannel().getRemoteAddress() + "' #" + ctx.getChannel().getId()
        );
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
        if (res.getStatus().getCode() != 200) {
            res.setContent(ChannelBuffers.copiedBuffer(res.getStatus().toString().getBytes()));
            HttpHeaders.setContentLength(res, res.getContent().readableBytes());
        }

        ChannelFuture f = ctx.getChannel().write(res);

        if (!HttpHeaders.isKeepAlive(req) || res.getStatus().getCode() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static String getWebSocketLocation(HttpRequest req, String path) {
        return "wss://" + req.headers().get(HOST) + path;
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (websocketAccepted) {
            controller.disconnect(ctx);
        }
        java.util.logging.Logger.getGlobal().info(
                "WS disconnect: '" + ctx.getChannel().getRemoteAddress() + "' #" + ctx.getChannel().getId()
        );
        super.channelDisconnected(ctx, e);
    }
}
