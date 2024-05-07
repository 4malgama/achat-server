package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketInitChats extends Packet {
    public String jsonData;

    public PacketInitChats() {
        this.id = 1002;
    }

    @Override
    public void receive(ChannelBuffer buffer) {

    }

    @Override
    public void send(ChannelBuffer buffer) {
        buffer.writeInt(jsonData.length());
        for (int i = 0; i < jsonData.length(); i++)
            buffer.writeChar(jsonData.charAt(i));
    }

    @Override
    public int size() {
        return 4 + jsonData.length() * 2;
    }
}
