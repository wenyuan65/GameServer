package com.panda.game.core.jdbc.pool;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.core.jdbc.common.JdbcUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionPool {

    private static Logger log = LoggerFactory.getLogger(ConnectionPool.class);

    private String driver;
    private String url;
    private String username;
    private String password;
    // 连接池的数量
    private int size;

    private CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<>();

    public ConnectionPool(String driver, String url, String username, String password, int size) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public boolean init() {
        try {
            Class.forName(driver);

            createConnection();




        } catch (Throwable e) {
            log.error("初始化数据库失败", e);
            return false;
        }

        return true;
    }

    private Connection createConnection() throws Throwable {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Connection connection = DriverManager.getConnection(url, username, password);

        return (Connection) Proxy.newProxyInstance(classLoader, new Class[]{Connection.class}, new ConnectionProxyHandler(connection, this));
    }

    public Connection getConnection() {
        if (connections.size() > 0) {
            Connection connection = connections.remove(0);
            if (connection != null) {
                return connection;
            }
        }

        return null;
    }

    public void close(Connection connection) {
        connections.add(connection);
    }

    public void destroy(Connection connection) {
        if (connection instanceof  Proxy) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(connection);
            if (invocationHandler instanceof ConnectionProxyHandler) {
                Connection rawConnection = ((ConnectionProxyHandler) invocationHandler).getConnection();
                JdbcUtils.close(rawConnection);
                return;
            }
        }

        JdbcUtils.close(connection);
    }

}
