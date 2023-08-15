package com.panda.game.logic.constants;

public enum DropSource {

    GM(1, "gm命令"),

    ;

    private int source;
    private String desc;

    DropSource(int source, String desc) {
        this.source = source;
        this.desc = desc;
    }

    public int getSource() {
        return source;
    }
}
