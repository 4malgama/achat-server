package org.amalgama.utils;

public class ServerLogger {

    public static void log(String message) {
        System.out.println("[SERVER]: " + message);
    }

    public static void warning(String message) {
        System.out.println("[SERVER] [WARNING]: " + message);
    }

    public static void error(String message) {
        System.err.println("[SERVER] [ERROR]: " + message);
    }
}
