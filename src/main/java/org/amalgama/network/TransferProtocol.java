package org.amalgama.network;

import org.amalgama.database.DBService;
import org.amalgama.database.dao.UserDAO;
import org.amalgama.database.entities.Chat;
import org.amalgama.database.entities.User;
import org.amalgama.network.packets.*;
import org.amalgama.servecies.CacheService;
import org.amalgama.utils.CryptoUtils;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;
import java.util.Objects;

public class TransferProtocol {
    private ChannelHandlerContext context;
    private Channel channel;
    public ClientData clientData = new ClientData();
    private DBService dbService = DBService.getInstance();

    public TransferProtocol(ChannelHandlerContext ctx) {
        this.context = ctx;
        this.channel = ctx.getChannel();
    }

    public void onDisconnect() {

    }

    public void acceptPacket(Packet packet) {
        try {
            if (packet instanceof PacketLogin packetLogin) {
                onLogin(packetLogin.login, packetLogin.password);
            } else if (packet instanceof PacketRegister packetRegister) {
                onRegister(packetRegister.login, packetRegister.password);
            } else if (packet instanceof PacketInitLocation packetInitLocation) {
                onInitLocation(packetInitLocation.location);
            } else if (packet instanceof PacketCheckAvatarHash packetCheckAvatarHash) {
                onCheckAvatarHash(packetCheckAvatarHash.avatarHash);
            } else if (packet instanceof PacketUpdateProfile packetUpdateProfile) {
                onUpdateProfile(packetUpdateProfile.changes);
            }
        } catch (Exception e) {
            System.out.println("[EXCEPTION]: " + e.getMessage());
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

    private void onLogin(String login, String password) {
        if (dbService.validateCredentials(login, password)) {
            clientData.user = UserDAO.getUser(login);
            PacketAuthAccept packet = new PacketAuthAccept();
            packet.uid = clientData.user.getId();
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
