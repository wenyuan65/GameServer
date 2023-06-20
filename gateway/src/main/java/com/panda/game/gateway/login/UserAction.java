package com.panda.game.gateway.login;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.panda.game.common.constants.NodeCluster;
import com.panda.game.common.constants.NodeType;
import com.panda.game.core.cmd.annotation.Action;
import com.panda.game.core.cmd.annotation.Command;
import com.panda.game.core.nacos.NodeManager;
import com.panda.game.core.rpc.RpcManager;
import com.panda.game.proto.CmdPb;
import com.panda.game.proto.LoginPb.*;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

@Action
public class UserAction {

    @Command(value = CmdPb.Cmd.GatewayLoginRq_VALUE)
    public GatewayLoginRs login(GatewayLoginRq rq, Channel channel) {

        Instance instance = NodeManager.getInstance().selectOne(NodeType.Logic.getName(), NodeCluster.Common.getName());
        if (instance == null) {
            return null;
        }

        LogicLoginRq.Builder builder = LogicLoginRq.newBuilder();
        builder.setUserId(rq.getUserId());
        builder.setChannel(rq.getChannel());
        builder.setIp(((InetSocketAddress)channel.remoteAddress()).getHostName());
        builder.setYx(rq.getYx());

        LogicLoginRs loginRs = RpcManager.getInstance().sendSync(instance.getIp(), instance.getPort(), CmdPb.Cmd.LogicLoginRq_VALUE, builder.build(), LogicLoginRs.parser());



        return null;
    }

    @Command(value = CmdPb.Cmd.GatewayConnectRq_VALUE)
    public GatewayConnectRs connect(GatewayConnectRq rq) {


        return null;
    }


}
