package com.panda.game.core.cmd.bind;

import com.panda.game.core.cmd.Binder;

public class PlayerIdBinder implements Binder {

    public PlayerIdBinder() {
    }

    @Override
    public int calcBindIndex(long playerId, int cmd, Object[] params) {
        return Long.hashCode(playerId);
    }
}
