package com.panda.game.logic.player;

import com.panda.game.logic.world.WorldModuleService;

import java.util.concurrent.ConcurrentHashMap;

public class WorldPlayerService implements WorldModuleService {

    private ConcurrentHashMap<Long, Long> userId2PlayerIdMap = new ConcurrentHashMap<>();

    @Override
    public boolean init() {
        return false;
    }

    @Override
    public boolean loadDatabase() {
        return false;
    }

    @Override
    public boolean save() {
        return false;
    }

    @Override
    public boolean resetDaily() {
        return false;
    }

    @Override
    public void tick(long now) {

    }
}
