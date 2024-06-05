package org.amalgama.network;

import org.amalgama.database.DBService;
import org.amalgama.database.dao.UserDAO;
import org.amalgama.database.entities.Attachment;
import org.amalgama.database.entities.Chat;
import org.amalgama.database.entities.Message;
import org.amalgama.database.entities.User;
import org.amalgama.network.packets.*;
import org.amalgama.network.services.ChatService;
import org.amalgama.security.certification.CertificationManager;
import org.amalgama.security.encryption.AES;
import org.amalgama.security.encryption.AESMode;
import org.amalgama.servecies.CacheService;
import org.amalgama.utils.CryptoUtils;
import org.amalgama.utils.TokenUtils;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransferProtocol {
    private ChannelHandlerContext context;
    private Channel channel;
    public boolean encryptionSent = false;
    public boolean encryptionEnabled = false;
    public ClientData clientData = new ClientData();
    public AES aes;
    private DBService dbService = DBService.getInstance();

    public TransferProtocol(ChannelHandlerContext ctx) {
        this.context = ctx;
        this.channel = ctx.getChannel();
        this.aes = null;
    }

    public void onDisconnect() {

    }

    public void send(Packet packet) {
        channel.write(packet);
    }

    public void acceptPacket(Packet packet) {
        try {
            if (packet instanceof PacketClientHello) {
                onClientHello();
            } else if (packet instanceof PacketClientReady) {
                if (encryptionSent) {
                    encryptionEnabled = true;
                    PacketServerReady p = new PacketServerReady();
                    channel.write(p);
                }
            } else if (packet instanceof PacketLogin packetLogin) {
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
            }
        } catch (Exception e) {
            System.out.println("[EXCEPTION]: " + e.getMessage());
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

        channel.write(new PacketAuthReject(clientData.locale.equalsIgnoreCase("RU") ? "Неверный логин или пароль" : "Invalid credentials"));
    }

    private void onClientHello() throws Exception {
        if (encryptionEnabled || encryptionSent)
            return;
        String certificate = CertificationManager.getCertificate();
        aes = new AES(AESMode.CBC);
        aes.setKey(AES.generateKey(256));
        aes.iv = AES.generateIV();
        PacketServerHello packet = new PacketServerHello();
        packet.protocolVersion = "1.0";
        packet.Certificate = certificate;
        packet.clientKey = CryptoUtils.getBase64(aes.getKey());
        packet.IV = CryptoUtils.getBase64(aes.iv);
        encryptionSent = true;
        channel.write(packet);
    }

    private void onSendMessage(long chatId, String jsonData) throws ParseException {
        DBService db = DBService.getInstance();
        Chat chat = db.getChat(clientData.user, chatId);
        if (chat != null) {
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

        UserDAO.updateUser(clientData.user);
        initProfile();
    }

    private void initProfile() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(clientData.user.getSettingsData());
            JSONObject privateSettings = (JSONObject) json.get("private_settings");

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

            JSONObject private_settings = new JSONObject();
            private_settings.put("see_avatar", privateSettings.get("see_avatar"));
            private_settings.put("see_description", privateSettings.get("see_description"));
            private_settings.put("see_post", privateSettings.get("see_post"));
            private_settings.put("send_friend_request", privateSettings.get("send_friend_request"));
            private_settings.put("see_online_status", privateSettings.get("see_online_status"));
            private_settings.put("send_messages", privateSettings.get("send_messages"));
            private_settings.put("can_invite_groups", privateSettings.get("can_invite_groups"));
            private_settings.put("hide_forwards", privateSettings.get("hide_forwards"));

            JSONObject permissions = new JSONObject();
            permissions.put("ban", false);
            permissions.put("ban_ip", false);
            permissions.put("ban_max_time", 0);
            permissions.put("see_ip", false);
            permissions.put("mute", false);

            JSONObject init_profile = new JSONObject();
            init_profile.put("profile_info", profile_info);
            init_profile.put("private_settings", private_settings);
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
                User user = chat.getUser();
                if (Objects.equals(user.getId(), clientData.user.getId()))
                    user = chat.getSecond();
                jsonUser.put("id", user.getId());
                jsonUser.put("surname", user.getSName());
                jsonUser.put("name", user.getFName());
                jsonUser.put("patronymic", user.getMName());
                jsonUser.put("post", user.getPost());
                jsonUser.put("avatar_data", CryptoUtils.getBase64(cs.getUserAvatar(user.getId())));
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

    public void close() {
        onDisconnect();
        channel.close();
    }
}
