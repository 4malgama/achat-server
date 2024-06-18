package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketSearch extends Packet {
    public String json;

    public PacketSearch() {
        this.id = 2500;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        int length = buffer.readShort();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append(buffer.readChar());
        json = sb.toString();
    }

    @Override
    public void send(ChannelBuffer buffer) {
        int length = json.length();
        buffer.writeInt(length);
        for (int i = 0; i < length; i++)
            buffer.writeChar(json.charAt(i));
    }

    @Override
    public int size() {
        return 4 + json.length() * 2;
    }
}
