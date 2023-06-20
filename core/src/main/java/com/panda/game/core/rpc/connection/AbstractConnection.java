package com.panda.game.core.rpc.connection;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.core.rpc.Callback;
import com.panda.game.core.rpc.RpcRequest;
import com.panda.game.core.rpc.RpcResponse;
import com.panda.game.core.rpc.future.DefaultRpcFuture;
import com.panda.game.core.rpc.future.RpcFuture;
import com.panda.game.proto.CmdPb;

import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractConnection implements Connection {

	protected static final Logger log = LoggerFactory.getLogger(Connection.class);
	
	private ConcurrentHashMap<Integer, RpcFuture> invokeFutureMap = new ConcurrentHashMap<>();
	
	protected RpcFuture createInvokeFuture(RpcRequest request, RpcResponse response, Callback callback) {
		return new DefaultRpcFuture(request, response, callback);
	}
	
	@Override
	public void handleResponse(RpcResponse response) {
		int requestId = response.getRequestId();
		
		RpcFuture future = getFuture(requestId);
		if (future != null) {
			future.setErrorCode(response.getErrorCode());
			future.putResponse(response.getResult());
			// 回调
			future.executeCallback();
		} else {
			log.info("#RPC#{}#执行超时或异常", requestId);
		}
	}
	
	@Override
	public RpcFuture getFuture(int id) {
		return invokeFutureMap.get(id);
	}
	
	@Override
	public void addFuture(RpcFuture future) {
		invokeFutureMap.put(future.getRequestId(), future);
	}
	
	@Override
	public void removeFuture(RpcFuture future) {
		invokeFutureMap.remove(future.getRequestId());
	}

}
