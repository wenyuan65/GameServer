package com.panda.game.core.jdbc;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.core.jdbc.common.PreparedStatementHandler;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class BaseDao<T> {

    private static Logger log = LoggerFactory.getLogger(BaseDao.class);
    private static final Logger asyncLogger = LoggerFactory.getLogger("async");






    public List<Map<String, Object>> queryListMap(DataSource dataSource, String sql) {
        return execute(dataSource, sql, ps -> {
            ResultSet rs = ps.executeQuery();
            List<Map<String, Object>> result = new ArrayList<>();
            try {
                ResultSetMetaData metaData = rs.getMetaData();
                int size = metaData.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> columnMap = new HashMap<>();
                    for (int i = 1; i <= size; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = rs.getObject(i);

                        columnMap.put(columnName, value);
                    }
                    result.add(columnMap);
                }
            } finally {
                close(rs);
            }
            return result != null ? result : Collections.emptyList();
        });
    }

    public <T> T execute(DataSource dataSource, String sql, PreparedStatementHandler<T> handler) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(sql);

            return handler.handle(ps);
        } catch (Throwable e) {
            log.error("execute sql error", e);
            asyncLogger.error("{}#{}#", sql, 2);
        } finally {
            close(connection, ps);
        }

        return null;
    }

    public void close(Connection connection, Statement ps, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("关闭数据库出错", e);
            }
        }
        close(connection, ps);
    }

    public void close(Connection connection, Statement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                log.error("关闭数据库出错", e);
            }
        }

        close(connection);
    }

    public void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("关闭数据库出错", e);
            }
        }
    }

    public void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("关闭数据库出错", e);
            }
        }
    }

}
