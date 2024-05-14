package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketServerReady extends Packet {
    public PacketServerReady() {
        this.id = 4;
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
