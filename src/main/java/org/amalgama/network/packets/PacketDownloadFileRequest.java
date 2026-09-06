package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

/** Correlated download used by Web. The Qt 2700/2701 exchange is unchanged. */
public class PacketDownloadFileRequest extends PacketDownloadFile {
    public long requestId;

    public PacketDownloadFileRequest() {
        this.id = 2702;
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        fileId = buffer.readLong();
        requestId = buffer.readLong();
        if (fileId <= 0 || requestId <= 0) {
            throw new IllegalArgumentException("Invalid download identifier");
        }
    }
}
