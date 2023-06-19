package com.panda.game.core.netty;

public class NettyServerConfig {
	
	private int bossEventLoopNum = 1;
	
	private int workerEventLoopNum = 8;
	
	private int port;
	
	public int getBossEventLoopNum() {
		return bossEventLoopNum;
	}

	public void setBossEventLoopNum(int bossEventLoopNum) {
		this.bossEventLoopNum = bossEventLoopNum;
	}

	public int getWorkerEventLoopNum() {
		return workerEventLoopNum;
	}

	public void setWorkerEventLoopNum(int workerEventLoopNum) {
		this.workerEventLoopNum = workerEventLoopNum;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}


}



