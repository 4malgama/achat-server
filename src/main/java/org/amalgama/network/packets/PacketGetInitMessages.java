package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketGetInitMessages extends Packet {
    public long chatId;

    public PacketGetInitMessages() {
        this.id = 1003;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        chatId = buffer.readLong();
    }

    @Override
    public void send(ChannelBuffer buffer) {
        //server doesn't need this
    }

    @Override
    public int size() {
        return 0;
    }
}
