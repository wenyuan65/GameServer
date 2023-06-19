package com.panda.game.common.constants;

/**
 * 服务器类型
 */
public enum NodeType {

    Logic("logic", "逻辑服"),
    Login("login", "登陆服"),
    Gateway("gateway", "网关服"),
    Battle("battle", "战斗服"),
    Chat("chat", "聊天服"),
    Schedule("schedule", "匹配服"),
    ;

    private String name;
    private String intro;

    NodeType(String name, String intro) {
        this.name = name;
        this.intro = intro;
    }

    public String getName() {
        return name;
    }

    public String getIntro() {
        return intro;
    }

}
