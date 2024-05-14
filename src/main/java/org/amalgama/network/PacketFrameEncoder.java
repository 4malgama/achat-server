package org.amalgama.network;

import org.amalgama.network.packets.Packet;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class PacketFrameEncoder extends OneToOneEncoder {
    private ChannelHandlerContext clientCtx = null;

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof Packet))
            return msg;
        Packet packet = (Packet) msg;

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        Packet.write(packet, buffer);

        ConnectionController controller = ConnectionHandler.getInstance().getController();
        if (clientCtx == null) {
            clientCtx = channel.getPipeline().getContext("handler");
        }
        TransferProtocol client = controller.getConnection(clientCtx);
        if (client != null && client.encryptionEnabled) {
            byte[] bytes = new byte[buffer.readableBytes()];
            buffer.getBytes(buffer.readerIndex(), bytes);
            byte[] cipher = client.aes.encrypt(bytes);
            int cipherLength = cipher.length;
            buffer.clear();
            buffer.writeInt(cipherLength);
            buffer.writeBytes(cipher);
        }

        return buffer;
    }

    public ChannelHandlerContext getClientCtx() {
        return clientCtx;
    }

    public void setClientCtx(ChannelHandlerContext clientCtx) {
        this.clientCtx = clientCtx;
    }
}
