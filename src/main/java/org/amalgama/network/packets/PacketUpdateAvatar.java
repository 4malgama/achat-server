package org.amalgama.network.packets;

import org.amalgama.utils.CryptoUtils;
import org.jboss.netty.buffer.ChannelBuffer;

public class PacketUpdateAvatar extends Packet {
    public byte[] avatarData;
    public String avatarBase64;

    public PacketUpdateAvatar() {
        this.id = 1001;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        int length = buffer.readInt();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.readChar());
        }
        avatarBase64 = sb.toString();
        avatarData = CryptoUtils.fromBase64(avatarBase64);
    }

    @Override
    public void send(ChannelBuffer buffer) {
        buffer.writeBytes(avatarData);
    }

    @Override
    public int size() {
        return avatarData.length;
    }
}
