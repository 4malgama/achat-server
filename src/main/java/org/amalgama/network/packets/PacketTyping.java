package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketTyping extends Packet {
    public boolean isTyping;
    public long chatId;

    public PacketTyping() {
        this.id = 3350;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        chatId = buffer.readLong();
        isTyping = buffer.readByte() != 0;
    }

    @Override
    public void send(ChannelBuffer buffer) {
        buffer.writeLong(chatId);
        buffer.writeByte(isTyping ? 1 : 0);
    }

    @Override
    public int size() {
        return 9;
    }
}
