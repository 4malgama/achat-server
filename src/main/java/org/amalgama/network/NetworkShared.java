package org.amalgama.network;

public class NetworkShared {
    private static ConnectionController controller = null;

    public static void setController(ConnectionController controller) {
        NetworkShared.controller = controller;
    }

    public static ConnectionController getController() {
        return controller;
    }
}
