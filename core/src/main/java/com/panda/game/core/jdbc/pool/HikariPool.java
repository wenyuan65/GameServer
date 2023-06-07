package com.panda.game.core.jdbc.pool;

import com.panda.game.common.config.Configuration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class HikariPool {

    private String poolName;

    private DataSource dataSource;

    public HikariPool(String poolName) {
        this.poolName = poolName;
    }

    public void init() {
        String name = Configuration.getProperty(String.format("jdbc.%s.datasource.name", poolName));
        String driver = Configuration.getProperty(String.format("jdbc.%s.datasource.driver", poolName));
        String url = Configuration.getProperty(String.format("jdbc.%s.datasource.url", poolName));
        String username = Configuration.getProperty(String.format("jdbc.%s.datasource.username", poolName));
        String password = Configuration.getProperty(String.format("jdbc.%s.datasource.password", poolName));
        String minimumSize = Configuration.getProperty(String.format("jdbc.%s.datasource.minimumSize", poolName));
        String maximumSize = Configuration.getProperty(String.format("jdbc.%s.datasource.maximumSize", poolName));
        int minSize = Integer.parseInt(minimumSize);
        int maxSize = Integer.parseInt(maximumSize);

        HikariConfig config = new HikariConfig();
        config.setPoolName("HikariPool-" + poolName);
        config.setDataSourceClassName(name);
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setConnectionTimeout(30000); // 默认30秒
        config.setIdleTimeout(600000); // 默认10分钟
        config.setMaxLifetime(1800000); // 默认30分钟
        config.setMinimumIdle(minSize);
        config.setMaximumPoolSize(maxSize);

        dataSource = new HikariDataSource(config);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public String getPoolName() {
        return poolName;
    }
}
