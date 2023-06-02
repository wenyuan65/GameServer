package com.panda.game.core.netty;


import com.panda.game.core.common.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public abstract class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    protected ServerConfig config;

    public NettyServerInitializer(ServerConfig config) {
        this.config = config;
    }

    public abstract void initBootstrap(ServerBootstrap bootstrap);

}
