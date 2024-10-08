package org.amalgama.database;

import org.amalgama.database.dao.*;
import org.amalgama.database.entities.Attachment;
import org.amalgama.database.entities.Chat;
import org.amalgama.database.entities.Message;
import org.amalgama.database.entities.User;
import org.json.simple.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class DBService {
    private static DBService instance;

    static {
        instance = new DBService();
    }

    private DBService() {}

    public static DBService getInstance() {
        return instance;
    }

    public User registerUser(String login, String password) {
        JSONObject jsonPrivateSettings = new JSONObject();
        jsonPrivateSettings.put("see_avatar", "all");
        jsonPrivateSettings.put("see_description", "all");
        jsonPrivateSettings.put("see_post", "all");
        jsonPrivateSettings.put("send_friend_request", "all");
        jsonPrivateSettings.put("see_online_status", "all");
        jsonPrivateSettings.put("send_messages", "all");
        jsonPrivateSettings.put("can_invite_groups", "all");
        jsonPrivateSettings.put("hide_forwards", false);

        JSONObject jsonSettings = new JSONObject();
        jsonSettings.put("private_settings", jsonPrivateSettings);
        jsonSettings.put("admin", false);
        jsonSettings.put("superadmin", false);

        User user = new User();
        user.setLogin(login);
        user.setPassword(hashPassword(password));
        user.setScore(0L);
        user.setBalance(0L);
        user.setCash(0L);
        user.setPermission(PermissionDAO.getPermission("user"));
        user.setSettingsData(jsonSettings.toJSONString());
        user.setRegisterTimestamp(System.currentTimeMillis());
        user.setLastLoginTimestamp(System.currentTimeMillis());

        UserDAO.addUser(user);
        return user;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder(2 * hash.length);

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRegistered(String login) {
        return UserDAO.getUser(login) != null;
    }

    public boolean validateCredentials(String login, String password) {
        return UserDAO.getUser(login, password) != null;
    }

    public void updateEmail(User user, String email) {
        user.setEmail(email);
        UserDAO.updateUser(user);
    }

    public List<Chat> getChats(User user) {
        return ChatDAO.getChats(user);
    }

    public boolean existsChat(User user, long chatId) {
        return ChatDAO.existsChat(user, chatId);
    }

    public Chat getChat(User user, long chatId) {
        return ChatDAO.getChatFromUser(user, chatId);
    }

    public List<Message> getMessages(Chat chat) {
        return MessageDAO.getMessages(chat);
    }

    public void addMessage(Message message) {
        MessageDAO.addMessage(message);
    }

    public void addAttachments(List<Attachment> attachments) {
        AttachmentDAO.addAttachments(attachments);
    }

    public void addAttachment(Attachment a) {
        AttachmentDAO.addAttachment(a);
    }

    public List<Attachment> getAttachments(Message message) {
        return AttachmentDAO.getAttachments(message);
    }

    public List<User> getAllUsersByLogin(String login) {
        return UserDAO.getUsersByLogin(login);
    }

    public Attachment getAttachment(long fileId) {
        return AttachmentDAO.getAttachment(fileId);
    }

    public Chat getChat(User user1, User user2) {
        return ChatDAO.getChatBetweenUsers(user1, user2);
    }

    public User getUser(long userId) {
        return UserDAO.getUser(userId);
    }

    public void addChat(Chat chat) {
        ChatDAO.addChat(chat);
    }
}
