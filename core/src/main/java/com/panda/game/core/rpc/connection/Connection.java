package com.panda.game.core.rpc.connection;

import com.panda.game.core.rpc.Callback;
import com.panda.game.core.rpc.RpcRequest;
import com.panda.game.core.rpc.RpcResponse;
import com.panda.game.core.rpc.future.InvokeFuture;

public interface Connection {
	
	public InvokeFuture sendRequest(RpcRequest request, RpcResponse response, Callback callback);
	
	public void handleResponse(RpcResponse response);
	
	public InvokeFuture getInvokeFuture(int id);
	
	public void addInvokeFuture(InvokeFuture future);
	
	public void removeInvokeFuture(InvokeFuture future);
	
	public boolean checkActive();
	
	public void close();
	
}
