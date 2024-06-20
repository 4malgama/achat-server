package org.amalgama.servecies;

import org.amalgama.database.entities.Attachment;
import org.amalgama.utils.FileUtils;

public class CacheService {
    private static CacheService instance;

    private String serverPath;

    private CacheService() {
        serverPath = FileUtils.getAppDataDir() + "\\achat-server\\";
    }
    public static CacheService getInstance() {
        if (instance == null) {
            instance = new CacheService();
        }
        return instance;
    }

    public byte[] getUserAvatar(long uid) {
        String dir = serverPath + "avatars\\";
        FileUtils.createDirectoryIfNotExists(dir);
        return FileUtils.readFile(dir + uid + ".jpg");
    }

    public void saveAttachment(long chatId, String attachmentName, byte[] bytes) {
        String dir = serverPath + "attachments\\" + chatId + "\\";
        FileUtils.createDirectoryIfNotExists(dir);
        FileUtils.writeFile(dir + attachmentName, bytes);
    }

    public long getAttachmentSize(long chatId, Attachment a) {
        String dir = serverPath + "attachments\\" + chatId + "\\";
        FileUtils.createDirectoryIfNotExists(dir);
        return FileUtils.getFileSize(dir + a.getId() + "_" + a.getName());
    }

    /**
     * Reads an attachment from the specified chat ID.
     *
     * @param  chatId   the ID of the chat
     * @param  a        the Attachment object to read
     * @return          the byte array of the attachment data or null if not found
     */
    public byte[] readAttachment(long chatId, Attachment a) {
        String dir = serverPath + "attachments\\" + chatId + "\\";
        return FileUtils.readFile(dir + a.getId() + "_" + a.getName());
    }
}
