package com.panda.game.tools.aoi;

import java.util.HashMap;
import java.util.Map;

public class AOIManager {

    private static final AOIManager instance = new AOIManager();

    private AOIManager() {
    }

    public static AOIManager getInstance() {
        return instance;
    }

    private Map<Integer, AOIMap> areaMap = new HashMap<>();

    public void init() {
        AOIMap map = new AOIMap(10, 10);
        areaMap.put(0, map);
    }

    public static void main(String[] args) {



    }


}
