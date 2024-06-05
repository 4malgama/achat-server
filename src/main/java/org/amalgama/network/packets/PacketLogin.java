package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketLogin extends Packet{
    public String login;
    public String password;
    public boolean remember;

    public PacketLogin() {
        this.id = 100;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        remember = buffer.readByte() != 0;
        int loginLength = buffer.readShort();
        int passwordLength = buffer.readShort();
        StringBuilder loginBuilder = new StringBuilder();
        for (int i = 0; i < loginLength; i++)
            loginBuilder.append(buffer.readChar());
        login = loginBuilder.toString();
        StringBuilder passwordBuilder = new StringBuilder();
        for (int i = 0; i < passwordLength; i++)
            passwordBuilder.append(buffer.readChar());
        password = passwordBuilder.toString();
    }

    @Override
    public void send(ChannelBuffer buffer) {
        //server don't send this packet
    }

    @Override
    public int size() {
        return 0;
    }
}
