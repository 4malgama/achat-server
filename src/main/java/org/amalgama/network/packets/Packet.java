package org.amalgama.network.packets;

import org.amalgama.network.PacketFactory;
import org.jboss.netty.buffer.ChannelBuffer;

import java.io.IOException;

public abstract class Packet {
    protected int id;

    public static Packet read(ChannelBuffer buffer) throws IOException {
        int id = buffer.readUnsignedShort();
        Packet packet = PacketFactory.makePacket(id);
        if (packet == null)
            throw new IOException("Bad packet ID: " + id);
        packet.receive(buffer);
        return packet;
    }

    public static void write(Packet packet, ChannelBuffer buffer) {
        //6 = sizeof id (unsigned short) + sizeof size
        buffer.writeInt(6 + packet.size());
        buffer.writeChar(packet.id);
        packet.send(buffer);
    }

    public abstract void receive(ChannelBuffer buffer);
    public abstract void send(ChannelBuffer buffer);
    public abstract int size();
}
