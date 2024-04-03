package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketUpdateAvatar extends Packet {
    public byte[] avatarData;

    public PacketUpdateAvatar() {
        this.id = 1001;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        //no
    }

    @Override
    public void send(ChannelBuffer buffer) {
        buffer.writeBytes(avatarData);
    }

    @Override
    public int size() {
        return avatarData.length;
    }
}
