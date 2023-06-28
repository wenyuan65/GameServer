package com.panda.game.core.netty;

public class NettyClientConfig {

	private String name = "NettyClient";

	private boolean epoll = false;
	
	private boolean usePool = false;
	
	private int eventGroupNum = 8;
	
	// rpc请求超时时间
	private long timeout = 5000;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEpoll() {
		return epoll;
	}

	public void setEpoll(boolean epoll) {
		this.epoll = epoll;
	}

	public boolean isUsePool() {
		return usePool;
	}

	public void setUsePool(boolean usePool) {
		this.usePool = usePool;
	}

	public int getEventGroupNum() {
		return eventGroupNum;
	}

	public void setEventGroupNum(int eventGroupNum) {
		this.eventGroupNum = eventGroupNum;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
}
