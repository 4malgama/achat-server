package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketClientHello extends Packet {
    public PacketClientHello() {
        this.id = 1;
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
