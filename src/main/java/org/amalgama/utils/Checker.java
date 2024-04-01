package org.amalgama.utils;

import org.amalgama.database.ConnectionChecker;

public class Checker {
    private static void checkDatabaseConnection() {
        ServerLogger.log("Checking database connection...");
        ConnectionChecker.checkConnection();
    }
    public static void runChecks() {
        checkDatabaseConnection();
    }
}
