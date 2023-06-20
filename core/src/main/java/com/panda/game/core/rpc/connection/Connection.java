package com.panda.game.core.rpc.connection;

import com.panda.game.core.rpc.Callback;
import com.panda.game.core.rpc.RpcRequest;
import com.panda.game.core.rpc.RpcResponse;
import com.panda.game.core.rpc.future.RpcFuture;

public interface Connection {
	
	RpcFuture sendRequest(RpcRequest request, RpcResponse response, Callback callback);
	
	void handleResponse(RpcResponse response);
	
	RpcFuture getFuture(int id);
	
	void addFuture(RpcFuture future);

	void removeFuture(RpcFuture future);
	
	boolean checkActive();

	String getRemoteAddress();
	
	void close();
	
}
