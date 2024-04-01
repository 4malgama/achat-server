package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketAuthAccept extends Packet {
    public PacketAuthAccept() {
        this.id = 102;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        //server don't receive this packet
    }

    @Override
    public void send(ChannelBuffer buffer) {
        //nothing
    }

    @Override
    public int size() {
        //empty packet
        return 0;
    }
}
