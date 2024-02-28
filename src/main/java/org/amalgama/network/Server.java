package org.amalgama.network;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Server implements Destroyable {
    private final ServerBootstrap networkServer;
    private final String address;
    private final int port;

    public Server(int port) {
        this.address = null;
        this.port = port;
        networkServer = initServerBootstrap();
    }

    public Server(String address, int port) {
        this.address = address;
        this.port = port;
        networkServer = initServerBootstrap();
    }

    private ServerBootstrap initServerBootstrap() {
        final ServerBootstrap networkServer;
        ExecutorService bossExec = new OrderedMemoryAwareThreadPoolExecutor(1, 400000000, 2000000000, 60, TimeUnit.SECONDS);
        ExecutorService ioExec = new OrderedMemoryAwareThreadPoolExecutor(4, 400000000, 2000000000, 60, TimeUnit.SECONDS);
        networkServer = new ServerBootstrap(new NioServerSocketChannelFactory(bossExec, ioExec, 4));
        networkServer.setOption("backlog", 500);
        networkServer.setOption("connectTimeoutMillis", 10000);
        networkServer.setOption("child.tcpNpDelay", true);
        networkServer.setOption("child.keepAlive", true);
        networkServer.setPipelineFactory(new ServerPipelineFactory());
        return networkServer;
    }

    public void start() {
        if (address != null) networkServer.bind(new InetSocketAddress(address, port));
        else networkServer.bind(new InetSocketAddress(port));
        Logger.getGlobal().info("Server available on " + (address == null ? "127.0.0.1" : address) + ":" + port);
    }

    @Override
    public void destroy() throws DestroyFailedException {
        networkServer.releaseExternalResources();
        Destroyable.super.destroy();
    }
}
