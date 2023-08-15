package com.panda.game.logic.drop;

import com.panda.game.logic.common.GamePlayer;
import com.panda.game.logic.constants.DropSource;
import com.panda.game.proto.CommonPb;

public abstract class DropItem {
    // 道具id
    protected int itemId;
    // 数量
    protected long count;
    // 权重
    protected int weight;

    public DropItem(int itemId, long count) {
        this(itemId, count, 0);
    }

    public DropItem(int itemId, long count, int weight) {
        this.itemId = itemId;
        this.count = count;
        this.weight = weight;
    }

    /**
     * 掉落信息
     * @param player
     * @return
     */
    public CommonPb.DropItemMessagePb.Builder buildInfo(GamePlayer player) {
        CommonPb.DropItemMessagePb.Builder builder = CommonPb.DropItemMessagePb.newBuilder();
        builder.setItemId(itemId);
        builder.setCount(count);

        return builder;
    }

    /**
     * 判断数量是否足够
     * @param player
     * @return
     */
    public abstract boolean isEnough(GamePlayer player);

    /**
     * 扣除指定数量的道具
     * @param player
     * @return
     */
    public abstract boolean costItem(GamePlayer player, DropSource source);

    /**
     * 是否可以掉落
     * @return
     */
    public boolean canDrop(GamePlayer player) {
        return true;
    }

    /**
     * 掉落道具
     * @param player
     * @param source
     * @return
     */
    public abstract CommonPb.DropItemMessagePb dropItem(GamePlayer player, DropSource source);


    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
