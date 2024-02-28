package org.amalgama.network;

import org.jboss.netty.channel.*;

import java.util.logging.Logger;

public class ConnectionHandler extends SimpleChannelUpstreamHandler {
    private static ConnectionHandler instance;
    private final ConnectionController controller = new ConnectionController();
    private ConnectionHandler() { }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        controller.acceptMessage(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Channel channel = ctx.getChannel();
        if (channel.isOpen())
            channel.close();
        Logger.getGlobal().warning("Exception caught: " + e.getCause());
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Channel channel = ctx.getChannel();
        controller.newConnection(ctx);
        Logger.getGlobal().info("New connection: '" + channel.getRemoteAddress() + "' #" + channel.getId());
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Channel channel = ctx.getChannel();
        controller.disconnect(ctx);
        Logger.getGlobal().info("Disconnect: '" + channel.getRemoteAddress() + "' #" + channel.getId());
    }

    public static ConnectionHandler getInstance() {
        if (instance == null)
            instance = new ConnectionHandler();
        return instance;
    }

    public ConnectionController getController() {
        return controller;
    }
}
