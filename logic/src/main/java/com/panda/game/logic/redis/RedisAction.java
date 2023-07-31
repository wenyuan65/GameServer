package com.panda.game.logic.redis;


import com.panda.game.core.annotation.RedisCommand;
import com.panda.game.core.cmd.annotation.Action;
import com.panda.game.proto.RedisPb;

@Action
public class RedisAction {

    @RedisCommand(value = RedisPb.RedisCmd.Player_Online_Message_VALUE)
    public void onlinePlayerMessage(RedisPb.PlayerOnlinePb msg) {

    }

}
