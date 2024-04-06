package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketInitProfile extends Packet {
    public String jsonData;

    public PacketInitProfile() {
        this.id = 500;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        //nothing
    }

    @Override
    public void send(ChannelBuffer buffer) {
        buffer.writeShort(jsonData.length());
        for (int i = 0; i < jsonData.length(); i++)
            buffer.writeChar(jsonData.charAt(i));
    }

    @Override
    public int size() {
        return 2 + jsonData.length() * 2;
    }
}
