package com.panda.game.common.constants;

public interface RedisKey {

    // 订阅发布命令头节点
    public static final String Redis_PubSub_Topic_Suffix = "Redis_PubSub_Topic_";

    /**
     * 订阅发布的主题名
     * @param cmd
     * @return
     */
    public static String getPubSubTopic(int cmd) {
        return Redis_PubSub_Topic_Suffix + cmd;
    }

}
