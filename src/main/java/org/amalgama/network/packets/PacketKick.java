package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketKick extends Packet {
    public String reason;

    public PacketKick(int id) {
        super(id);
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        int length = buffer.readShort();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++)
            builder.append(buffer.readChar());
        reason = builder.toString();
    }

    @Override
    public void send(ChannelBuffer buffer) {
        int length = reason.length();
        buffer.writeShort(length);
        for (int i = 0; i < length; i++)
            buffer.writeChar(reason.charAt(i));
    }
}
