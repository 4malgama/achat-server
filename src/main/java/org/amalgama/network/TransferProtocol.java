package org.amalgama.network;

import org.amalgama.database.DBService;
import org.amalgama.database.dao.UserDAO;
import org.amalgama.database.entities.User;
import org.amalgama.network.packets.*;
import org.amalgama.servecies.CacheService;
import org.amalgama.utils.CryptoUtils;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

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
        if (packet instanceof PacketLogin packetLogin) {
            onLogin(packetLogin.login, packetLogin.password);
        }
        else if (packet instanceof PacketRegister packetRegister) {
            onRegister(packetRegister.login, packetRegister.password);
        }
        else if (packet instanceof PacketInitLocation packetInitLocation) {
            onInitLocation(packetInitLocation.location);
        }
        else if (packet instanceof PacketCheckAvatarHash packetCheckAvatarHash) {
            onCheckAvatarHash(packetCheckAvatarHash.avatarHash);
        }
    }

    private void onCheckAvatarHash(String avatarHash) {
        User user = UserDAO.getUser(clientData.login);
        assert user != null;
        byte[] avatarData = CacheService.getInstance().getUserAvatar(user.getId());
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
            dbService.registerUser(login, password);
            PacketRegister packetRegister = new PacketRegister();
            packetRegister.errorCode = 0;
            clientData.login = login;
            channel.write(packetRegister);
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
            clientData.login = login;
            channel.write(new PacketAuthAccept());
        }
        else {
            channel.write(new PacketAuthReject(clientData.locale.equalsIgnoreCase("RU") ? "Неверный логин или пароль" : "Invalid credentials"));
        }
    }

    public void close() {
        onDisconnect();
        channel.close();
    }
}
