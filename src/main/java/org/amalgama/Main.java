package org.amalgama;

import org.amalgama.network.Server;
import org.amalgama.utils.Checker;
import org.amalgama.utils.ServerLogger;
import org.json.simple.JSONObject;

public class Main {
    public static void main(String[] args) {
        ServerLogger.log("Starting server...");
        Checker.runChecks();
        Server server = new Server(13050);
        server.start();
        JSONObject json = new JSONObject();
    }
}