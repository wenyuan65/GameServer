package com.panda.game.core.common;

import com.google.protobuf.GeneratedMessageV3;
import com.panda.game.common.proto.PbUtil;
import io.netty.channel.Channel;

public class BaseAction {

    protected void sendError(Channel channel, int cmd, int errorCode) {
        channel.writeAndFlush(PbUtil.createErrorPkg(cmd, errorCode));
    }

    protected void sendOk(Channel channel, int cmd) {
        channel.writeAndFlush(PbUtil.createPkg(cmd).build());
    }

    protected void sendMessage(Channel channel, int cmd, GeneratedMessageV3 message) {
        channel.writeAndFlush(PbUtil.createPkg(cmd, message).build());
    }

}
