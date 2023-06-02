package com.panda.game.core.cmd.bind;

import com.panda.game.core.cmd.Binder;

public class CmdBinder implements Binder {

    public CmdBinder() {
    }

    @Override
    public int calcBindIndex(long playerId, int cmd, Object[] params) {
        return Integer.hashCode(cmd);
    }
}
