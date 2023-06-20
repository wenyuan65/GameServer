package com.panda.game.core.rpc.future;

public interface RpcFuture {
	
	void waitResponse(long timeoutMs) throws InterruptedException;
	
	void waitResponse() throws InterruptedException;
	
	boolean isDone();
	
	void cancel();
	
	void putResponse(byte[] result);
	
	int getRequestId();
	
	void executeCallback();
	
	void setErrorCode(int errorCode);
	
	int getErrorCode();

}
