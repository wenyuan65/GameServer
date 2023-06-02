package com.panda.game.logic.common;

import com.panda.game.logic.player.GamePlayer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class WorldManager {

    private static final WorldManager instance = new WorldManager();

    private WorldManager() {
    }

    public static WorldManager getInstance() {
        return instance;
    }

    private ConcurrentHashMap<Long, GamePlayer> players = new ConcurrentHashMap<>(128);

    public boolean init() {
        // TODO: 加载一定数量的活跃玩家
        return true;
    }


    public void addPlayer(GamePlayer player) {
        players.put(player.getPlayerId(), player);
    }

    public GamePlayer getPlayer(long playerId) {
        return players.get(playerId);
    }

    /**
     * 加载玩家数据
     * @param playerId
     * @return
     */
    public GamePlayer load(long playerId, boolean create) {
        if (players.containsKey(playerId)) {
            GamePlayer gamePlayer = players.get(playerId);
            gamePlayer.load(false);

            return gamePlayer;
        }

        GamePlayer player = new GamePlayer();
        player.setPlayerId(playerId);
        player.load(create);

        return player;
    }

    /**
     * 分组加载玩家数据
     * @param playerId
     * @param list
     */
    public GamePlayer loadModuleGroup(long playerId, List<ModuleGroups> list) {
        if (players.containsKey(playerId)) {
            GamePlayer gamePlayer = players.get(playerId);
            gamePlayer.loadModuleGroup(list);

            return gamePlayer;
        }

        GamePlayer player = new GamePlayer();
        player.setPlayerId(playerId);
        player.loadModuleGroup(list);

        return player;
    }

}
