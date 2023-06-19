package com.panda.game.logic.world;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.timer.Scheduler;
import com.panda.game.common.timer.quartz.Job;
import com.panda.game.common.utils.ScanUtil;
import com.panda.game.logic.LogicServer;
import com.panda.game.logic.base.ModuleService;
import com.panda.game.logic.base.WorldServiceTrigger;
import com.panda.game.logic.common.ModuleGroups;
import com.panda.game.logic.common.GamePlayer;
import net.bytebuddy.implementation.bytecode.Throw;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorldManager {

    private static final Logger logger = LoggerFactory.getLogger(WorldManager.class);

    private static final WorldManager instance = new WorldManager();

    private WorldManager() {
    }

    public static WorldManager getInstance() {
        return instance;
    }

    private ConcurrentHashMap<Long, GamePlayer> players = new ConcurrentHashMap<>(128);

    private Map<Class<? extends WorldModuleService>, WorldModuleService> worldServiceMap = new HashMap<>();

    public boolean init() {
        if (initWorldModuleService()) {
            return false;
        }
        // 初始化模块
        if (triggered(WorldServiceTrigger.init)) {
            return false;
        }

        loadActivePlayer();

        return true;
    }

    /**
     * 加载公共服务模块
     */
    private boolean initWorldModuleService() {
        Set<Class<?>> classes = ScanUtil.scan(LogicServer.class.getPackage().getName());
        for (Class<?> clazz : classes) {
            if (!WorldModuleService.class.isAssignableFrom(clazz)) {
                continue;
            }

            int modifiers = clazz.getModifiers();
            if (Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers)) {
                continue;
            }

            try {
                Class<? extends WorldModuleService> serviceClazz = (Class<? extends WorldModuleService>)clazz;
                WorldModuleService service = serviceClazz.getConstructor().newInstance();
                worldServiceMap.put(serviceClazz, service);
            } catch (Throwable e) {
                logger.error("初始化公共模块异常, {}", e, clazz.getName());
                return false;
            }
        }

        return true;
    }

    /**
     * 触发点
     * @param serviceTrigger
     * @param params
     */
    public boolean triggered(WorldServiceTrigger serviceTrigger, Object... params) {
        for (Map.Entry<Class<? extends WorldModuleService>, WorldModuleService> entry : worldServiceMap.entrySet()) {
            WorldModuleService worldModuleService = entry.getValue();
            boolean result = serviceTrigger.trigger(worldModuleService, params);
            if (!result) {
                logger.info("触发公共服务异常, {}, {}", serviceTrigger.name(), entry.getKey().getName());
                return false;
            }
        }
        return false;
    }

    public <T extends WorldModuleService> T getWorldModuleService(Class<T> clazz) {
        return (T) worldServiceMap.get(clazz);
    }

    public void loadActivePlayer() {
        // TODO: 加载一定数量的活跃玩家
    }

    public void addPlayer(GamePlayer player) {
        players.put(player.getPlayerId(), player);
    }

    public GamePlayer getPlayer(long playerId) {
        return players.get(playerId);
    }

    public Collection<GamePlayer> getAllPlayers() {
        return players.values();
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
