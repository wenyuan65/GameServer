package com.panda.game.core.rpc;

@FunctionalInterface
public interface Callback {

	void execute(RpcRequest request, RpcResponse response);
	
}
