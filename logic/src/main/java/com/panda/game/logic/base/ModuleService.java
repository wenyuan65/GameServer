package com.panda.game.logic.base;

public interface ModuleService {

    /**
     * 初始化模块数据
     * @return
     */
    boolean init();

    /**
     * 加载数据
     * @return
     */
    boolean load();

    /**
     * 保存数据
     * @return
     */
    boolean save();

    /**
     * 卸载数据
     * @return
     */
    boolean unload();

    /**
     * 数据加载后处理
     * @return
     */
    boolean doAfterLoaded();

    /**
     * 登陆消息发出前处理
     * @return
     */
    boolean doBeforeLogin();

    /**
     * 登陆消息发出后处理
     * @return
     */
    boolean doAfterLogin();

    /**
     * 退出时处理
     * @return
     */
    boolean doAfterLogout();

    /**
     * 每日重置
     */
    void resetDaily(boolean isLogin);

    /**
     * 定时执行方法, 该任务只处理玩家相关的事
     */
    void tick(long now);

}
