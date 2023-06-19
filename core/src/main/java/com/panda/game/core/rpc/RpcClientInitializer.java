package com.panda.game.core.rpc;

import com.panda.game.core.netty.NettyClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class RpcClientInitializer extends NettyClientInitializer {

    public RpcClientInitializer() {
    }

    @Override
    public void initBootstrap(Bootstrap bootstrap) {
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
//        bootstrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT);
//        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);

        bootstrap.handler(this);
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast("rpcClientHandler", new RpcClientHandler());
    }
}
