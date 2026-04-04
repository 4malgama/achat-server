package org.amalgama.network;

import org.amalgama.network.packets.Packet;
import org.jboss.netty.channel.*;

public class PacketDispatchHandler extends SimpleChannelUpstreamHandler {
    private final ConnectionController controller;

    public PacketDispatchHandler(ConnectionController controller) {
        this.controller = controller;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof Packet) {
            controller.acceptPacket(ctx, (Packet) e.getMessage());
            return;
        }
        super.messageReceived(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Channel channel = ctx.getChannel();
        if (channel.isOpen()) {
            channel.close();
        }
        java.util.logging.Logger.getGlobal().warning("Exception caught: " + e.getCause());
    }
}
