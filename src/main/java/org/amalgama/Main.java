package org.amalgama;

import org.amalgama.network.Server;
import org.amalgama.utils.Checker;
import org.amalgama.utils.FileUtils;
import org.amalgama.utils.ServerLogger;

public class Main {
    public static void main(String[] args) {
        ServerLogger.log("Starting server...");
        Checker.runChecks();
        FileUtils.makePaths();
        Server server = new Server(13050);
        server.start();
    }
}