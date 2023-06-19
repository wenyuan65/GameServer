package com.panda.game.common.constants;

/**
 * 数据库类型
 */
public enum DataBaseType {

    Logic("logic", "逻辑服数据库"),
    Login("login", "登陆服数据库"),

    ;

    private String name;
    private String intro;

    DataBaseType(String name, String intro) {
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
