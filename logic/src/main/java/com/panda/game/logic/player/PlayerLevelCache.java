package com.panda.game.logic.player;

import com.panda.game.core.cache.Cache;
import com.panda.game.dao.entity.logic.player.PlayerLevelTemplate;

public class PlayerLevelCache implements Cache<PlayerLevelTemplate> {

    @Override
    public boolean reload() {
        return false;
    }

}
