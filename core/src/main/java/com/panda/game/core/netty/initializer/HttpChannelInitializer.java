package com.panda.game.core.netty.initializer;

import com.panda.game.core.common.ServerConfig;
import com.panda.game.core.netty.NettyServerInitializer;
import com.panda.game.core.netty.handler.PacketInboundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.lang.reflect.Constructor;

public class HttpChannelInitializer extends NettyServerInitializer {

	private Class<? extends PacketInboundHandler> handlerClazz;

	public HttpChannelInitializer(ServerConfig config, Class<? extends PacketInboundHandler> handlerClazz) {
		super(config);
		this.handlerClazz = handlerClazz;
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
		Constructor<? extends PacketInboundHandler> constructor = handlerClazz.getDeclaredConstructor(Boolean.TYPE);
		PacketInboundHandler handler = constructor.newInstance(config.isUseSession());

		ChannelPipeline cp = ch.pipeline();
		cp.addLast(new HttpResponseEncoder());
		cp.addLast(new HttpRequestDecoder());
		cp.addLast(new HttpObjectAggregator(1024 * 1024));
		cp.addLast(new ChunkedWriteHandler());
		cp.addLast(handler);
	}

}
