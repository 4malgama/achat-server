package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketCheckAvatarHash extends Packet {
    public String avatarHash;

    public PacketCheckAvatarHash() {
        this.id = 1000;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        int length = buffer.readShort();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.readChar());
        }
        avatarHash = sb.toString();
    }

    @Override
    public void send(ChannelBuffer buffer) {
        //not used
    }

    @Override
    public int size() {
        return 0;
    }
}
