package org.amalgama.network.web;

import org.amalgama.network.ConnectionController;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class WebSocketServer {
    private final ServerBootstrap serverBootstrap;
    private final String address;
    private final int port;

    public WebSocketServer(String address, int port, ConnectionController controller) {
        this.address = address;
        this.port = port;

        ExecutorService bossExec = new OrderedMemoryAwareThreadPoolExecutor(1, 400000000, 2000000000L, 60, TimeUnit.SECONDS);
        ExecutorService ioExec = new OrderedMemoryAwareThreadPoolExecutor(4, 400000000, 2000000000L, 60, TimeUnit.SECONDS);

        serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(bossExec, ioExec, 4));
        serverBootstrap.setOption("backlog", 500);
        serverBootstrap.setOption("connectTimeoutMillis", 10000);
        serverBootstrap.setOption("child.tcpNoDelay", true);
        serverBootstrap.setOption("child.keepAlive", true);
        serverBootstrap.setPipelineFactory(new WebSocketPipelineFactory(controller));
    }

    public void start() {
        if (address != null) {
            serverBootstrap.bind(new InetSocketAddress(address, port));
        } else {
            serverBootstrap.bind(new InetSocketAddress(port));
        }

        Logger.getGlobal().info("WebServer available on " +
                (address == null ? "0.0.0.0" : address) + ":" + port);
    }

    public void stop() {
        serverBootstrap.releaseExternalResources();
    }
}
