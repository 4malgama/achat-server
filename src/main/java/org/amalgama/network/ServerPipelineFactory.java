package org.amalgama.network;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

public class ServerPipelineFactory implements ChannelPipelineFactory {
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        PacketFrameEncoder encoder = new PacketFrameEncoder();
        PacketFrameDecoder decoder = new PacketFrameDecoder();
        pipeline.addLast("decoder", decoder);
        pipeline.addLast("encoder", encoder);
        pipeline.addLast("handler", ConnectionHandler.getInstance());
        return pipeline;
    }
}
