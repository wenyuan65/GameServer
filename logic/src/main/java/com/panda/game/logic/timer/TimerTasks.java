package com.panda.game.logic.timer;

import com.panda.game.common.concrrent.ServerThreadManager;
import com.panda.game.common.timer.annotation.Crontab;
import com.panda.game.logic.base.ServiceTrigger;
import com.panda.game.logic.base.WorldServiceTrigger;
import com.panda.game.logic.common.GamePlayer;
import com.panda.game.logic.world.WorldManager;

import java.util.Collection;

public class TimerTasks {

    @Crontab("0 0/1 * * * ? *")
    public void executePerMinutes(long now) {
        // 处理公共tick事件
        WorldManager.getInstance().triggered(WorldServiceTrigger.tick, now);

        // 处理在线玩家数据
        Collection<GamePlayer> players = WorldManager.getInstance().getAllPlayers();
        for (GamePlayer gamePlayer : players) {
            ServerThreadManager.getInstance().runBy(() -> handleGamePlayer(gamePlayer, now), gamePlayer.getPlayerId());
        }
    }

    //    @Crontab("0 0/5 * * * ? *")
    public void executePer5Minutes(long now) {

    }

    //    @Crontab("0 0 0/1 * * ? *")
    public void executePerHour(long now) {

    }

    @Crontab("0 0 5 * * ? *")
    public void executePerDay(long now) {
        Collection<GamePlayer> players = WorldManager.getInstance().getAllPlayers();
        for (GamePlayer player : players) {
            if (!player.isOnline()) {
                continue;
            }

            // 定时处理任务
            ServerThreadManager.getInstance().runBy(new Runnable() {
                @Override
                public void run() {
                    player.triggered(ServiceTrigger.resetDaily);
                }
            }, player.getPlayerId());
        }
    }

    private void handleGamePlayer(GamePlayer player, long now) {
        if (!player.isOnline()) {
            if (player.getExpiredTime() > 0 && now > player.getExpiredTime()) {
                player.unload();
            }
            return;
        }

        // 定时处理
        player.triggered(ServiceTrigger.tick, now);

        // TODO: 数据落地检查,玩家掉线检查，玩家状态检查
        long nextSaveTime = player.getNextSaveTime();
        if (now >= nextSaveTime) {
            player.updateSaveTime();
            player.triggered(ServiceTrigger.save);
        }
    }

}
