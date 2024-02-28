package org.amalgama.network;

import org.amalgama.network.packets.Packet;
import org.amalgama.network.packets.PacketKick;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

public class TransferProtocol {
    private ChannelHandlerContext context;
    private Channel channel;

    public TransferProtocol(ChannelHandlerContext ctx) {
        this.context = ctx;
        this.channel = ctx.getChannel();
    }

    public void onDisconnect() {

    }

    public void acceptPacket(Packet packet) {
        System.out.println("Accepted packet id: " + packet.getId());
    }

    public void close() {
        onDisconnect();
        channel.close();
    }
}
