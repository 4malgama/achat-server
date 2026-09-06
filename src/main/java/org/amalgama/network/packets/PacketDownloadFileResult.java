package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

/** 2703: requestId, fileId, status, error (string16), name (string32), bytes32. */
public class PacketDownloadFileResult extends Packet {
    public long requestId;
    public long fileId;
    public int status;
    public String error = "";
    public String fileName = "";
    public byte[] fileData = new byte[0];

    public PacketDownloadFileResult() {
        this.id = 2703;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        throw new IllegalArgumentException("Download results are server-only");
    }

    @Override
    public void send(ChannelBuffer buffer) {
        buffer.writeLong(requestId);
        buffer.writeLong(fileId);
        buffer.writeShort(status);
        buffer.writeShort(error.length());
        for (int i = 0; i < error.length(); i++) buffer.writeChar(error.charAt(i));
        buffer.writeInt(fileName.length());
        for (int i = 0; i < fileName.length(); i++) buffer.writeChar(fileName.charAt(i));
        buffer.writeInt(fileData.length);
        buffer.writeBytes(fileData);
    }

    @Override
    public int size() {
        return 28 + error.length() * 2 + fileName.length() * 2 + fileData.length;
    }
}
