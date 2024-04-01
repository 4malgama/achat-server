package org.amalgama.network;

import org.amalgama.database.DBService;
import org.amalgama.network.packets.Packet;
import org.amalgama.network.packets.PacketAuthAccept;
import org.amalgama.network.packets.PacketAuthReject;
import org.amalgama.network.packets.PacketLogin;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

public class TransferProtocol {
    private ChannelHandlerContext context;
    private Channel channel;
    private DBService dbService = DBService.getInstance();

    public TransferProtocol(ChannelHandlerContext ctx) {
        this.context = ctx;
        this.channel = ctx.getChannel();
    }

    public void onDisconnect() {

    }

    public void acceptPacket(Packet packet) {
        if (packet instanceof PacketLogin packetLogin) {
            onLogin(packetLogin.login, packetLogin.password);
        }
    }

    private void onLogin(String login, String password) {
        if (dbService.validateCredentials(login, password))
            channel.write(new PacketAuthAccept());
        else
            channel.write(new PacketAuthReject("Invalid credentials"));
    }

    public void close() {
        onDisconnect();
        channel.close();
    }
}
