package org.amalgama.network.web;

import org.amalgama.network.packets.Packet;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import java.io.IOException;

public class WebSocketPacketDecoder extends OneToOneDecoder {
    private static final int MAX_PACKET_SIZE = 64 * 1024 * 1024;

    @Override
    protected Object decode(
            ChannelHandlerContext ctx,
            Channel channel,
            Object msg
    ) throws Exception {
        if (!(msg instanceof BinaryWebSocketFrame frame)) {
            throw new IOException("Expected a binary WebSocket frame");
        }

        if (!frame.isFinalFragment() || frame.getRsv() != 0) {
            throw new IOException("Unsupported WebSocket frame format");
        }

        ChannelBuffer buffer = frame.getBinaryData().duplicate();

        if (buffer.readableBytes() < 2
                || buffer.readableBytes() > MAX_PACKET_SIZE) {
            throw new IOException("Invalid incoming packet size");
        }

        Packet packet = Packet.read(buffer);

        if (buffer.readable()) {
            throw new IOException("Unexpected trailing packet data");
        }

        return packet;
    }
}