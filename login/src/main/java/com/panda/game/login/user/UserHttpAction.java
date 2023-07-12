package com.panda.game.login.user;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.panda.game.common.constants.NodeCluster;
import com.panda.game.common.constants.NodeType;
import com.panda.game.common.json.JsonDocument;
import com.panda.game.common.utils.SnowFlake;
import com.panda.game.common.utils.StringUtils;
import com.panda.game.core.annotation.Bind;
import com.panda.game.core.annotation.HttpCommand;
import com.panda.game.core.cmd.CmdBindType;
import com.panda.game.core.cmd.annotation.Action;
import com.panda.game.core.cmd.annotation.Command;
import com.panda.game.core.common.BaseAction;
import com.panda.game.core.common.BaseHttpAction;
import com.panda.game.core.jdbc.base.Option;
import com.panda.game.core.nacos.NodeHelper;
import com.panda.game.core.nacos.NodeManager;
import com.panda.game.core.rpc.RpcManager;
import com.panda.game.dao.db.login.LoginDBManager;
import com.panda.game.dao.entity.login.User;
import com.panda.game.proto.CmdPb;
import com.panda.game.proto.LoginPb;
import com.panda.game.proto.LoginPb.GatewayLoginRq;
import com.panda.game.proto.LoginPb.GatewayLoginRs;
import com.panda.game.proto.LoginPb.LoginRs;
import io.netty.channel.Channel;

import java.util.Date;

@Action
public class UserHttpAction extends BaseHttpAction {

    @Bind(bindType=CmdBindType.Bind_Group)
    @HttpCommand("createUser")
    public void createUser(String userName, String password, Channel channel) {
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            sendError(channel, CmdPb.ErrorCode.Param_VALUE);
            return ;
        }
        // TODO: 缓存数据
        User user = LoginDBManager.getUserDao().getUserByUserName(userName);
        if (user != null) {
            sendError(channel, CmdPb.ErrorCode.UserExist_VALUE);
            return ;
        }

        user = new User();
        user.setUserId(SnowFlake.getNextId());
        user.setUserName(userName);
        user.setPassword(password);
        user.setYxUserId("default_"+ user.getUserId());
        user.setYx("default");
        user.setChannelId("default");
        user.setCreateTime(new Date());
        user.setLogicNodeId(0);
        user.setOp(Option.Insert);
        LoginDBManager.getUserDao().add(user);

        sendOk(channel);
    }

    @HttpCommand("login")
    public void login(String userName, String password, Channel channel) {
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            sendError(channel, CmdPb.ErrorCode.Param_VALUE);
            return ;
        }
        // TODO: 缓存数据
        User user = LoginDBManager.getUserDao().getUserByUserName(userName);
        if (user == null) {
            sendError(channel, CmdPb.ErrorCode.Param_VALUE);
            return ;
        }
        if (!password.equals(user.getPassword())) {
            sendError(channel, CmdPb.ErrorCode.Param_VALUE);
            return ;
        }

        // 随机gateway/和logic
        Instance instance = NodeManager.getInstance().selectNode(NodeType.Gateway, NodeCluster.Common);
        if (instance == null) {
            sendError(channel, CmdPb.ErrorCode.ServiceError_VALUE);
            return ;
        }
        // 随机logic
        if (user.getLogicNodeId() == 0) {
            Instance logicInstance = NodeManager.getInstance().selectNode(NodeType.Logic, NodeCluster.Common);
            user.setLogicNodeId(NodeHelper.getNodeId(logicInstance.getInstanceId()));
            LoginDBManager.getUserDao().addOrUpdate(user);
        }
        if (user.getLogicNodeId() == 0) {
            sendError(channel, CmdPb.ErrorCode.GameServiceError_VALUE);
            return ;
        }

        // rpc请求连接gateway
        GatewayLoginRq.Builder builder = GatewayLoginRq.newBuilder();
        builder.setUserId(user.getUserId());
        builder.setYx(user.getYx());
        builder.setChannel(user.getChannelId());
        builder.setNodeId(user.getLogicNodeId());
        builder.setYxUserId(user.getYxUserId());

        GatewayLoginRs loginRs = RpcManager.getInstance().sendSync(instance, CmdPb.Cmd.GatewayLoginRq_VALUE, builder.build());
        if (loginRs == null) {
            sendError(channel, CmdPb.ErrorCode.GameServiceError_VALUE);
            return ;
        }

        JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("playerId", String.valueOf(loginRs.getPlayerId()));
        doc.createElement("logicNodeId", "user.getLogicNodeId()");
        doc.createElement("ip", instance.getIp());
        doc.createElement("port", instance.getPort());
        doc.endObject();

        sendOk(channel, doc.toString());
    }

}
