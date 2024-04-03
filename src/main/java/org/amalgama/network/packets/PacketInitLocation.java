package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketInitLocation extends Packet {
    public String location;

    public PacketInitLocation() {
        this.id = 301;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        int length = buffer.readShort();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++)
            builder.append(buffer.readChar());
        location = builder.toString();
    }

    @Override
    public void send(ChannelBuffer buffer) {

    }

    @Override
    public int size() {
        return 0;
    }
}
