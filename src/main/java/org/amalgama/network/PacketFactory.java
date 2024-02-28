package org.amalgama.network;

import org.amalgama.network.packets.*;

public class PacketFactory {
    public static Packet makePacket(int id) {
        Packet packet;
        switch (id) {
            case 100 -> packet = new PacketLogin(100);
            case 200 -> packet = new PacketKick(200);
            default -> packet = null;
        }
        return packet;
    }
}
