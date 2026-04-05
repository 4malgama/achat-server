package org.amalgama.network;

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
        pipeline.addLast("decoder", new PacketFrameDecoder(controller));
        pipeline.addLast("encoder", new PacketFrameEncoder(controller));
        pipeline.addLast("handler", new TcpConnectionHandler(controller));
        return pipeline;
    }
}
