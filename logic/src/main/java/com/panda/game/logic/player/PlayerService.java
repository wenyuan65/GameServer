package com.panda.game.logic.player;

import com.panda.game.common.utils.StringUtils;
import com.panda.game.core.jdbc.base.Option;
import com.panda.game.dao.db.logic.DBManager;
import com.panda.game.dao.entity.logic.player.Player;
import com.panda.game.logic.base.Group;
import com.panda.game.logic.base.AbstractModuleService;
import com.panda.game.logic.common.GamePlayer;
import com.panda.game.logic.common.ModuleGroups;
import com.panda.game.proto.CmdPb.*;
import com.panda.game.proto.PlayerPb.*;

import java.util.Date;
import java.util.List;

@Group(ModuleGroups.Base)
public class PlayerService extends AbstractModuleService {

    private Player player;

    public PlayerService(GamePlayer player) {
        super(player);
    }

    @Override
    public boolean init() {
        player = new Player();
        player.setPlayerId(gamePlayer.getPlayerId());
        player.setPlayerName("冒险者");
        player.setLv(1);
        player.setExp(0);
        player.setMale(1);
        player.setPic("");
        player.setUserId(0L);
        player.setYxUserId("");
        player.setYx("");
        player.setChannelId("");
        player.setCreateTime(new Date());
        player.setOp(Option.Insert);

        return true;
    }

    @Override
    public boolean load() {
        List<Player> playerList = DBManager.getPlayerDao().getAllByPlayerId(player.getPlayerId());
        if (playerList == null || playerList.size() == 0) {
            return init();
        }

        player = playerList.get(0);

        return true;
    }

    @Override
    public boolean save() {
        DBManager.getPlayerDao().addOrUpdate(player);
        return true;
    }

    public Player getPlayer() {
        return player;
    }

    public void updatePlayer(long userId, int male, String yxUserId, String yx, String channelId) {
        if (player == null) {
            return ;
        }

        player.setMale(male);
        player.setUserId(userId);
        player.setYxUserId(yxUserId);
        player.setYx(yx);
        player.setChannelId(channelId);
    }

    public void getInfo(PlayerGetInfoRq rq) {
        PlayerGetInfoRs.Builder builder = PlayerGetInfoRs.newBuilder();
        builder.setLv(0);
        builder.setExp(0);
        builder.setName("");
        builder.setPic("");
        gamePlayer.sendMessage(Cmd.PlayerGetInfoRs_VALUE, builder.build());
    }

    public void setName(PlayerSetNameRq rq) {
        String name = rq.getName();
        if (StringUtils.isBlank(name)) {
            gamePlayer.sendError(Cmd.PlayerSetNameRs_VALUE, ErrorCode.Param_VALUE, "1");
            return;
        }

        player.setPlayerName(name);

        PlayerSetNameRs.Builder builder = PlayerSetNameRs.newBuilder();
        builder.setName("");
        gamePlayer.sendMessage(Cmd.PlayerSetNameRs_VALUE, builder.build());
    }

}
