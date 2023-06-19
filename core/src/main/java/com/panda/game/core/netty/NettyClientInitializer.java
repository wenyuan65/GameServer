package com.panda.game.core.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public abstract class NettyClientInitializer extends ChannelInitializer<SocketChannel> {


    public abstract void initBootstrap(Bootstrap bootstrap);
}
