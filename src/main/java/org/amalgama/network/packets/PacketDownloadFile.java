package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketDownloadFile extends Packet {
    public long fileId;

    public PacketDownloadFile() {
        this.id = 2700;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        fileId = buffer.readLong();
    }

    @Override
    public void send(ChannelBuffer buffer) {

    }

    @Override
    public int size() {
        return 0;
    }
}
