package com.panda.game.common.constants;

public enum GlobalType {

    Version(1, "版本号"),
    Server_Start_Time(2, "开服时间"),
    ;

    private int id;
    private String intro;

    GlobalType(int id, String intro) {
        this.id = id;
        this.intro = intro;
    }

    public int getId() {
        return id;
    }

    public String getIntro() {
        return intro;
    }
}
