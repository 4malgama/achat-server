package org.amalgama.network;

import org.amalgama.security.tls.ServerTls;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

public class ServerPipelineFactory implements ChannelPipelineFactory {
    private final ConnectionController controller;

    public ServerPipelineFactory(ConnectionController controller) {
        this.controller = controller;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("tls", ServerTls.newHandler());
        pipeline.addLast("decoder", new PacketFrameDecoder());
        pipeline.addLast("encoder", new PacketFrameEncoder());
        pipeline.addLast("handler", new TcpConnectionHandler(controller));

        return pipeline;
    }
}
