package com.panda.game.gateway.login;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.panda.game.common.constants.NodeType;
import com.panda.game.core.cmd.annotation.Action;
import com.panda.game.core.cmd.annotation.Command;
import com.panda.game.core.common.BaseAction;
import com.panda.game.core.nacos.NodeManager;
import com.panda.game.core.rpc.RpcManager;
import com.panda.game.proto.CmdPb;
import com.panda.game.proto.LoginPb.*;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

@Action
public class UserAction extends BaseAction {

    @Command(CmdPb.Cmd.GatewayLoginRq_VALUE)
    public void login(GatewayLoginRq rq, Channel channel) {
        int nodeId = rq.getNodeId();
        Instance node = NodeManager.getInstance().getNode(NodeType.Logic, nodeId);
        if (node == null) {
            sendError(channel, CmdPb.Cmd.GatewayLoginRs_VALUE, CmdPb.ErrorCode.GameServiceError_VALUE);
            return;
        }

        LogicLoginRq.Builder builder = LogicLoginRq.newBuilder();
        builder.setUserId(rq.getUserId());
        builder.setChannel(rq.getChannel());
        builder.setIp(((InetSocketAddress)channel.remoteAddress()).getHostName());
        builder.setYx(rq.getYx());
        builder.setYxUserId(rq.getYxUserId());

        LogicLoginRs loginRs = RpcManager.getInstance().sendSync(node, CmdPb.Cmd.LogicLoginRq_VALUE, builder.build());
        if (loginRs == null) {
            sendError(channel, CmdPb.Cmd.GatewayLoginRs_VALUE, CmdPb.ErrorCode.GameServiceError_VALUE);
            return;
        }

        GatewayLoginRs.Builder rs = GatewayLoginRs.newBuilder();
        rs.setPlayerId(loginRs.getPlayerId());
        sendMessage(channel, CmdPb.Cmd.GatewayLoginRs_VALUE, rs.build());
    }

    @Command(CmdPb.Cmd.GatewayConnectRq_VALUE)
    public GatewayConnectRs connect(GatewayConnectRq rq, Channel channel) {


        return null;
    }


}
