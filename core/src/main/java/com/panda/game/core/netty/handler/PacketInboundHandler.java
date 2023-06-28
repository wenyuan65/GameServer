package com.panda.game.core.netty.handler;

import com.panda.game.proto.PacketPb;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class PacketInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.channelRead(ctx.channel(), (PacketPb.Pkg)msg);
    }

    public abstract void channelRead(Channel channel, PacketPb.Pkg pkg);
}
