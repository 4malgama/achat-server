package org.amalgama.network;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;

public class TcpConnectionHandler extends PacketDispatchHandler {
    private final ConnectionController controller;

    public TcpConnectionHandler(ConnectionController controller) {
        super(controller);
        this.controller = controller;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        controller.newConnection(ctx);
        java.util.logging.Logger.getGlobal().info(
                "New TCP connection: '" + ctx.getChannel().getRemoteAddress() + "' #" + ctx.getChannel().getId()
        );
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        controller.disconnect(ctx);
        java.util.logging.Logger.getGlobal().info(
                "TCP disconnect: '" + ctx.getChannel().getRemoteAddress() + "' #" + ctx.getChannel().getId()
        );
    }
}
