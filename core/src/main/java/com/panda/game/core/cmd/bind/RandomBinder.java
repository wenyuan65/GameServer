package com.panda.game.core.cmd.bind;

import com.panda.game.common.utils.RandomUtils;
import com.panda.game.core.cmd.Binder;

public class RandomBinder implements Binder {

    public RandomBinder() {
    }

    @Override
    public int calcBindIndex(long playerId, int cmd, Object[] params) {
        return RandomUtils.nextInt();
    }
}
