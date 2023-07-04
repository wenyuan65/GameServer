package com.panda.game.core.netty.handler;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.core.cmd.CommandManager;
import com.panda.game.proto.PacketPb;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class PacketCommandHandler extends PacketInboundHandler {

    private static final Logger log = LoggerFactory.getLogger(PacketCommandHandler.class);

    @Override
    public void channelRead(Channel channel, PacketPb.Pkg pkg) {
        CommandManager.getInstance().handle(channel, pkg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("un-catch exception in netty", cause);
    }

}
