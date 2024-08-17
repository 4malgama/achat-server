package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketUpdateChatId extends Packet {
    public long chatId;
    public String login;

    public PacketUpdateChatId(long chatId, String login) {
        this.chatId = chatId;
        this.login = login;
        this.id = 2850;
    }
    public PacketUpdateChatId() {
        this.id = 2850;
    }

    @Override
    public void receive(ChannelBuffer buffer) {

    }

    @Override
    public void send(ChannelBuffer buffer) {
        buffer.writeLong(chatId);
        int length = login.length();
        buffer.writeShort(length);
        for (int i = 0; i < length; i++)
            buffer.writeChar(login.charAt(i));
    }

    @Override
    public int size() {
        return 10 + login.length() * 2;
    }
}
