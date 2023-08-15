package com.panda.game.logic.constants;

public enum ChangeType {

    Cost(0, "消耗"),
    Add(1, "增加"),
    ;

    private int type;
    private String desc;

    ChangeType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }
}
