package com.panda.game.logic.drop;

import com.panda.game.common.utils.StringUtils;
import com.panda.game.logic.drop.impl.DropItemGold;

import java.util.List;

public class DropFactory {

    public static final Drop parseDrop(String dropContent) {
        Drop drop = new Drop();

        List<int[]> arrays = StringUtils.str2IntArray2(dropContent);
        for (int[] array : arrays) {
            int weight = array.length > 2 ? array[2] : 0;

            DropItem dropItem = parseDropItem(array[0], array[1], weight);
            drop.addDropItem(dropItem);
        }

        return drop;
    }

    public static final DropItem parseDropItem(String dropIItemContent) {
        int[] array = StringUtils.str2IntArray(dropIItemContent);
        int weight = array.length > 2 ? array[2] : 0;

        return parseDropItem(array[0], array[1], weight);
    }

    public static final DropItem parseDropItem(int itemId, long count, int weight) {
        // TODO:
        int type = 1;

        switch (type) {
            case 1 : return new DropItemGold(itemId, count, weight);

            default: throw new RuntimeException("Unknown DropItem，itemId:" + itemId + ", type:" + type);
        }
    }

}
