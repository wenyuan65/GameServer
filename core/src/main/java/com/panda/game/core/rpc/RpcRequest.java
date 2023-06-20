package com.panda.game.core.rpc;

import com.panda.game.proto.PacketPb;

public class RpcRequest {

	/** 请求id */
	private int requestId;
	// 数据包
	private PacketPb.Pkg pkg;
	
	/** rpc访问主机ip */
	private String host;
	/** rpc访问主机端口号 */
	private int port;
	
	public RpcRequest() {}

	public RpcRequest(int requestId, PacketPb.Pkg pkg) {
		this.requestId = requestId;
		this.pkg = pkg;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public PacketPb.Pkg getPkg() {
		return pkg;
	}

	public void setPkg(PacketPb.Pkg pkg) {
		this.pkg = pkg;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
