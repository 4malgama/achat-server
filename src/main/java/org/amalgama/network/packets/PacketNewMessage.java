package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketNewMessage extends Packet {
    public String jsonData;

    public PacketNewMessage() {
        this.id = 2001;
    }

    @Override
    public void receive(ChannelBuffer buffer) {

    }

    @Override
    public void send(ChannelBuffer buffer) {
        buffer.writeInt(jsonData.length());
        for (int i = 0; i < jsonData.length(); i++) {
            buffer.writeChar(jsonData.charAt(i));
        }
    }

    @Override
    public int size() {
        return 4 + jsonData.length() * 2;
    }
}
