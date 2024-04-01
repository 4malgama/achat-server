package org.amalgama.network;

import org.amalgama.network.packets.*;

public class PacketFactory {
    public static Packet makePacket(int id) {
        Packet packet;
        switch (id) {
            case 100 -> packet = new PacketLogin();
            case 200 -> packet = new PacketKick();
            case 101 -> packet = new PacketAuthReject();
            case 102 -> packet = new PacketAuthAccept();
            default -> packet = null;
        }
        return packet;
    }
}
