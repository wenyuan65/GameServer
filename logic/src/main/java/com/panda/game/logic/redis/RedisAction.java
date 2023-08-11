package com.panda.game.logic.redis;


import com.panda.game.core.annotation.RedisCommand;
import com.panda.game.core.cmd.annotation.Action;
import com.panda.game.core.redis.RedisUtil;
import com.panda.game.proto.CmdPb;
import com.panda.game.proto.RedisPb;

@Action
public class RedisAction {

    @RedisCommand(CmdPb.Cmd.TopicPlayerOnline_VALUE)
    public void onlinePlayerMessage(RedisPb.PlayerOnlinePb msg) {
        RedisPb.PlayerOnlinePb.Builder builder = RedisPb.PlayerOnlinePb.newBuilder();
        RedisUtil.publishTopic(CmdPb.Cmd.TopicPlayerOnline_VALUE, builder.build());
    }

}
