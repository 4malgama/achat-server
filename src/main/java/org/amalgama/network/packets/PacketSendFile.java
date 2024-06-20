package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketSendFile extends Packet {
    public byte[] fileData;
    public String fileName;

    public PacketSendFile() {
        this.id = 2701;
    }

    @Override
    public void receive(ChannelBuffer buffer) {

    }

    @Override
    public void send(ChannelBuffer buffer) {
        buffer.writeInt(fileData.length);
        buffer.writeBytes(fileData);
        int length = fileName.length();
        buffer.writeInt(length);
        for (int i = 0; i < length; i++)
            buffer.writeChar(fileName.charAt(i));
    }

    @Override
    public int size() {
        return 8 + fileData.length + fileName.length() * 2;
    }
}
