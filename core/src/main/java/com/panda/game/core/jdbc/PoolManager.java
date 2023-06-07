package com.panda.game.core.jdbc;

import com.panda.game.common.config.Configuration;
import com.panda.game.common.utils.StringUtils;
import com.panda.game.core.jdbc.pool.HikariPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PoolManager {

    private static final PoolManager instance = new PoolManager();

    private PoolManager() {
    }

    public static PoolManager getInstance() {
        return instance;
    }

    private Map<String, HikariPool> poolMap = new HashMap<>();

    public boolean init() {
        String databaseList = Configuration.getProperty("database.list");
        String[] databases = StringUtils.split(databaseList, ",");

        for (String database : databases) {
            HikariPool pool = new HikariPool(database);
            pool.init();

            poolMap.put(database, pool);
        }

        return true;
    }

    public Connection getConnection(String database) throws SQLException {
        return poolMap.get(database).getConnection();
    }

    public DataSource getDataSource(String database) {
        return poolMap.get(database).getDataSource();
    }

}
