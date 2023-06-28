package com.panda.game.dao.db.logic.common;

import com.panda.game.core.jdbc.dao.BaseDao;
import com.panda.game.dao.entity.logic.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerDao extends BaseDao<Player> {

    public Player getPlayerByUserId(long userId) {
        String sql = "select * from player where user_id=" + userId;
        List<Player> list = getAllBySql(sql);
        if (list == null || list.size() == 0) {
            return null;
        }

        return list.get(0);
    }

    public Map<Long, Long> getUserIdAndPlayerIdMap() {
        String sql = "select user_id, player_id from player";
        List<Map<String, Object>> list = queryMapList(sql);
        Map<Long, Long> result = new HashMap<>(list.size());

        for (Map<String, Object> map : list) {
            Long userId = (Long)map.get("user_id");
            Long playerId = (Long)map.get("player_id");

            result.put(userId, playerId);
        }

        return result;
    }

}
