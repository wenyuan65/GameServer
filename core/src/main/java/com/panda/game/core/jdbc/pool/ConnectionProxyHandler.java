package com.panda.game.core.jdbc.pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

public class ConnectionProxyHandler implements InvocationHandler {

    private Connection connection;
    private ConnectionPool pool;

    public ConnectionProxyHandler(Connection connection, ConnectionPool pool) {
        this.connection = connection;
        this.pool = pool;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("close".equals(method.getName())) {
            pool.close((Connection) proxy);
            return null;
        }

        return method.invoke(connection, args);
    }

    public Connection getConnection() {
        return connection;
    }

    public ConnectionPool getDataPool() {
        return pool;
    }

}
