package com.panda.game.core.rpc;

public class RpcResponse {
	
	private int requestId;
	private int errorCode;
	private byte[] result;

	public RpcResponse() {
	}

	public RpcResponse(int requestId) {
		this.requestId = requestId;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public byte[] getResult() {
		return result;
	}

	public void setResult(byte[] result) {
		this.result = result;
	}
}
