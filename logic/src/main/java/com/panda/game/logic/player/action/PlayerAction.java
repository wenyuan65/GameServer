package com.panda.game.logic.player.action;

import com.panda.game.core.cmd.annotation.Action;
import com.panda.game.core.cmd.annotation.Command;
import com.panda.game.logic.player.GamePlayer;
import com.panda.game.logic.player.PlayerService;
import com.panda.game.proto.CmdPb.Cmd;
import com.panda.game.proto.PlayerPb.PlayerGetInfoRq;
import com.panda.game.proto.PlayerPb.PlayerSetNameRq;

@Action
public class PlayerAction {

    /**
     * 获取玩家角色信息
     * @param rq
     * @param player
     */
    @Command(value = Cmd.PlayerGetInfoRq_VALUE)
    public void getInfo(PlayerGetInfoRq rq, GamePlayer player) {
        player.getService(PlayerService.class).getInfo(rq);
    }

    /**
     * 设置玩家角色名称
     * @param rq
     * @param player
     */
    @Command(Cmd.PlayerSetNameRq_VALUE)
    public void setName(PlayerSetNameRq rq, GamePlayer player) {
        player.getService(PlayerService.class).setName(rq);
    }

}
