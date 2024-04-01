package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketAuthReject extends Packet {
    public String reason;

    public PacketAuthReject() {
        this.id = 101;
    }

    public PacketAuthReject(String reason) {
        this.id = 101;
        this.reason = reason;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        //server don't receive this packet
    }

    @Override
    public void send(ChannelBuffer buffer) {
        int length = reason.length();
        buffer.writeShort(length);
        for (int i = 0; i < length; i++)
            buffer.writeChar(reason.charAt(i));
    }

    @Override
    public int size() {
        //string, length
        return (reason.length() * 2 + 2);
    }
}
