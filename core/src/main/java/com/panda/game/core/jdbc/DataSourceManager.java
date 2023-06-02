package com.panda.game.core.jdbc;

public class DataSourceManager {

    private static final DataSourceManager instance = new DataSourceManager();

    private DataSourceManager() {
    }

    public static DataSourceManager getInstance() {
        return instance;
    }



}
