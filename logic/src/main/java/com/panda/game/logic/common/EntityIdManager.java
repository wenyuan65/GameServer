package com.panda.game.logic.common;

import com.panda.game.common.config.Configuration;
import com.panda.game.common.utils.SnowFlake;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 服务器id管理单元
 */
public class EntityIdManager {

    private static final EntityIdManager instance = new EntityIdManager();

    private EntityIdManager() {
    }

    public static EntityIdManager getInstance() {
        return instance;
    }

    private AtomicLong playerIdHolder = new AtomicLong();


    public boolean init() {
        Integer result = Configuration.getIntProperty("server.uniqueId", null);
        if (result == null) {
            return false;
        }

        int uniqueServerId = result;
        SnowFlake.init(uniqueServerId, null);

        // TODO: 从数据库中读取
        // long maxPlayerId = PlayerDao.getInstance().getMaxPlayerId();
        long maxPlayerId = new Random().nextInt() == 0 ? 0 : 100000000L;
        if (maxPlayerId == 0) {
            playerIdHolder.set(uniqueServerId * 100000000L);
        } else {
            playerIdHolder.set(maxPlayerId);
        }

        return false;
    }

    public long getNextPlayerId() {
        return playerIdHolder.incrementAndGet();
    }

    public long getNextId() {
        return SnowFlake.getNextId();
    }

}
