package com.panda.game.core.rpc.connection;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.core.netty.NettyClient;
import com.panda.game.core.netty.NettyClientConfig;
import com.panda.game.core.netty.NettyClientInitializer;
import io.netty.channel.Channel;

public class DefaultConnectionFactory implements ConnectionFactory {
	
	public static final Logger log = LoggerFactory.getLogger(ConnectionFactory.class);

	private NettyClientConfig config;
	private NettyClientInitializer initializer;
	protected NettyClient client;
	
	public DefaultConnectionFactory(NettyClientConfig config, NettyClientInitializer initializer) {
		this.config = config;
		this.initializer = initializer;
	}

	@Override
	public void init() {
		client = new NettyClient(config.getName(), config, initializer);
		client.init();
	}

	@Override
	public Connection createConnection(String ip, int port, int timeoutMs) throws Exception {
		Channel channel = doCreateConnection(ip, port, timeoutMs);
		return new DefaultConnection(channel);
	}
	
	private Channel doCreateConnection(String ip, int port, int timeoutMs) throws Exception {
		return client.connect(ip, port, timeoutMs);
	}

}
