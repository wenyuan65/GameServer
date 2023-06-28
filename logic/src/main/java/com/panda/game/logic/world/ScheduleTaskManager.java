package com.panda.game.logic.world;

import com.panda.game.common.concrrent.ServerThreadManager;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.timer.Scheduler;
import com.panda.game.common.timer.quartz.Job;
import com.panda.game.common.utils.StringUtils;
import com.panda.game.core.annotation.Crontab;
import com.panda.game.logic.base.ServiceTrigger;
import com.panda.game.logic.base.WorldServiceTrigger;
import com.panda.game.logic.common.GamePlayer;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class ScheduleTaskManager {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleTaskManager.class);

    private static final ScheduleTaskManager instance = new ScheduleTaskManager();

    private ScheduleTaskManager() {
    }

    public static ScheduleTaskManager getInstance() {
        return instance;
    }

    public boolean init() {
        try {
            Method[] methods = ScheduleTaskManager.class.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                Crontab crontab = method.getDeclaredAnnotation(Crontab.class);
                if (crontab == null) {
                    continue;
                }
                int modifiers = method.getModifiers();
                if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers)) {
                    continue;
                }

                int jobId = crontab.jobId() <= 0 ? i + 1 : crontab.jobId();
                String jobName = StringUtils.isBlank(crontab.jobName()) ? method.getName() : crontab.jobName();

                Scheduler.addJob(new Job(jobId, jobName, this, method, crontab.value()));
            }
        } catch (SecurityException e) {
            logger.error("初始化定时任务异常", e);
            return false;
        }

        return true;
    }

    @Crontab("0 0/1 * * * ? *")
    public void executePerMinutes() {
        long time = System.currentTimeMillis();
        final long now = ((time + 500) / 1000L) * 1000L;

        // 处理公共tick事件
        WorldManager.getInstance().triggered(WorldServiceTrigger.tick, now);

        // 处理在线玩家数据
        Collection<GamePlayer> players = WorldManager.getInstance().getAllPlayers();
        for (GamePlayer gamePlayer : players) {
            ServerThreadManager.getInstance().runBy(() -> handleGamePlayer(gamePlayer, now), gamePlayer.getPlayerId());
        }
    }

//    @Crontab("0 0/5 * * * ? *")
    public void executePer5Minutes() {

    }

//    @Crontab("0 0 0/1 * * ? *")
    public void executePerHour() {

    }

    @Crontab("0 0 5 * * ? *")
    public void executePerDay() {
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
