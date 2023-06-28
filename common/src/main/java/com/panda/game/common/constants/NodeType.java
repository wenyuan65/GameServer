package com.panda.game.common.constants;

/**
 * 服务器类型
 */
public enum NodeType {

    Logic("logic", 1, "逻辑服"),
    Login("login", 2,"登陆服"),
    Gateway("gateway", 3, "网关服"),
    Battle("battle", 4, "战斗服"),
    Chat("chat", 5, "聊天服"),
    Schedule("schedule", 6, "匹配服"),
    ;

    private String name;
    private int type;
    private String intro;

    NodeType(String name, int type, String intro) {
        this.name = name;
        this.type = type;
        this.intro = intro;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getIntro() {
        return intro;
    }

    public static NodeType getNodeType(String nodeType) {
        for (NodeType value : values()) {
            if (value.getName().equals(nodeType)) {
                return value;
            }
        }

        throw new IllegalArgumentException("未知的NodeType:" + nodeType);
    }

    public static NodeType getNodeType(int nodeType) {
        for (NodeType value : values()) {
            if (value.getType() == nodeType) {
                return value;
            }
        }

        throw new IllegalArgumentException("未知的NodeType:" + nodeType);
    }

}
