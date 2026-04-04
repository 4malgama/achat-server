package org.amalgama.network.web;

import org.amalgama.network.ConnectionController;
import org.amalgama.network.TransferProtocol;
import org.amalgama.network.packets.Packet;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

public class WebSocketPacketDecoder extends OneToOneDecoder {
    private final ConnectionController controller;

    public WebSocketPacketDecoder(ConnectionController controller) {
        this.controller = controller;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof BinaryWebSocketFrame frame)) {
            return msg;
        }

        ChannelBuffer buffer = frame.getBinaryData();

        ConnectionController controller = this.controller;
        TransferProtocol client = controller.getConnection(channel);

        if (client != null && client.encryptionEnabled) {
            byte[] cipher = new byte[buffer.readableBytes()];
            buffer.readBytes(cipher);

            byte[] decrypted = client.aes.decrypt(cipher);
            buffer = ChannelBuffers.wrappedBuffer(decrypted);
        }

        return Packet.read(buffer);
    }
}
