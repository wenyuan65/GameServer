package com.panda.game.core.rpc.connection;

import com.panda.game.core.netty.NettyConstants;
import com.panda.game.core.rpc.Callback;
import com.panda.game.core.rpc.RpcRequest;
import com.panda.game.core.rpc.RpcResponse;
import com.panda.game.core.rpc.future.RpcFuture;
import com.panda.game.proto.CmdPb;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class DefaultConnection extends AbstractConnection {
	
	private Channel channel;

	private String address;
	
	public DefaultConnection(Channel channel, String address) {
		this.channel = channel;
		this.channel.attr(NettyConstants.CONNECTION).set(this);
		this.address = address;
	}

	@Override
	public RpcFuture sendRequest(RpcRequest request, RpcResponse response, Callback callback) {
		final RpcFuture future = createInvokeFuture(request, response, callback);
		addFuture(future);
		try {
			channel.writeAndFlush(request.getPkg()).addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture f) throws Exception {
					if (!f.isSuccess()) {
						future.setErrorCode(CmdPb.ErrorCode.RpcSendFailed_VALUE);
						future.putResponse(null);

						removeFuture(future);
					}
				}
			});
		} catch (Throwable e) {
			log.error("发送rpc请求异常", e);
			future.setErrorCode(CmdPb.ErrorCode.RpcSendError_VALUE);
			future.putResponse(null);

			removeFuture(future);
		}
		
		return future;
	}
	
	@Override
	public boolean checkActive() {
		return channel.isActive();
	}

	@Override
	public String getRemoteAddress() {
		return address;
	}

	@Override
	public void close() {
		if (channel != null) {
			channel.attr(NettyConstants.CONNECTION).set(null);
			channel.close();
		}
	}

}
