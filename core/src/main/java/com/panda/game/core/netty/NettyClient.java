package com.panda.game.core.netty;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class NettyClient {

	private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
	
	/** 服务器名称 */
	private String name;
	/** netty配置 */
	private NettyClientConfig config;
	/** channel初始化配置 */
	private NettyClientInitializer initializer;
	
	/** netty启动类 */
	private Bootstrap bootstrap = new Bootstrap(); 
	/** netty线程 */
	private EventLoopGroup group;
	
	public NettyClient(String name, NettyClientConfig config, NettyClientInitializer initializer) {
		this.name = name;
		this.config = config;
		this.initializer = initializer;
	}
	
	public void init() {
		group = config.isEpoll() ? new EpollEventLoopGroup(config.getEventGroupNum())  : new NioEventLoopGroup(config.getEventGroupNum());
		bootstrap.group(group);
		bootstrap.channel(config.isEpoll() ? EpollSocketChannel.class: NioSocketChannel.class);
		bootstrap.option(ChannelOption.ALLOCATOR, config.isUsePool() ? PooledByteBufAllocator.DEFAULT  : UnpooledByteBufAllocator.DEFAULT);

		initializer.initBootstrap(bootstrap);
	}
	
	public Channel connect(String host, int port, int timeoutMs) throws Exception {
		return connect(new InetSocketAddress(host, port), timeoutMs);
	}

	public Channel connect(SocketAddress socketAddress, int timeoutMs) throws Exception {
		timeoutMs = Math.max(timeoutMs, 1000);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutMs);

		ChannelFuture future = bootstrap.connect(socketAddress).sync();

		if (future.isSuccess()) {
			logger.info("create connection success: {} ==> {}", name, socketAddress.toString());
			return future.channel();
		}

		String connectionMsg = name + " ==> " + socketAddress;
		if (!future.isDone()) {
			String errMsg = String.format("create connection timeout: " + connectionMsg);
			logger.warn(errMsg);
			throw new Exception(errMsg);
		}
		if (future.isCancelled()) {
			String errMsg = String.format("create connection cancelled: " + connectionMsg);
			logger.warn(errMsg);
			throw new Exception(errMsg);
		}

		String errMsg = String.format("create connection error: " + connectionMsg);
		logger.warn(errMsg);
		throw new Exception(errMsg, future.cause());
	}
	
}
