package org.amalgama.network.services;

import org.amalgama.database.DBService;
import org.amalgama.database.entities.Chat;
import org.amalgama.database.entities.User;
import org.amalgama.network.ConnectionController;
import org.amalgama.network.ConnectionHandler;
import org.amalgama.network.TransferProtocol;
import org.amalgama.network.packets.Packet;

import java.util.Objects;

public class ChatService {

    public static void broadcastChat(Chat chat, Packet packet) {
        DBService db = DBService.getInstance();
        ConnectionController controller = ConnectionHandler.getInstance().getController();
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
}
