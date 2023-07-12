package com.panda.game.core.netty.handler;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.protobuf.GeneratedMessageV3;
import com.panda.game.common.concrrent.ServerThreadManager;
import com.panda.game.common.constants.NodeType;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.proto.PbUtil;
import com.panda.game.core.cmd.CommandManager;
import com.panda.game.core.nacos.NodeManager;
import com.panda.game.core.netty.NettyConstants;
import com.panda.game.core.proto.ProtoManager;
import com.panda.game.core.rpc.RpcManager;
import com.panda.game.core.session.Session;
import com.panda.game.core.session.SessionManager;
import com.panda.game.proto.CmdPb;
import com.panda.game.proto.PacketPb;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;

public class GatewayHandler extends PacketInboundHandler {

    private static final Logger log = LoggerFactory.getLogger(PacketCommandHandler.class);

    public GatewayHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
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
        if (pkg.getNodeType() == NodeType.Gateway.getType()) {
            CommandManager.getInstance().handle(channel, pkg);
        } else {
            ServerThreadManager.getInstance().runAsync(() -> handlePacket(channel, pkg));
        }
    }

    private void handlePacket(Channel channel, PacketPb.Pkg pkg) {
        NodeType nodeType = NodeType.getNodeType(pkg.getNodeType());
        Instance node = NodeManager.getInstance().getNode(nodeType, pkg.getNodeId());

        GeneratedMessageV3 result = RpcManager.getInstance().sendSync(node, pkg);
        if (result == null) {
            log.info("转发数据包异常，{}，{}", node.getInstanceId(), CmdPb.Cmd.forNumber(pkg.getCmd()).name());
            return;
        }

        int cmd = ProtoManager.getInstance().getRsCmd(pkg.getCmd());
        PacketPb.Pkg.Builder resp = PbUtil.createPkg(cmd, pkg.getPlayerId(), result);
        resp.setRequestId(pkg.getRequestId());

        channel.writeAndFlush(resp.build());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("un-catch exception in netty", cause);
    }

}
