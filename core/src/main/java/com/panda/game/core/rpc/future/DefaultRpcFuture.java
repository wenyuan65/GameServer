package com.panda.game.core.rpc.future;

import com.panda.game.core.rpc.Callback;
import com.panda.game.core.rpc.RpcRequest;
import com.panda.game.core.rpc.RpcResponse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DefaultRpcFuture implements RpcFuture {
	
	private CountDownLatch countDownLatch = new CountDownLatch(1);
	private RpcRequest request;
	private RpcResponse response;
	private Callback callback;
	
	public DefaultRpcFuture(RpcRequest request, RpcResponse response, Callback callback) {
		this.request = request;
		this.response = response;
		this.callback = callback;
	}

	@Override
	public void waitResponse(long timeoutMs) throws InterruptedException {
		countDownLatch.await(timeoutMs, TimeUnit.MILLISECONDS);
	}

	@Override
	public void waitResponse() throws InterruptedException {
		countDownLatch.await();
	}

	@Override
	public boolean isDone() {
		return countDownLatch.getCount() <= 0;
	}

	@Override
	public void cancel() {
		
	}

	@Override
	public void putResponse(byte[] result) {
		if (response != null) {
			response.setResult(result);
		}
		countDownLatch.countDown();
	}

	@Override
	public int getRequestId() {
		return request.getRequestId();
	}

	@Override
	public void executeCallback() {
		if (callback != null) {
			callback.execute(request, response);
		}
	}

	@Override
	public void setErrorCode(int errorCode) {
		if (response != null) {
			response.setErrorCode(errorCode);
		}
	}

	@Override
	public int getErrorCode() {
		if (response != null) {
			return response.getErrorCode();
		}

		return 0;
	}
}
