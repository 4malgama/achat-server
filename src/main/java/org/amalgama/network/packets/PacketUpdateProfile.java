package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketUpdateProfile extends Packet {
    public String changes;

    public PacketUpdateProfile() {
        id = 501;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        int length = buffer.readShort();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append(buffer.readChar());
        changes = sb.toString();
    }

    @Override
    public void send(ChannelBuffer buffer) {
        //no
    }

    @Override
    public int size() {
        return 0;
    }
}
