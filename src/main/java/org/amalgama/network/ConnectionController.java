package org.amalgama.network;

import org.amalgama.network.packets.Packet;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Collection;
import java.util.HashMap;

public class ConnectionController implements Runnable {
    private Thread t = new Thread(this);
    private final HashMap<ChannelHandlerContext, TransferProtocol> connections = new HashMap<>();

    public ConnectionController() {
        this.t.setName("thread_ConnectionController");
        this.t.start();
    }

    public void newConnection(ChannelHandlerContext ctx) {
        connections.put(ctx, new TransferProtocol(ctx));
    }

    public void disconnect(ChannelHandlerContext ctx) {
        if (connections.containsKey(ctx)) {
            connections.get(ctx).onDisconnect();
            connections.remove(ctx);
        }
    }

    public void acceptMessage(ChannelHandlerContext ctx, MessageEvent e) {
        if (connections.containsKey(ctx)) {
            connections.get(ctx).acceptPacket((Packet) e.getMessage());
        }
    }

    public Collection<TransferProtocol> getConnections() {
        return connections.values();
    }

    @Override
    public void run() { }

    public TransferProtocol getConnection(ChannelHandlerContext ctx) {
        return connections.get(ctx);
    }
}
