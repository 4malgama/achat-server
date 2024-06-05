package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketAuthAccept extends Packet {
    public long uid;
    public String token = null;

    public PacketAuthAccept() {
        this.id = 102;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        //server don't receive this packet
    }

    @Override
    public void send(ChannelBuffer buffer) {
        buffer.writeLong(uid);
        if (token != null) {
            buffer.writeByte(1);
            buffer.writeShort(token.length());
            for (int i = 0; i < token.length(); i++) {
                buffer.writeChar(token.charAt(i));
            }
        }
        else {
            buffer.writeByte(0);
        }
    }

    @Override
    public int size() {
        //empty packet
        return 9 + (token != null ? token.length() * 2 + 2 : 0);
    }
}
