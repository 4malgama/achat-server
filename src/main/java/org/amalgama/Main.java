package org.amalgama;

import org.amalgama.network.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(13050);
        server.start();
    }
}