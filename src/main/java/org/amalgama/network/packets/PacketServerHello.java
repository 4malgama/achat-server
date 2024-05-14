package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketServerHello extends Packet {
    public String protocolVersion;
    public String Certificate;
    public String clientKey;
    public String IV;

    public PacketServerHello() {
        this.id = 2;
    }

    @Override
    public void receive(ChannelBuffer buffer) {

    }

    @Override
    public void send(ChannelBuffer buffer) {
        int versionLen = protocolVersion.length();
        int certLen = Certificate.length();
        int keyLen = clientKey.length();
        int ivLen = IV.length();
        buffer.writeShort(versionLen);
        buffer.writeShort(certLen);
        buffer.writeShort(keyLen);
        buffer.writeShort(ivLen);
        for (int i = 0; i < versionLen; i++) buffer.writeChar(protocolVersion.charAt(i));
        for (int i = 0; i < certLen; i++) buffer.writeChar(Certificate.charAt(i));
        for (int i = 0; i < keyLen; i++) buffer.writeChar(clientKey.charAt(i));
        for (int i = 0; i < ivLen; i++) buffer.writeChar(IV.charAt(i));
    }

    @Override
    public int size() {
        return 8 + protocolVersion.length() * 2 + Certificate.length() * 2 + clientKey.length() * 2 + IV.length() * 2;
    }
}
