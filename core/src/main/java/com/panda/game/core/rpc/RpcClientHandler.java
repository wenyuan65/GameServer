package com.panda.game.core.rpc;

import com.panda.game.core.netty.NettyConstants;
import com.panda.game.core.rpc.connection.Connection;
import com.panda.game.core.rpc.connection.DefaultConnection;
import com.panda.game.core.rpc.future.RpcFuture;
import com.panda.game.proto.PacketPb;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RpcClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof PacketPb.Pkg) {
			PacketPb.Pkg pkg = (PacketPb.Pkg) msg;
			Connection connection = ctx.channel().attr(NettyConstants.CONNECTION).get();
			if (connection != null) {
				// 这是一个临时的RpcResponse
				RpcResponse response = new RpcResponse();
				response.setRequestId(pkg.getRequestId());
				response.setErrorCode(pkg.getErrorCode());
				response.setResult(pkg.getBody().toByteArray());

				connection.handleResponse(response);
			}
		}
	}
	
}
