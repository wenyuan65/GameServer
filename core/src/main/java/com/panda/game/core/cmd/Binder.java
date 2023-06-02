package com.panda.game.core.cmd;

public interface Binder {

    int calcBindIndex(long playerId, int cmd, Object[] params);

}
