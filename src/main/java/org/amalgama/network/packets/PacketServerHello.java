package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketServerHello extends Packet {
    public String protocolVersion = "2.0";

    public PacketServerHello() {
        this.id = 2;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        throw new UnsupportedOperationException("ServerHello is a server-to-client packet");
    }

    @Override
    public void send(ChannelBuffer buffer) {
        validateVersion();

        buffer.writeShort(protocolVersion.length());

        for (int i = 0; i < protocolVersion.length(); i++) {
            buffer.writeChar(protocolVersion.charAt(i));
        }
    }

    @Override
    public int size() {
        validateVersion();
        return 2 + protocolVersion.length() * 2;
    }

    private void validateVersion() {
        if (protocolVersion == null
                || protocolVersion.isEmpty()
                || protocolVersion.length() > 32) {
            throw new IllegalStateException(
                    "Invalid protocol version"
            );
        }
    }
}
