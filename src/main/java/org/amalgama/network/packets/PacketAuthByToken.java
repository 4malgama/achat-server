package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketAuthByToken extends Packet {
    public String token;
    public PacketAuthByToken() {
        this.id = 5;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        int length = buffer.readShort();
        StringBuilder tokenBuilder = new StringBuilder();
        for (int i = 0; i < length; i++)
            tokenBuilder.append(buffer.readChar());
        token = tokenBuilder.toString();
    }

    @Override
    public void send(ChannelBuffer buffer) {

    }

    @Override
    public int size() {
        return 0;
    }
}
