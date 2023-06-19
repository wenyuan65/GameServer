package com.panda.game.logic.user;

import com.panda.game.core.cmd.annotation.Action;
import com.panda.game.core.cmd.annotation.Command;
import com.panda.game.logic.common.GamePlayer;
import com.panda.game.logic.player.PlayerService;
import com.panda.game.proto.CmdPb;
import com.panda.game.proto.PlayerPb;

@Action
public class UserAction {

    @Command(value = CmdPb.Cmd.PlayerGetInfoRq_VALUE)
    public void getInfo(PlayerPb.PlayerGetInfoRq rq, GamePlayer player) {
        player.getService(PlayerService.class).getInfo(rq);
    }


}
