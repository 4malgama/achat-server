package org.amalgama;

import org.amalgama.network.ConnectionController;
import org.amalgama.network.NetworkShared;
import org.amalgama.network.Server;
import org.amalgama.network.web.WebSocketServer;
import org.amalgama.utils.Checker;
import org.amalgama.utils.FileUtils;
import org.amalgama.utils.ServerLogger;

public class Main {
    public static void main(String[] args) {
        ServerLogger.log("Starting server...");
        Checker.runChecks();
        FileUtils.makePaths();

        ConnectionController controller = new ConnectionController();
        NetworkShared.setController(controller);

        Server server = new Server(13050, controller);
        server.start();

        WebSocketServer webServer = new WebSocketServer(null, 8080, controller);
        webServer.start();
    }
}