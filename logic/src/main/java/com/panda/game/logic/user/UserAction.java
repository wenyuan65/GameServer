package com.panda.game.logic.user;

import com.panda.game.core.cmd.annotation.Action;
import com.panda.game.core.cmd.annotation.Command;
import com.panda.game.core.common.BaseAction;
import com.panda.game.logic.common.EntityIdManager;
import com.panda.game.logic.common.GamePlayer;
import com.panda.game.logic.common.OnlineStatus;
import com.panda.game.logic.player.PlayerService;
import com.panda.game.logic.world.WorldManager;
import com.panda.game.proto.CmdPb;
import com.panda.game.proto.LoginPb;
import io.netty.channel.Channel;

@Action
public class UserAction extends BaseAction {

    @Command(CmdPb.Cmd.LogicLoginRq_VALUE)
    public void login(LoginPb.LogicLoginRq rq, Channel channel) {
        long userId = rq.getUserId();
        String yxUserId = rq.getYxUserId();
        String yxChannel = rq.getChannel();
        String yx = rq.getYx();
        String ip = rq.getIp();

        boolean createNewPlayer = false;
        Long playerId = WorldManager.getInstance().getPlayerId(userId);
        if (playerId == null) {
            playerId = EntityIdManager.getInstance().getNextPlayerId();
            createNewPlayer = true;
        }

        GamePlayer gamePlayer = WorldManager.getInstance().load(playerId, createNewPlayer);
        if (gamePlayer == null) {
            sendError(channel, CmdPb.Cmd.LogicLoginRs_VALUE, CmdPb.ErrorCode.GameServiceError_VALUE);
            return;
        }

        // 更新数据
        if (createNewPlayer) {
            gamePlayer.getService(PlayerService.class).updatePlayer(userId, 1, yxUserId, yx, yxChannel);
            WorldManager.getInstance().addUserIdAndPlayerIdCache(userId, playerId);
        }

        gamePlayer.setChannel(channel);
        gamePlayer.setStatus(OnlineStatus.Online);
        gamePlayer.updateSaveTime();

        LoginPb.LogicLoginRs.Builder builder = LoginPb.LogicLoginRs.newBuilder();
        builder.setPlayerId(playerId);
        gamePlayer.sendMessage(CmdPb.Cmd.LogicLoginRs_VALUE, builder.build());
    }

}
