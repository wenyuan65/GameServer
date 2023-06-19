package com.panda.game.logic.world;

/**
 * 公共模块抽象类,存在玩家交互的模块
 */
public interface WorldModuleService {

    /**
     * 初始化
     * @return
     */
    boolean init();

    /**
     * 加载数据
     * @return
     */
    boolean loadDatabase();

    /**
     * 数据保存
     * @return
     */
    boolean save();

    /**
     * 每日系统重置时间时，重置逻辑
     * @return
     */
    boolean resetDaily();

    /**
     * 定时执行方法
     */
    void tick(long now);

}
