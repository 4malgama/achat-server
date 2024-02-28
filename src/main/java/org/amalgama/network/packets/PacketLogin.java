package org.amalgama.network.packets;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketLogin extends Packet{
    public String login;

    public PacketLogin(int id) {
        super(id);
    }

    @Override
    public void receive(ChannelBuffer buffer) {
        int length = buffer.readShort();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++)
            builder.append(buffer.readChar());
        login = builder.toString();
    }

    @Override
    public void send(ChannelBuffer buffer) {
        //server don't send this packet
    }}
