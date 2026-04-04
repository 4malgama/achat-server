package org.amalgama.network.web;

import org.amalgama.network.ConnectionController;
import org.amalgama.network.TransferProtocol;
import org.amalgama.network.packets.Packet;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class WebSocketPacketEncoder extends OneToOneEncoder {
    private final ConnectionController controller;

    public WebSocketPacketEncoder(ConnectionController controller) {
        this.controller = controller;
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof Packet packet)) {
            return msg;
        }

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        Packet.write(packet, buffer);

        ConnectionController controller = this.controller;
        TransferProtocol client = controller.getConnection(channel);

        if (client != null && client.encryptionEnabled) {
            byte[] bytes = new byte[buffer.readableBytes()];
            buffer.getBytes(buffer.readerIndex(), bytes);

            byte[] cipher = client.aes.encrypt(bytes);
            buffer = ChannelBuffers.wrappedBuffer(cipher);
        }

        return new BinaryWebSocketFrame(buffer);
    }
}
