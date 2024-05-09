package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketSendMessage extends Packet {
    public long chatId;
    public String jsonData;

    public PacketSendMessage() {
        this.id = 2000;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        chatId = buffer.readLong();
        int length = buffer.readInt();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.readChar());
        }
        jsonData = sb.toString();
    }

    @Override
    public void send(ChannelBuffer buffer) {

    }

    @Override
    public int size() {
        return 0;
    }
}
