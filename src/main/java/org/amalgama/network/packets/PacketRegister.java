package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketRegister extends Packet {
    public int errorCode;
    public String login;
    public String password;

    public PacketRegister() {
        this.id = 103;
    }
    @Override
    public void receive(ChannelBuffer buffer) {
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
        buffer.writeShort(errorCode);
    }

    @Override
    public int size() {
        return 2;
    }
}
