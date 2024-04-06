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
            case 103 -> packet = new PacketRegister();
            case 301 -> packet = new PacketInitLocation();
            case 500 -> packet = new PacketInitProfile();
            case 501 -> packet = new PacketUpdateProfile();
            case 1000 -> packet = new PacketCheckAvatarHash();
            case 1001 -> packet = new PacketUpdateAvatar();
            default -> packet = null;
        }
        return packet;
    }
}
