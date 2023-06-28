package com.panda.game.logic.base;

import com.panda.game.logic.common.GamePlayer;

public abstract class AbstractModuleService implements ModuleService {

    protected GamePlayer gamePlayer;

    public AbstractModuleService(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
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
