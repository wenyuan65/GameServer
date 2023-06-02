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

    public LogicHandler(boolean useSession) {
        super(useSession);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!useSession) {
            return;
        }

        Channel channel = ctx.channel();
        boolean hasSession = channel.hasAttr(NettyConstants.Session_Key);
        if (!hasSession) {
            Session session = SessionManager.getInstance().getSession("", true);
            Attribute<String> attr = channel.attr(NettyConstants.Session_Key);
            attr.setIfAbsent(session.getSessionId());

            log.info("netty channel active, session: {}", session.getSessionId());
        }
    }

    @Override
    public void channelRead(Channel channel, PacketPb.Pkg pkg) {
        CommandManager.getInstance().handle(channel, pkg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("not catch exception in netty", cause);
    }

}
