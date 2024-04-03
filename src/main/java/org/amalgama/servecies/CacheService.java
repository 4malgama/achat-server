package org.amalgama.servecies;

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
        return FileUtils.readFile(dir + uid + ".jpg");
    }
}
