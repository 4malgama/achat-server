package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketClientReady extends Packet {
    public PacketClientReady() {
        this.id = 3;
    }

    @Override
    public void receive(ChannelBuffer buffer) {

    }

    @Override
    public void send(ChannelBuffer buffer) {

    }

    @Override
    public int size() {
        return 0;
    }
}
