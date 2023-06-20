package com.panda.game.core.rpc;

import com.panda.game.core.netty.NettyClientInitializer;
import com.panda.game.proto.PacketPb;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

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
        ChannelPipeline cp = socketChannel.pipeline();

        cp.addLast(new LengthFieldPrepender(4));
        cp.addLast(new LengthFieldBasedFrameDecoder(10485760, 0, 4, 0, 4));
        cp.addLast(new ProtobufDecoder(PacketPb.Pkg.getDefaultInstance()));
        cp.addLast(new ProtobufEncoder());

        cp.addLast(new RpcClientHandler());
    }
}
