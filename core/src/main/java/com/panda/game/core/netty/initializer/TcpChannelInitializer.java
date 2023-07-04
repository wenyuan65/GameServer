package com.panda.game.core.netty.initializer;

import com.panda.game.core.common.ServerConfig;
import com.panda.game.core.netty.NettyServerInitializer;
import com.panda.game.core.netty.handler.PacketCommandHandler;
import com.panda.game.proto.PacketPb;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class TcpChannelInitializer extends NettyServerInitializer {

	public TcpChannelInitializer(ServerConfig config) {
		super(config);
	}

	@Override
	public void initBootstrap(ServerBootstrap bootstrap) {
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT);
		bootstrap.childOption(ChannelOption.SO_BACKLOG, 1024);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);

		bootstrap.childHandler(this);
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline cp = ch.pipeline();

		cp.addLast(new LengthFieldPrepender(4));
		cp.addLast(new LengthFieldBasedFrameDecoder(10485760, 0, 4, 0, 4));
		cp.addLast(new ProtobufDecoder(PacketPb.Pkg.getDefaultInstance()));
		cp.addLast(new ProtobufEncoder());
		cp.addLast(new PacketCommandHandler());
	}

}
