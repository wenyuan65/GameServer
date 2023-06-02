package com.panda.game.core.netty.initializer;

import com.panda.game.common.config.Configuration;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
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
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.security.KeyStore;

public class HttpsChannelInitializer extends NettyServerInitializer {

	private static final Logger log = LoggerFactory.getLogger(HttpsChannelInitializer.class);

	private SSLContext sslContext;

	private Class<? extends PacketInboundHandler> handlerClazz;

	public HttpsChannelInitializer(ServerConfig config, Class<? extends PacketInboundHandler> handlerClazz) {
		super(config);

		this.handlerClazz = handlerClazz;
	}

	@Override
	public void initBootstrap(ServerBootstrap bootstrap) {
		try {
			sslContext = createSSLContext(config);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT);
		bootstrap.childOption(ChannelOption.SO_BACKLOG, 1024);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);

		bootstrap.childHandler(this);
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline cp = ch.pipeline();
		
		SSLEngine engine = sslContext.createSSLEngine();
		engine.setUseClientMode(false);
		engine.setNeedClientAuth(config.isNeedClientAuth());

		Constructor<? extends PacketInboundHandler> constructor = handlerClazz.getDeclaredConstructor(Boolean.TYPE);
		PacketInboundHandler handler = constructor.newInstance(config.isUseSession());
		
		cp.addLast(new SslHandler(engine));
		cp.addLast(new HttpResponseEncoder());
		cp.addLast(new HttpRequestDecoder());
		cp.addLast(new HttpObjectAggregator(1024 * 1024));
		cp.addLast(new ChunkedWriteHandler());
		cp.addLast(handler);
	}

	/**
	 * 读取https端口的sslContext
	 * @param config
	 * @return
	 * @throws Exception
	 */
	private SSLContext createSSLContext(ServerConfig config) throws Exception {
		String protocol = Configuration.getProperty("ssl.protocol");
		String keyAlgorithm = Configuration.getProperty("ssl.key.algorithm");
		String keyType = Configuration.getProperty("ssl.key.type");
		String keyStore = Configuration.getProperty("ssl.key.store");
		String keyStorePassword = Configuration.getProperty("ssl.key.storePassword");
		String trustAlgorithm = Configuration.getProperty("ssl.trust.algorithm");
		String trustType = Configuration.getProperty("ssl.trust.type");
		String trustStore = Configuration.getProperty("ssl.trust.store");
		String trustStorePassword = Configuration.getProperty("ssl.trust.storePassword");
		String keyKeyPassword = Configuration.getProperty("ssl.key.keyPassword");
		String needClientAuth = Configuration.getProperty("ssl.needClientAuth");

		log.info("{} ==> {}", "protocol", protocol);
		log.info("{} ==> {}", "keyAlgorithm", keyAlgorithm);
		log.info("{} ==> {}", "keyType", keyType);
		log.info("{} ==> {}", "keyStore", keyStore);
		log.info("{} ==> {}", "keyStorePassword", keyStorePassword);
		log.info("{} ==> {}", "trustAlgorithm", trustAlgorithm);
		log.info("{} ==> {}", "trustType", trustType);
		log.info("{} ==> {}", "trustStore", trustStore);
		log.info("{} ==> {}", "trustStorePassword", trustStorePassword);
		log.info("{} ==> {}", "keyKeyPassword", keyKeyPassword);
		log.info("{} ==> {}", "needClientAuth", needClientAuth);

		SSLContext ctx = SSLContext.getInstance(protocol);
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(keyAlgorithm);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(trustAlgorithm);
		KeyStore ks = KeyStore.getInstance(keyType);
		KeyStore tks = KeyStore.getInstance(trustType);
		ks.load(new FileInputStream(keyStore), keyStorePassword.toCharArray());
		tks.load(new FileInputStream(trustStore), trustStorePassword.toCharArray());
		kmf.init(ks, keyKeyPassword.toCharArray());
		tmf.init(tks);
		ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		config.setNeedClientAuth("true".equalsIgnoreCase(needClientAuth));

		return ctx;
	}

}
