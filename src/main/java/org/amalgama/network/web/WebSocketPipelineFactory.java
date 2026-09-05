package org.amalgama.network.web;

import org.amalgama.network.ConnectionController;
import org.amalgama.network.PacketDispatchHandler;
import org.amalgama.security.tls.ServerTls;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

public class WebSocketPipelineFactory implements ChannelPipelineFactory {
    private final ConnectionController controller;

    public WebSocketPipelineFactory(ConnectionController controller) {
        this.controller = controller;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("tls", ServerTls.newHandler());
        pipeline.addLast("httpDecoder", new HttpRequestDecoder());
        pipeline.addLast("httpAggregator", new HttpChunkAggregator(65536));
        pipeline.addLast("httpEncoder", new HttpResponseEncoder());

        pipeline.addLast(
                "wsHandshake",
                new WebSocketHandshakeHandler("/ws", controller)
        );

        pipeline.addLast("wsDecoder", new WebSocketPacketDecoder());
        pipeline.addLast("wsEncoder", new WebSocketPacketEncoder());

        pipeline.addLast(
                "packetHandler",
                new PacketDispatchHandler(controller)
        );

        return pipeline;
    }
}