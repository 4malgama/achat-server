package org.amalgama.network;

import org.amalgama.network.packets.Packet;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class PacketFrameEncoder extends OneToOneEncoder {
    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof Packet))
            return msg;
        Packet packet = (Packet) msg;

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        Packet.write(packet, buffer);
        return buffer;
    }
}
