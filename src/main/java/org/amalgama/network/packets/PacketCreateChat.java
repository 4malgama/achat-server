package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketCreateChat extends Packet {
    public String jsonData;

    public PacketCreateChat() {
        this.id = 2860;
    }

    @Override
    public void receive(ChannelBuffer buffer) {

    }

    @Override
    public void send(ChannelBuffer buffer) {
        int length = jsonData.length();
        buffer.writeInt(length);
        for (int i = 0; i < length; i++)
            buffer.writeChar(jsonData.charAt(i));
    }

    @Override
    public int size() {
        return 0;
    }
}
