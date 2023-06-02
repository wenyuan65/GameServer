package com.panda.game.core.cmd.bind;

import com.panda.game.core.cmd.Binder;

public class GroupBinder implements Binder {

    private int group;

    public GroupBinder(int group) {
        this.group = group;
    }

    @Override
    public int calcBindIndex(long playerId, int cmd, Object[] params) {
        return group;
    }
}
