package com.panda.game.login.user;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.protobuf.GeneratedMessageV3;
import com.panda.game.common.constants.NodeCluster;
import com.panda.game.common.constants.NodeType;
import com.panda.game.common.proto.PbUtil;
import com.panda.game.common.utils.SnowFlake;
import com.panda.game.common.utils.StringUtils;
import com.panda.game.core.cmd.CmdBindType;
import com.panda.game.core.cmd.annotation.Action;
import com.panda.game.core.cmd.annotation.Command;
import com.panda.game.core.jdbc.base.Option;
import com.panda.game.core.nacos.NodeManager;
import com.panda.game.core.rpc.RpcManager;
import com.panda.game.dao.db.login.LoginDBManager;
import com.panda.game.dao.entity.login.User;
import com.panda.game.proto.CmdPb;
import com.panda.game.proto.LoginPb;
import com.panda.game.proto.LoginPb.*;
import io.netty.channel.Channel;

import java.util.Date;

@Action
public class UserAction {

    @Command(value=CmdPb.Cmd.CreateUserRq_VALUE, bindType=CmdBindType.Bind_Group)
    public void createUser(LoginPb.CreateUserRq rq, Channel channel) {
        String userName = rq.getUserName();
        String password = rq.getPassword();
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            sendError(channel, CmdPb.Cmd.CreateUserRs_VALUE, CmdPb.ErrorCode.Param_VALUE);
            return ;
        }
        User user = LoginDBManager.getUserDao().getUserByUserName(userName);
        if (user != null) {
            sendError(channel, CmdPb.Cmd.CreateUserRs_VALUE, CmdPb.ErrorCode.UserExist_VALUE);
            return ;
        }

        user = new User();
        user.setUserId(SnowFlake.getNextId());
        user.setUserName(userName);
        user.setPassword(password);
        user.setYxUserId("");
        user.setYx("");
        user.setChannelId("");
        user.setCreateTime(new Date());
        user.setOp(Option.Insert);
        LoginDBManager.getUserDao().add(user);

        sendOk(channel, CmdPb.Cmd.CreateUserRs_VALUE);
    }

    @Command(value=CmdPb.Cmd.LoginRq_VALUE, bindType=CmdBindType.Bind_Group)
    public void login(LoginPb.LoginRq rq, Channel channel) {
        String userName = rq.getUserName();
        String password = rq.getPassword();
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            sendError(channel, CmdPb.Cmd.LoginRs_VALUE, CmdPb.ErrorCode.Param_VALUE);
            return ;
        }
        User user = LoginDBManager.getUserDao().getUserByUserName(userName);
        if (user == null) {
            sendError(channel, CmdPb.Cmd.LoginRs_VALUE, CmdPb.ErrorCode.Param_VALUE);
            return ;
        }
        if (!password.equals(user.getPassword())) {
            sendError(channel, CmdPb.Cmd.LoginRs_VALUE, CmdPb.ErrorCode.Param_VALUE);
            return ;
        }

        // 随机gateway/和logic
        Instance instance = NodeManager.getInstance().selectOne(NodeType.Gateway.getName(), NodeCluster.Common.getName());
        if (instance == null) {
            sendError(channel, CmdPb.Cmd.LoginRs_VALUE, CmdPb.ErrorCode.ServiceError_VALUE);
            return ;
        }

        // rpc请求连接gateway
        GatewayLoginRq.Builder builder = GatewayLoginRq.newBuilder();
        builder.setUserId(user.getUserId());
        builder.setYx(user.getYx());
        builder.setChannel(user.getChannelId());

        GatewayLoginRs loginRs = RpcManager.getInstance().sendSync(instance.getIp(), instance.getPort(), CmdPb.Cmd.GatewayLoginRq_VALUE, builder.build(), GatewayLoginRs.parser());



        sendOk(channel, CmdPb.Cmd.LoginRs_VALUE);
    }

    private void sendError(Channel channel, int cmd, int errorCode) {
        channel.writeAndFlush(PbUtil.createErrorPkg(cmd, errorCode));
    }

    private void sendOk(Channel channel, int cmd) {
        channel.writeAndFlush(PbUtil.createPkg(cmd).build());
    }

    private void sendMessage(Channel channel, int cmd, GeneratedMessageV3 message) {
        channel.writeAndFlush(PbUtil.createPkg(cmd, message).build());
    }

}
