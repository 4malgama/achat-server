package org.amalgama.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {
    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }

    public static String getFileNameWithoutExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, dotIndex);
    }

    public static String getFileName(String path) {
        int lastIndex = path.lastIndexOf('/');
        if (lastIndex == -1) {
            return path;
        }
        return path.substring(lastIndex + 1);
    }

    public static String getAppDataDir() {
        String appDataDir = System.getenv("APPDATA");
        if (appDataDir == null) {
            appDataDir = System.getProperty("user.home");
        }
        return appDataDir;
    }

    public static byte[] readFile(String path) {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean compareFileWithHash(String hash, String path) {
        return hash.equals(calculateHash(path));
    }

    public static String calculateHash(String path) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(Files.readAllBytes(Paths.get(path)));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(NoSuchAlgorithmException | IOException e) {
            return null;
        }
    }

    public static void makePaths() {
        try {
            Files.createDirectories(Paths.get(getAppDataDir() + "\\achat-server\\avatars\\"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String filePath, byte[] bytes) {
        try {
            Files.write(Paths.get(filePath), bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long getFileSize(String path) {
        try {
            return Files.size(Paths.get(path));
        } catch (IOException e) {
            return 0;
        }
    }

    public static void createDirectoryIfNotExists(String dir) {
        try {
            Files.createDirectories(Paths.get(dir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
