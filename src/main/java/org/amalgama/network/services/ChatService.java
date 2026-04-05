package org.amalgama.network.services;

import org.amalgama.database.DBService;
import org.amalgama.database.entities.Chat;
import org.amalgama.database.entities.User;
import org.amalgama.network.ConnectionController;
import org.amalgama.network.NetworkShared;
import org.amalgama.network.TransferProtocol;
import org.amalgama.network.packets.Packet;
import org.amalgama.network.packets.PacketTyping;

import java.util.Objects;

public class ChatService {

    public static void broadcastChat(Chat chat, Packet packet) {
        DBService db = DBService.getInstance();
        ConnectionController controller = NetworkShared.getController();
        if (!chat.isGroup()) {
            User user1 = chat.getUser();
            User user2 = chat.getSecond();

            for (TransferProtocol net : controller.getConnections()) {
                if (Objects.equals(net.clientData.user.getId(), user1.getId()) || Objects.equals(net.clientData.user.getId(), user2.getId())) {
                    net.send(packet);
                }
            }
        }
    }

    public static void sendTyping(long chatId, User user, boolean isTyping) {
        Chat chat = DBService.getInstance().getChat(user, chatId);
        if (chat != null) {
            User receiver = chat.getSecond();

            if (Objects.equals(receiver.getId(), user.getId()))
                receiver = chat.getUser();

            if (receiver != null) {
                PacketTyping packet = new PacketTyping();
                packet.chatId = chatId;
                packet.isTyping = isTyping;
                ConnectionController controller = NetworkShared.getController();
                for (TransferProtocol net : controller.getConnections()) {
                    if (Objects.equals(net.clientData.user.getId(), receiver.getId())) {
                        net.send(packet);
                    }
                }
            }
        }
    }
}
