package org.amalgama.network;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

public class ServerPipelineFactory implements ChannelPipelineFactory {
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("decoder", new PacketFrameDecoder());
        pipeline.addLast("encoder", new PacketFrameEncoder());
        pipeline.addLast("handler", ConnectionHandler.getInstance());
        return pipeline;
    }
}
