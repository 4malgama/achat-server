package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketCreateChatWithMessage extends Packet {
    public long userId;
    public String messageData;

    public PacketCreateChatWithMessage() {
        this.id = 2800;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        userId = buffer.readLong();
        int length = buffer.readInt();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append(buffer.readChar());
        messageData = sb.toString();
    }

    @Override
    public void send(ChannelBuffer buffer) {

    }

    @Override
    public int size() {
        return 0;
    }
}
