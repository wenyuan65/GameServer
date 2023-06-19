package com.panda.game.core.rpc;

public interface Callback {

	public void invoke(RpcRequest request, RpcResponse response);
	
}
