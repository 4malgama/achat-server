package org.amalgama.network;

import org.amalgama.network.packets.Packet;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import java.io.IOException;

public class PacketFrameEncoder extends OneToOneEncoder {
    private static final int MAX_PACKET_SIZE = 64 * 1024 * 1024;

    @Override
    protected Object encode(
            ChannelHandlerContext ctx,
            Channel channel,
            Object msg
    ) throws Exception {
        if (!(msg instanceof Packet packet)) {
            return msg;
        }

        long expectedSize = 6L + packet.size();

        if (expectedSize < 6 || expectedSize > MAX_PACKET_SIZE) {
            channel.close();
            throw new IOException("Invalid outgoing packet size");
        }

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();

        Packet.write(packet, buffer);

        if (buffer.readableBytes() != expectedSize) {
            channel.close();
            throw new IOException(
                    "Serialized packet size does not match Packet.size()"
            );
        }

        return buffer;
    }
}
