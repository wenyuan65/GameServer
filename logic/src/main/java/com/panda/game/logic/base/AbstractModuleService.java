package com.panda.game.logic.base;

import com.panda.game.logic.common.GamePlayer;

public abstract class AbstractModuleService implements ModuleService {

    protected GamePlayer player;

    public AbstractModuleService(GamePlayer player) {
        this.player = player;
    }

    @Override
    public boolean unload() {
        return false;
    }

    @Override
    public boolean doAfterLoaded() {
        return false;
    }

    @Override
    public boolean doBeforeLogin() {
        return false;
    }

    @Override
    public boolean doAfterLogin() {
        return false;
    }

    @Override
    public boolean doAfterLogout() {
        return false;
    }

    @Override
    public void resetDaily(boolean isLogin) {

    }

    @Override
    public void tick(long now) {

    }
}
