package org.amalgama.network;

import org.amalgama.network.packets.Packet;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;

public class PacketFrameDecoder extends ReplayingDecoder<VoidEnum> {
    private ChannelHandlerContext clientCtx = null;

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        ctx.sendUpstream(e);
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        ctx.sendUpstream(e);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, VoidEnum state) throws Exception {
        ConnectionController controller = ConnectionHandler.getInstance().getController();
        if (clientCtx == null) {
            clientCtx = channel.getPipeline().getContext("handler");
        }
        TransferProtocol client = controller.getConnection(clientCtx);
        if (client != null && client.encryptionEnabled) {
            if (buffer.readableBytes() < 4)
                return null;
            int cipherLength = buffer.readInt();
            if (buffer.readableBytes() < cipherLength)
                return null;
            byte[] cipher = new byte[cipherLength];
            buffer.readBytes(cipher);
            byte[] decrypted = client.aes.decrypt(cipher);
            buffer = ChannelBuffers.wrappedBuffer(decrypted);
        }
        return Packet.read(buffer);
    }

    public ChannelHandlerContext getClientCtx() {
        return clientCtx;
    }

    public void setClientCtx(ChannelHandlerContext clientCtx) {
        this.clientCtx = clientCtx;
    }
}
