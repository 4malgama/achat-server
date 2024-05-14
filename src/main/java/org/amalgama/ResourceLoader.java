package org.amalgama;

import java.io.IOException;
import java.io.InputStream;

public class ResourceLoader {
    public static InputStream getResourceAsStream(String filename) {
        ClassLoader classLoader = ResourceLoader.class.getClassLoader();
        return classLoader.getResourceAsStream(filename);
    }

    public static String getResourceAsString(String filename) {
        try (InputStream inputStream = getResourceAsStream(filename)) {
            if (inputStream != null) {
                return new String(inputStream.readAllBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
