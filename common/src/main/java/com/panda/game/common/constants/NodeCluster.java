package com.panda.game.common.constants;

public enum NodeCluster {
    Common("common", "公共区"),
    Appstore("appstore", "苹果区"),
    Android("android", "安卓区"),
    ;

    private String name;
    private String intro;

    NodeCluster(String name, String intro) {
        this.name = name;
        this.intro = intro;
    }

    public String getName() {
        return name;
    }

    public String getIntro() {
        return intro;
    }

    public static NodeCluster getNodeCluster(String nodeCluster) {
        for (NodeCluster value : values()) {
            if (value.getName().equals(nodeCluster)) {
                return value;
            }
        }

        throw new IllegalArgumentException("未知的NodeCluster:" + nodeCluster);
    }

}
