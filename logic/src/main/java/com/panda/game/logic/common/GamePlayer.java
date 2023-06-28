package com.panda.game.logic.common;

import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.googlecode.protobuf.format.JsonFormat;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.core.netty.listener.SendFailListener;
import com.panda.game.logic.base.ModuleService;
import com.panda.game.logic.base.ModuleServiceHelper;
import com.panda.game.logic.base.ServiceTrigger;
import com.panda.game.proto.CmdPb;
import com.panda.game.proto.PacketPb;
import io.netty.channel.Channel;

import java.lang.reflect.Constructor;
import java.util.*;

public class GamePlayer {

    private static final Logger logger = LoggerFactory.getLogger(GamePlayer.class);
    private static final Logger dayLog = LoggerFactory.getDayLog();

    private Channel channel;
    private long playerId;
    private OnlineStatus status = OnlineStatus.Offline;
    private long nextSaveTime = 0;
    private long expiredTime = 0;

    private Map<Class<? extends ModuleService>, ModuleService> serviceMap = new HashMap<>();

    public GamePlayer(long playerId) {
        this.playerId = playerId;

        this.nextSaveTime = System.currentTimeMillis();
        updateSaveTime();
    }

    /**
     * 加载数据
     * @param create
     * @return
     */
    public boolean load(boolean create) {
        if (!initModuleGroupService(Arrays.asList(ModuleGroups.values()))) {
            return false;
        }
        if (!doLoad(create, null)) {
            return false;
        }

        return true;
    }

    public boolean loadModuleGroup(List<ModuleGroups> list) {
        Set<Class<? extends ModuleService>> filter = serviceMap.keySet();
        if (!initModuleGroupService(list)) {
            return false;
        }
        if (!doLoad(false, filter)) {
            return false;
        }

        return true;
    }

    private boolean initModuleGroupService(List<ModuleGroups> list) {
        List<Class<? extends ModuleService>> moduleGroupServices = ModuleServiceHelper.getModuleGroupServices(list);
        for (Class<? extends ModuleService> clazz : moduleGroupServices) {
            if (serviceMap.containsKey(clazz)) {
                continue;
            }

            try {
                Constructor<? extends ModuleService> constructor = clazz.getConstructor(GamePlayer.class);
                ModuleService moduleService = constructor.newInstance(this);
                serviceMap.put(clazz, moduleService);
            } catch (Throwable e) {
                logger.error("初始化玩家模块异常", e);
                return false;
            }
        }

        return true;
    }

    /**
     * 加载数据
     * @param create
     * @return
     */
    private boolean doLoad(boolean create, Set<Class<? extends ModuleService>> filter) {
        for (Map.Entry<Class<? extends ModuleService>, ModuleService> entry : serviceMap.entrySet()) {
            ModuleService service = entry.getValue();
            if (filter != null && filter.contains(entry.getKey())) {
                continue;
            }

            boolean success = false;
            if (create) {
                success = service.init();
            } else {
                success = service.load();
            }
            if (!success) {
                return false;
            }
        }

        // 触发加载后事件
        triggered(ServiceTrigger.doAfterLoaded);

        return true;
    }

    /**
     * 触发各种触发点事件
     * @param serviceTrigger
     * @param params
     * @return
     */
    public boolean triggered(ServiceTrigger serviceTrigger, Object... params) {
        for (Map.Entry<Class<? extends ModuleService>, ModuleService> entry : serviceMap.entrySet()) {
            try {
                ModuleService service = entry.getValue();
                boolean result = serviceTrigger.trigger(service, params);
                if (!result) {
                    logger.info("触发玩家事件错误, {}, {}, {}", playerId, serviceTrigger.name(), entry.getKey().getName());
                    return false;
                }
            } catch (Exception e) {
                logger.error("触发玩家事件异常, {}, {}, {}", e, playerId, serviceTrigger.name(), entry.getKey().getName());
                return false;
            }
        }

        return true;
    }

    public boolean unload() {
        triggered(ServiceTrigger.unload);


        return true;
    }

    /**
     * 获取指定的service
     * @param clazz
     * @return
     * @param <T>
     */
    public <T extends ModuleService> T getService(Class<T> clazz) {
        return (T) serviceMap.get(clazz);
    }

    /**
     * 更新下次保存时间
     */
    public void updateSaveTime() {
        this.nextSaveTime = this.nextSaveTime + Constants.Player_Save_Interval;
    }

    /**
     * 判断玩家是否在线
     * @return
     */
    public boolean isOnline() {
        return status == OnlineStatus.Online;
    }

    /**
     * 判断玩家是否可以推送数据
     * @return
     */
    public boolean isPushable() {
        return channel != null && channel.isActive();
    }

    /**
     * 发送消息
     * @param rs
     * @param msg
     */
    public void sendMessage(int rs, MessageLite msg) {
        if (!isOnline() || !isPushable()) {
            logger.error("玩家不在线，或者连接异常,无法推送数据", playerId, CmdPb.Cmd.forNumber(rs).name());
            return;
        }

        PacketPb.Pkg.Builder builder = PacketPb.Pkg.newBuilder();
        builder.setBody(msg.toByteString());
        builder.setCmd(rs);
        builder.setPlayerId(playerId);

        channel.writeAndFlush(builder.build()).addListener(new SendFailListener(playerId, rs));

        String response = JsonFormat.printToString((Message) msg);
        if (response.length() > 1024) {
            response = new StringBuilder().append(response, 0, 256).append("...}, length:").append(response.length()).toString();
        }
        dayLog.info("#o#{}#{}#{}#{}#", this, CmdPb.Cmd.forNumber(rs).name(), response);
    }

    public void sendError(int rs, int errorCode) {
        sendError(rs, errorCode, "");
    }

    public void sendError(int rs, int errorCode, String title) {
        if (!isOnline() || !isPushable()) {
            logger.error("玩家不在线，或者连接异常,无法推送数据", playerId, CmdPb.Cmd.forNumber(rs).name());
            return;
        }

        PacketPb.Pkg.Builder builder = PacketPb.Pkg.newBuilder();
        builder.setCmd(rs);
        builder.setErrorCode(errorCode);
        builder.setPlayerId(playerId);

        channel.writeAndFlush(builder.build()).addListener(new SendFailListener(playerId, rs));

        dayLog.info("#o#{}#{}#{}#{}#", this, CmdPb.Cmd.forNumber(rs).name(), CmdPb.ErrorCode.forNumber(errorCode).name(), title);
    }

    @Override
    public String toString() {
        // GamePlayer对象的toString()方法参与日志格式的打印，修改格式需谨慎
        StringBuilder sb = new StringBuilder();
        sb.append(playerId).append('#');
        sb.append("userId").append('#');
        sb.append("playerName").append('#');
        sb.append(status == OnlineStatus.Online ? "on" : "off");

        return sb.toString();
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public OnlineStatus getStatus() {
        return status;
    }

    public void setStatus(OnlineStatus status) {
        this.status = status;
    }

    public long getNextSaveTime() {
        return nextSaveTime;
    }

    public void setNextSaveTime(long nextSaveTime) {
        this.nextSaveTime = nextSaveTime;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }
}
