package org.amalgama.network;

import org.amalgama.network.packets.Packet;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionController {
    private final ConcurrentHashMap<Channel, TransferProtocol> connections = new ConcurrentHashMap<>();

    public ConnectionController() {
    }

    public void newConnection(ChannelHandlerContext ctx) {
        connections.put(ctx.getChannel(), new TransferProtocol(ctx));
    }

    public void disconnect(ChannelHandlerContext ctx) {
        if (connections.containsKey(ctx.getChannel())) {
            connections.get(ctx.getChannel()).onDisconnect();
            connections.remove(ctx.getChannel());
        }
    }

    public void acceptMessage(ChannelHandlerContext ctx, MessageEvent e) {
        if (connections.containsKey(ctx.getChannel())) {
            connections.get(ctx.getChannel()).acceptPacket((Packet) e.getMessage());
        }
    }

    public Collection<TransferProtocol> getConnections() {
        return connections.values();
    }

    public TransferProtocol getConnection(Channel ch) {
        return connections.get(ch);
    }
}
