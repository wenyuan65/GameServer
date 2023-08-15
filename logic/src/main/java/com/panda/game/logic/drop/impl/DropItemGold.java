package com.panda.game.logic.drop.impl;

import com.panda.game.logic.common.GamePlayer;
import com.panda.game.logic.constants.DropSource;
import com.panda.game.logic.drop.DropItem;
import com.panda.game.proto.CommonPb;

public class DropItemGold extends DropItem {

    public DropItemGold(int itemId, long count, int weight) {
        super(itemId, count, weight);
    }

    @Override
    public boolean isEnough(GamePlayer player) {
        return false;
    }

    @Override
    public boolean costItem(GamePlayer player, DropSource source) {
        return false;
    }

    @Override
    public CommonPb.DropItemMessagePb dropItem(GamePlayer player, DropSource source) {
        return null;
    }

}
