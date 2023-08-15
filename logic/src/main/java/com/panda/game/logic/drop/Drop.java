package com.panda.game.logic.drop;

import com.panda.game.logic.common.GamePlayer;
import com.panda.game.logic.constants.DropSource;
import com.panda.game.proto.CommonPb;

import java.util.ArrayList;
import java.util.List;

public class Drop {

    private List<DropItem> list = new ArrayList<>();

    public Drop() {
    }

    public Drop(List<DropItem> list) {
        this.list = list;
    }

    public void addDropItem(DropItem dropItem) {
        list.add(dropItem);
    }

    public CommonPb.DropMessagePb buildInfo(GamePlayer player) {
        CommonPb.DropMessagePb.Builder builder = CommonPb.DropMessagePb.newBuilder();

        for (DropItem dropItem : list) {
            builder.addList(dropItem.buildInfo(player));
        }

        return builder.build();
    }

    /**
     * 判断数量是否足够
     * @param player
     * @return
     */
    public boolean isEnough(GamePlayer player) {
        for (DropItem dropItem : list) {
            if (!dropItem.isEnough(player)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 扣除指定数量的道具
     * @param player
     * @return
     */
    public boolean costItem(GamePlayer player, DropSource source) {
        for (DropItem dropItem : list) {
            if (!dropItem.costItem(player, source)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 是否可以掉落
     * @return
     */
    public boolean canDrop(GamePlayer player) {
        for (DropItem dropItem : list) {
            if (!dropItem.canDrop(player)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 掉落道具
     * @param player
     * @param source
     * @return
     */
    public CommonPb.DropMessagePb dropItem(GamePlayer player, DropSource source) {
        CommonPb.DropMessagePb.Builder builder = CommonPb.DropMessagePb.newBuilder();
        for (DropItem dropItem : list) {
            CommonPb.DropItemMessagePb msg = dropItem.dropItem(player, source);
            if (msg != null) {
                builder.addList(msg);
            }
        }

        return builder.build();
    }


}
