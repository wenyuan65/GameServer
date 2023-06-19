package com.panda.game.logic.player;

import com.panda.game.logic.annotation.Group;
import com.panda.game.logic.base.AbstractModuleService;
import com.panda.game.logic.common.GamePlayer;
import com.panda.game.logic.common.ModuleGroups;
import com.panda.game.proto.CmdPb.*;
import com.panda.game.proto.PlayerPb.*;

@Group(ModuleGroups.Base)
public class PlayerService extends AbstractModuleService {

    public PlayerService(GamePlayer player) {
        super(player);
    }

    @Override
    public boolean init() {
        return false;
    }

    @Override
    public boolean load() {
        return false;
    }

    @Override
    public boolean save() {
        return false;
    }

    public void getInfo(PlayerGetInfoRq rq) {
        PlayerGetInfoRs.Builder builder = PlayerGetInfoRs.newBuilder();
        builder.setLv(0);
        builder.setExp(0);
        builder.setName("");
        builder.setPic("");
        player.sendMessage(Cmd.PlayerGetInfoRs_VALUE, builder.build());
    }

    public void setName(PlayerSetNameRq rq) {
        String name = rq.getName();

        PlayerSetNameRs.Builder builder = PlayerSetNameRs.newBuilder();
        builder.setName("");
        player.sendMessage(Cmd.PlayerSetNameRs_VALUE, builder.build());
    }

}
