package org.amalgama.network;

import org.amalgama.database.DBService;
import org.amalgama.database.dao.UserDAO;
import org.amalgama.database.entities.Attachment;
import org.amalgama.database.entities.Chat;
import org.amalgama.database.entities.Message;
import org.amalgama.database.entities.User;
import org.amalgama.network.packets.*;
import org.amalgama.network.services.AccessData;
import org.amalgama.network.services.ChatService;
import org.amalgama.network.services.UserAccessService;
import org.amalgama.servecies.CacheService;
import org.amalgama.utils.CryptoUtils;
import org.amalgama.utils.TokenUtils;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransferProtocol {
    private final Channel channel;

    public SessionState getState() {
        return state;
    }

    private SessionState state = SessionState.WAIT_CLIENT_HELLO;
    public ClientData clientData = new ClientData();
    private final DBService dbService = DBService.getInstance();

    public TransferProtocol(ChannelHandlerContext ctx) {
        this.channel = ctx.getChannel();
    }

    public void onDisconnect() {

    }

    public void send(Packet packet) {
        channel.write(packet);
    }

    public void acceptPacket(Packet packet) {
        try {
            if (state == SessionState.WAIT_CLIENT_HELLO) {
                if (packet instanceof PacketClientHello) {
                    onClientHello();
                    return;
                }
                disconnectByError("Expected ClientHello");
                return;
            }

            if (state == SessionState.WAIT_CLIENT_READY) {
                if (packet instanceof PacketClientReady) {
                    onClientReady();
                    return;
                }
                disconnectByError("Expected ClientReady");
                return;
            }

            if (state != SessionState.READY) {
                disconnectByError("Connection is not ready");
                return;
            }

            if (packet instanceof PacketLogin packetLogin) {
                onLogin(packetLogin.login, packetLogin.password, packetLogin.remember);
            } else if (packet instanceof PacketAuthByToken packetAuthByToken) {
                onLogin(packetAuthByToken.token);
            } else if (packet instanceof PacketRegister packetRegister) {
                onRegister(packetRegister.login, packetRegister.password);
            } else if (packet instanceof PacketInitLocation packetInitLocation) {
                onInitLocation(packetInitLocation.location);
            } else if (packet instanceof PacketCheckAvatarHash packetCheckAvatarHash) {
                onCheckAvatarHash(packetCheckAvatarHash.avatarHash);
            } else if (packet instanceof PacketUpdateProfile packetUpdateProfile) {
                onUpdateProfile(packetUpdateProfile.changes);
            } else if (packet instanceof PacketGetInitMessages packetGetInitMessages) {
                onGetInitMessages(packetGetInitMessages.chatId);
            } else if (packet instanceof PacketSendMessage packetSendMessage) {
                onSendMessage(packetSendMessage.chatId, packetSendMessage.jsonData);
            } else if (packet instanceof PacketSearch packetSearch) {
                onSearch(packetSearch.json);
            } else if (packet instanceof PacketDownloadFile packetDownloadFile) {
                onDownloadFile(packetDownloadFile.fileId);
            } else if (packet instanceof PacketCreateChatWithMessage packetCreateChatWithMessage) {
                onCreateChatWithMessage(packetCreateChatWithMessage.userId, packetCreateChatWithMessage.messageData);
            } else if (packet instanceof PacketUpdateAvatar packetUpdateAvatar) {
                onUpdateAvatar(packetUpdateAvatar.avatarData);
            } else if (packet instanceof PacketTyping packetTyping) {
                onTyping(packetTyping.chatId, packetTyping.isTyping);
            }
        } catch (Exception e) {
            System.out.println("[EXCEPTION]: " + e.getMessage());
        }
    }

    private void disconnectByError(String string) {
        System.out.println("Disconnect by error: " + string);
        close();
    }

    private void onClientReady() {
        state = SessionState.READY;
        channel.write(new PacketServerReady());
    }

    private void onTyping(long chatId, boolean isTyping) {
        ChatService.sendTyping(chatId, clientData.user, isTyping);
    }

    private void onUpdateAvatar(byte[] avatarData) {
        CacheService.getInstance().setUserAvatar(clientData.user.getId(), avatarData);
    }

    private void onCreateChatWithMessage(long userId, String messageData) throws ParseException {
        if (clientData.user == null) {
            return;
        }

        DBService db = DBService.getInstance();
        User recipient = db.getUser(userId);

        if (recipient == null
                || Objects.equals(recipient.getId(), clientData.user.getId())) {
            return;
        }

        AccessData access = UserAccessService.accessBetween(clientData.user, recipient);

        if (!access.accessSendMessage) {
            return;
        }

        if (db.getChat(clientData.user, recipient) != null) {
            return;
        }

        Chat chat = new Chat();
        chat.setGroup(false);
        chat.setUser(clientData.user);
        chat.setSecond(recipient);

        db.addChat(chat);

        if (chat.getId() == null) {
            return;
        }

        updateChatId(chat.getId(), recipient.getLogin());

        JSONObject json = new JSONObject();
        json.put("chat_id", chat.getId());
        json.put("user_data", makePublicUserJson(recipient, clientData.user));

        PacketCreateChat packet = new PacketCreateChat();
        packet.jsonData = json.toJSONString();

        TransferProtocol secondClient = getClientByLogin(recipient.getLogin());
        if (secondClient != null) {
            secondClient.send(packet);
        }

        onSendMessage(chat.getId(), messageData);
    }

    private void updateChatId(Long id, String login) {
        PacketUpdateChatId packet = new PacketUpdateChatId(id, login);
        channel.write(packet);
    }

    private void onDownloadFile(long fileId) {
        Attachment attachment = dbService.getAttachment(fileId);
        if (attachment == null)
            return;
        Message message = attachment.getMessage();
        if (message == null)
            return;
        Chat chat = message.getChat();
        if (chat == null)
            return;
        if (!chat.isGroup() && !(Objects.equals(chat.getUser().getId(), clientData.user.getId()) || Objects.equals(chat.getSecond().getId(), clientData.user.getId())))
            return;
        CacheService cache = CacheService.getInstance();
        byte[] bytes = cache.readAttachment(chat.getId(), attachment);
        if (bytes == null)
            return;
        PacketSendFile packet = new PacketSendFile();
        packet.fileData = bytes;
        packet.fileName = attachment.getName();
        channel.write(packet);
    }

    private JSONObject makePublicUserJson(User viewer, User target) {
        AccessData access = UserAccessService.accessBetween(viewer, target);

        String firstName = Objects.toString(target.getFName(), "");
        String surname = Objects.toString(target.getSName(), "");
        String middleName = Objects.toString(target.getMName(), "");

        if (!access.displayName.isEmpty()) {
            firstName = access.displayName;
            surname = "";
            middleName = "";
        }

        String displayName = (surname + " " + firstName + " " + middleName).trim();

        if (displayName.isEmpty()) {
            displayName = target.getLogin();
        }

        JSONObject json = new JSONObject();
        json.put("user_id", target.getId());
        json.put("login", target.getLogin());
        json.put("fname", firstName);
        json.put("sname", surname);
        json.put("mname", middleName);
        json.put("display_name", displayName);
        json.put("post", access.accessPost ? Objects.toString(target.getPost(), "") : "");

        json.put("avatar_data", null);

        if (access.accessPhoto) {
            byte[] avatar = CacheService.getInstance()
                    .getUserAvatar(target.getId());

            if (avatar != null) {
                json.put("avatar_data", CryptoUtils.getBase64(avatar));
            }
        }

        return json;
    }

    private void onSearch(String json) {
        if (clientData.user == null || json == null) {
            return;
        }

        JSONParser parser = new JSONParser();

        try {
            Object parsed = parser.parse(json);
            if (!(parsed instanceof JSONObject)) {
                return;
            }

            JSONObject request = (JSONObject) parsed;
            Object text = request.get("text");
            if (!(text instanceof String)) {
                return;
            }

            List<User> users = dbService.getAllUsersByLogin((String) text);
            JSONArray jsonUsers = new JSONArray();

            if (users != null) {
                for (User user : users) {
                    if (user == null
                            || Objects.equals(user.getId(), clientData.user.getId())) {
                        continue;
                    }

                    Chat chat = dbService.getChat(user, clientData.user);
                    JSONObject jsonUser =
                            makePublicUserJson(clientData.user, user);

                    jsonUser.put("chat_id", chat != null ? chat.getId() : 0);
                    jsonUsers.add(jsonUser);
                }
            }

            JSONObject result = new JSONObject();
            result.put("results", jsonUsers);

            PacketSearch packet = new PacketSearch();
            packet.json = result.toJSONString();
            channel.write(packet);
        } catch (ParseException e) {
            java.util.logging.Logger.getGlobal()
                    .warning("Invalid search JSON");
        }
    }

    private void onLogin(String token) throws ParseException {
        Long uid = TokenUtils.parseJWT(token);
        if (uid != null) {
            User user = UserDAO.getUser(uid);
            if (user != null) {
                clientData.user = user;
                PacketAuthAccept packet = new PacketAuthAccept();
                packet.uid = uid;
                channel.write(packet);
                initProfile();
                initChats();
                return;
            }
        }

        channel.write(new PacketAuthReject(clientData.locale.equalsIgnoreCase("RU") ? "Ваш токен устарел или несуществует" : "Your token is invalid or does not exist"));
    }

    private void onClientHello() throws Exception {
        PacketServerHello packet = new PacketServerHello();
        packet.protocolVersion = "2.0";

        state = SessionState.WAIT_CLIENT_READY;

        channel.write(packet);
    }

    private void onSendMessage(long chatId, String jsonData) throws ParseException {
        if (clientData.user == null) {
            return;
        }

        DBService db = DBService.getInstance();
        Chat chat = db.getChat(clientData.user, chatId);

        if (chat != null) {
            if (!chat.isGroup()) {
                User recipient;

                if (chat.getUser() != null
                        && Objects.equals(
                        chat.getUser().getId(),
                        clientData.user.getId()
                )) {
                    recipient = chat.getSecond();
                } else if (chat.getSecond() != null
                        && Objects.equals(
                        chat.getSecond().getId(),
                        clientData.user.getId()
                )) {
                    recipient = chat.getUser();
                } else {
                    return;
                }

                if (recipient == null
                        || !UserAccessService.accessBetween(
                        clientData.user,
                        recipient
                ).accessSendMessage) {
                    return;
                }
            }

            Message message = new Message();
            message.setChat(chat);
            message.setTimestamp(System.currentTimeMillis() / 1000L);
            message.setUser(clientData.user);

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonData);
            message.setContent((String) json.get("content"));
            db.addMessage(message);
            //start attachments
            List<Attachment> attachments = new ArrayList<>();
            if (json.containsKey("attachments")) {
                JSONArray jsonAttachments = (JSONArray) json.get("attachments");
                CacheService cacheService = CacheService.getInstance();
                for (Object jsonAttachment : jsonAttachments) {
                    JSONObject attachment = (JSONObject) jsonAttachment;
                    String name = (String) attachment.get("name");
                    String data = (String) attachment.get("data");
                    byte[] fileData = CryptoUtils.fromBase64(data);
                    Attachment a = new Attachment();
                    a.setMessage(message);
                    a.setName(name);
                    if (name.contains(".")) a.setType(name.substring(name.lastIndexOf('.') + 1).toLowerCase());
                    else a.setType("txt");
                    attachments.add(a);
                    db.addAttachment(a);
                    cacheService.saveAttachment(chatId, a.getId() + "_" + name, fileData);
                }
                //db.addAttachments(attachments);
            }
            //end attachments
            newMessage(message, attachments);
        }
    }

    private void newMessage(Message message, List<Attachment> attachments) {
        JSONObject json = new JSONObject();
        json.put("chat_id", message.getChat().getId());
        json.put("id", message.getId());
        json.put("time", message.getTimestamp());
        json.put("content", message.getContent());
        json.put("reply_id", message.getReplyId());
        json.put("forward_id", message.getForwardId());

        JSONObject sender = new JSONObject();
        sender.put("id", message.getUser().getId());
        sender.put("name", message.getUser().getFName());
        sender.put("surname", message.getUser().getSName());
        sender.put("patronymic", message.getUser().getMName());
        json.put("sender", sender);

        if (!attachments.isEmpty()) {
            JSONArray attachmentsArray = new JSONArray();
            for (Attachment attachment : attachments) {
                JSONObject attachmentJson = new JSONObject();
                attachmentJson.put("id", attachment.getId());
                attachmentJson.put("name", attachment.getName());
                attachmentJson.put("type", attachment.getType());
                attachmentJson.put("size", CacheService.getInstance().getAttachmentSize(message.getChat().getId(), attachment));
                attachmentsArray.add(attachmentJson);
            }
            json.put("attachments", attachmentsArray);
        }

        PacketNewMessage packet = new PacketNewMessage();
        packet.jsonData = json.toJSONString();
        ChatService.broadcastChat(message.getChat(), packet);
    }

    private void onGetInitMessages(long chatId) {
        DBService db = DBService.getInstance();
        Chat chat = db.getChat(clientData.user, chatId);
        if (chat != null) {
            List<Message> messages = db.getMessages(chat);

            JSONObject json = new JSONObject();
            JSONArray jsonMessages = new JSONArray();
            for (Message message : messages) {
                User sender = message.getUser();

                JSONArray attachments = new JSONArray();

                CacheService cacheService = CacheService.getInstance();
                for (Attachment a : db.getAttachments(message)) {
                    JSONObject attachment = new JSONObject();
                    attachment.put("id", a.getId());
                    attachment.put("name", a.getName());
                    attachment.put("type", a.getType());
                    attachment.put("size", cacheService.getAttachmentSize(chatId, a));
                    attachments.add(attachment);
                }

                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("id", message.getId());
                jsonMessage.put("content", message.getContent());
                jsonMessage.put("time", message.getTimestamp());

                JSONObject jsonSender = new JSONObject();
                jsonSender.put("id", sender.getId());
                jsonSender.put("name", sender.getFName());
                jsonSender.put("surname", sender.getSName());
                jsonSender.put("patronymic", sender.getMName());

                jsonMessage.put("sender", jsonSender);
                jsonMessage.put("reply_id", message.getReplyId());
                jsonMessage.put("forward_id", message.getForwardId());
                jsonMessage.put("attachments", attachments);

                jsonMessages.add(jsonMessage);
            }

            json.put("messages", jsonMessages);
            json.put("chat_id", chat.getId());
            PacketInitMessages packet = new PacketInitMessages();
            packet.jsonData = json.toJSONString();
            channel.write(packet);
        }
    }

    private void onUpdateProfile(String changes) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(changes);

        if (json.containsKey("email")) {
            clientData.user.setEmail((String) json.get("email"));
        }
        if (json.containsKey("first_name")) {
            clientData.user.setFName((String) json.get("first_name"));
        }
        if (json.containsKey("sur_name")) {
            clientData.user.setSName((String) json.get("sur_name"));
        }
        if (json.containsKey("patronymic")) {
            clientData.user.setMName((String) json.get("patronymic"));
        }
        if (json.containsKey("post")) {
            clientData.user.setPost((String) json.get("post"));
        }
        if (json.containsKey("description")) {
            clientData.user.setDescription((String) json.get("description"));
        }

        //TODO private settings
        if (json.containsKey("privacy")) {
            JSONObject jsonPrivacy = (JSONObject) json.get("privacy");
            JSONParser parserSettings = new JSONParser();
            JSONObject jsonSettings = (JSONObject) parserSettings.parse(clientData.user.getSettingsData());
            jsonSettings.put("privacy_settings", jsonPrivacy);
            clientData.user.setSettingsData(jsonSettings.toJSONString());
        }

        UserDAO.updateUser(clientData.user);
        initProfile();
    }

    private void initProfile() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(clientData.user.getSettingsData());
            JSONObject privateSettings = (JSONObject) json.get("privacy_settings");

            JSONObject profile_info = new JSONObject();
            profile_info.put("login", clientData.user.getLogin());
            profile_info.put("email", clientData.user.getEmail());
            profile_info.put("first_name", clientData.user.getFName());
            profile_info.put("sur_name", clientData.user.getSName());
            profile_info.put("patronymic", clientData.user.getMName());
            profile_info.put("post", clientData.user.getPost());
            profile_info.put("description", clientData.user.getDescription());
            profile_info.put("score", clientData.user.getScore());
            profile_info.put("balance", clientData.user.getBalance());
            profile_info.put("cash", clientData.user.getCash());
            profile_info.put("registration_date", clientData.user.getRegisterTimestamp());

            JSONObject privacy_settings = new JSONObject();
            privacy_settings.put("see_profile_photo", privateSettings.get("see_profile_photo"));
            privacy_settings.put("see_profile_description", privateSettings.get("see_profile_description"));
            privacy_settings.put("see_profile_comments", privateSettings.get("see_profile_comments"));
            privacy_settings.put("leave_comments", privateSettings.get("leave_comments"));
            privacy_settings.put("see_profile_post", privateSettings.get("see_profile_post"));
            privacy_settings.put("send_friend_request", privateSettings.get("send_friend_request"));
            privacy_settings.put("see_online_status", privateSettings.get("see_online_status"));
            privacy_settings.put("send_message", privateSettings.get("send_message"));
            privacy_settings.put("invite_to_groups", privateSettings.get("invite_to_groups"));
            privacy_settings.put("display_name", privateSettings.get("display_name"));

            JSONObject permissions = new JSONObject();
            permissions.put("ban", false);
            permissions.put("ban_ip", false);
            permissions.put("ban_max_time", 0);
            permissions.put("see_ip", false);
            permissions.put("mute", false);

            JSONObject init_profile = new JSONObject();
            init_profile.put("profile_info", profile_info);
            init_profile.put("privacy_settings", privacy_settings);
            init_profile.put("permissions", permissions);

            PacketInitProfile packetInitProfile = new PacketInitProfile();
            packetInitProfile.jsonData = init_profile.toJSONString();
            channel.write(packetInitProfile);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void onCheckAvatarHash(String avatarHash) {
        byte[] avatarData = CacheService.getInstance().getUserAvatar(clientData.user.getId());
        if (avatarData == null)
            return;

        //TODO send null image if avatarData == null

        if (!avatarHash.equals(CryptoUtils.getHash(avatarData, "MD5"))) {
            PacketUpdateAvatar packet = new PacketUpdateAvatar();
            packet.avatarData = avatarData;
            channel.write(packet);
        }
    }

    private void onInitLocation(String location) {
        clientData.locale = location;
    }

    private void onRegister(String login, String password) {
        if (dbService.isRegistered(login)) {
            PacketRegister packetRegister = new PacketRegister();
            packetRegister.errorCode = 1;
            channel.write(packetRegister);
        }
        else if (!checkPasswordForSafe(password)) {
            PacketRegister packetRegister = new PacketRegister();
            packetRegister.errorCode = 2;
            channel.write(packetRegister);
        }
        else if (login.contains("$")) {
            PacketRegister packetRegister = new PacketRegister();
            packetRegister.errorCode = 3;
            channel.write(packetRegister);
        }
        else {
            clientData.user = dbService.registerUser(login, password);
            PacketRegister packetRegister = new PacketRegister();
            packetRegister.errorCode = 0;
            packetRegister.uid = clientData.user.getId();
            channel.write(packetRegister);
            initProfile();
            initChats();
        }
    }

    private boolean checkPasswordForSafe(String password) {
        boolean no_char_repeat = !password.matches(".*(\\w)\\1{3,}.*");
        boolean good_length = password.length() > 7;
        boolean good_format = password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
        boolean simple_password = password.contains("1234") || password.contains("qwerty") || password.contains("password");
        return no_char_repeat && good_length && good_format && !simple_password;
    }

    private void onLogin(String login, String password, boolean remember) {
        if (dbService.validateCredentials(login, password)) {
            clientData.user = UserDAO.getUser(login);
            PacketAuthAccept packet = new PacketAuthAccept();
            packet.uid = clientData.user.getId();
            if (remember) {
                packet.token = TokenUtils.newJWT(clientData.user.getId());
            }
            channel.write(packet);
            initProfile();
            initChats();
        }
        else {
            channel.write(new PacketAuthReject(clientData.locale.equalsIgnoreCase("RU") ? "Неверный логин или пароль" : "Invalid credentials"));
        }
    }

    private void initChats() {
        DBService db = DBService.getInstance();
        CacheService cs = CacheService.getInstance();
        List<Chat> chats = db.getChats(clientData.user);

        JSONArray jsonChats = new JSONArray();

        for (Chat chat : chats) {
            JSONObject jsonChat = new JSONObject();
            jsonChat.put("id", chat.getId());
            jsonChat.put("is_group", chat.isGroup());
            if (!chat.isGroup()) {
                JSONObject jsonUser = new JSONObject();
                User userOpponent = chat.getUser();
                if (Objects.equals(userOpponent.getId(), clientData.user.getId()))
                    userOpponent = chat.getSecond();
                if (userOpponent == null)
                    continue;

                AccessData accessData = UserAccessService.accessBetween(clientData.user, userOpponent);

                jsonUser.put("id", userOpponent.getId());

                if (accessData.displayName.isEmpty()) {
                    jsonUser.put("surname", userOpponent.getSName());
                    jsonUser.put("name", userOpponent.getFName());
                    jsonUser.put("patronymic", userOpponent.getMName());
                } else {
                    jsonUser.put("surname", "");
                    jsonUser.put("name", accessData.displayName);
                    jsonUser.put("patronymic", "");
                }

                String post = accessData.accessPost ? userOpponent.getPost() : "";
                jsonUser.put("post", post);

                if (accessData.accessPhoto) {
                    byte[] avatarBytes = cs.getUserAvatar(userOpponent.getId());
                    if (avatarBytes != null)
                        jsonUser.put("avatar_data", CryptoUtils.getBase64(avatarBytes));
                }

                jsonChat.put("user", jsonUser);
            }
            jsonChats.add(jsonChat);
        }

        JSONObject json = new JSONObject();
        json.put("chats", jsonChats);

        PacketInitChats packetInitChats = new PacketInitChats();
        packetInitChats.jsonData = json.toJSONString();
        channel.write(packetInitChats);
    }

    public static TransferProtocol getClientByIP(SocketAddress ip) {
        for (TransferProtocol client : NetworkShared.getController().getConnections()) {
            if (client.channel.getRemoteAddress().equals(ip))
                return client;
        }

        return null;
    }

    public static TransferProtocol getClientByLogin(String login) {
        if (login == null) {
            return null;
        }

        for (TransferProtocol client : NetworkShared.getController().getConnections()) {
            if (client.clientData.user != null
                    && login.equals(client.clientData.user.getLogin())) {
                return client;
            }
        }

        return null;
    }

    public void close() {
        onDisconnect();
        channel.close();
    }
}
