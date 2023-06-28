package com.panda.game.core.netty.handler;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.core.cmd.CommandManager;
import com.panda.game.core.netty.NettyConstants;
import com.panda.game.core.session.Session;
import com.panda.game.core.session.SessionManager;
import com.panda.game.proto.PacketPb;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;

public class LogicHandler extends PacketInboundHandler {

    private static final Logger log = LoggerFactory.getLogger(LogicHandler.class);

    @Override
    public void channelRead(Channel channel, PacketPb.Pkg pkg) {
        CommandManager.getInstance().handle(channel, pkg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("un-catch exception in netty", cause);
    }

}
