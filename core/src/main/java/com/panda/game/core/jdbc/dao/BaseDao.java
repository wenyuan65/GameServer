package com.panda.game.core.jdbc.dao;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.core.jdbc.PoolManager;
import com.panda.game.core.jdbc.TableEntityManager;
import com.panda.game.core.jdbc.base.BaseEntity;
import com.panda.game.core.jdbc.common.JdbcUtils;
import com.panda.game.core.jdbc.entity.TableEntity;

import javax.sql.DataSource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDao<T> {

    private static Logger log = LoggerFactory.getLogger(BaseDao.class);
    private static final Logger asyncLogger = LoggerFactory.getLogger("async");

    private Class<T> clazz;
    private  TableEntity<T> tableEntity;

    public void init() {
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
            clazz = (Class<T>) typeArguments[0];
        } else {
            throw new RuntimeException("未知的泛型类型");
        }

        this.tableEntity = TableEntityManager.getInstance().getTableEntity(clazz);
    }

    /**
     * 查询整张表
     * @return
     */
    public List<T> getAll() {
        return JdbcUtils.execute(getDataSource(), this.tableEntity.getSelectAllSQL(), ps -> {
            ResultSet rs = ps.executeQuery();
            List<T> list;
            try {
                list = this.tableEntity.convert(rs);
            } finally {
                JdbcUtils.close(rs);
            }

            return list;
        });
    }

    /**
     * 通过primary key查询记录
     * @param keys
     * @return
     */
    public List<T> getAllByPrimaryKey(Object... keys) {
        return JdbcUtils.execute(getDataSource(), this.tableEntity.getSelectByPlayerIdSQL(), ps -> {
            this.tableEntity.setSelectSQLParams(ps, keys);

            ResultSet rs = ps.executeQuery();
            List<T> list;
            try {
                list = this.tableEntity.convert(rs);
            } finally {
                JdbcUtils.close(rs);
            }

            return list;
        });
    }

    /**
     * 查询玩家的所有数据
     * @param playerId
     * @return
     */
    public List<T> getAllByPlayerId(long playerId) {
        return JdbcUtils.execute(getDataSource(), this.tableEntity.getSelectByPlayerIdSQL(), ps -> {
            this.tableEntity.setSelectByPlayerIdSQLParams(ps, playerId);

            ResultSet rs = ps.executeQuery();
            List<T> list;
            try {
                list = this.tableEntity.convert(rs);
            } finally {
                JdbcUtils.close(rs);
            }

            return list;
        });
    }

    /**
     * 插入数据
     * @param obj
     * @return
     */
    public boolean add(T obj) {
        if (!(obj instanceof BaseEntity)) {
            return false;
        }
        BaseEntity entity = (BaseEntity) obj;
        if (!entity.isAddOption()) {
            return false;
        }

        Integer result = JdbcUtils.execute(getDataSource(), this.tableEntity.getInsertSQL(), ps -> {
            this.tableEntity.setInsertOrReplaceSQLParams(ps, obj);
            return ps.executeUpdate();
        });

        if (result == null || result != 1) {
            log.info("sql 执行异常");
        }

        entity.clearOption();

        return true;
    }

    /**
     * 新增或者更新数据
     * @param obj
     * @return
     */
    public boolean addOrUpdate(T obj) {
        if (!(obj instanceof BaseEntity)) {
            return false;
        }
        BaseEntity entity = (BaseEntity) obj;
        if (!entity.isAddOrUpdateOption()) {
            return false;
        }

        Integer result = JdbcUtils.execute(getDataSource(), this.tableEntity.getAddOrUpdateSQL(), ps -> {
            this.tableEntity.setInsertOrReplaceSQLParams(ps, obj);
            return ps.executeUpdate();
        });

        if (result == null || result != 1) {
            log.info("sql 执行异常");
        }

        entity.clearOption();

        return true;
    }

    /**
     * 批量更新
     * @param list
     * @return
     */
    public boolean addOrUpdateBatch(List<T> list) {
        List<T> candidateList = new ArrayList<>(list.size());
        for (T obj : list) {
            if (!(obj instanceof BaseEntity)) {
                continue;
            }
            BaseEntity entity = (BaseEntity) obj;
            if (!entity.isAddOrUpdateOption()) {
                continue;
            }
            candidateList.add(obj);
        }

        if (candidateList.size() == 0) {
            return false;
        }

        int[] result = JdbcUtils.execute(getDataSource(), this.tableEntity.getAddOrUpdateSQL(), ps -> {
            for (T obj : candidateList) {
                this.tableEntity.setInsertOrReplaceSQLParams(ps, obj);
                ps.addBatch();
            }

            return ps.executeBatch();
        });

        int failNo = 0;
        for (int i = 0; i < candidateList.size(); i++) {
            if (result[i] >= 0 || result[i] == Statement.SUCCESS_NO_INFO) {
                T obj = candidateList.get(i);
                BaseEntity entity = (BaseEntity) obj;
                entity.clearOption();
            } else {
                failNo ++;
            }
        }
        if (failNo > 0) {
            log.error("addOrUpdate批处理SQL执行异常, 失败:{}, 总数：{}", failNo, candidateList.size());
        }

        return true;
    }

    /**
     * 更新数据
     * @param obj
     * @return
     */
    public boolean update(T obj) {
        if (!(obj instanceof BaseEntity)) {
            return false;
        }
        BaseEntity entity = (BaseEntity) obj;
        if (!entity.isUpdateOption()) {
            return false;
        }

        Integer result = JdbcUtils.execute(getDataSource(), this.tableEntity.getUpdateSQL(), ps -> {
            this.tableEntity.setUpdateSQLParams(ps, obj);
            return ps.executeUpdate();
        });

        if (result == null || result != 1) {
            log.info("sql 执行异常");
        }

        entity.clearOption();

        return true;
    }

    /**
     * 删除数据
     * @param obj
     * @return
     */
    public boolean delete(T obj) {
        if (!(obj instanceof BaseEntity)) {
            return false;
        }
        BaseEntity entity = (BaseEntity) obj;
        if (!entity.isDeleteOption()) {
            return false;
        }

        Integer result = JdbcUtils.execute(getDataSource(), this.tableEntity.getDeleteSQL(), ps -> {
            this.tableEntity.setDeleteSQLParams(ps, obj);
            return ps.executeUpdate();
        });

        if (result == null) {
            log.info("sql 执行异常");
        }

        entity.clearOption();

        return true;
    }

    /**
     * 批量删除
     * @param list
     * @return
     */
    public boolean deleteBatch(List<T> list) {
        List<T> candidateList = new ArrayList<>(list.size());
        for (T obj : list) {
            if (!(obj instanceof BaseEntity)) {
                continue;
            }
            BaseEntity entity = (BaseEntity) obj;
            if (!entity.isAddOrUpdateOption()) {
                continue;
            }
            candidateList.add(obj);
        }

        if (candidateList.size() == 0) {
            return false;
        }

        int[] result = JdbcUtils.execute(getDataSource(), this.tableEntity.getDeleteSQL(), ps -> {
            for (T obj : candidateList) {
                this.tableEntity.setDeleteSQLParams(ps, obj);
                ps.addBatch();
            }

            return ps.executeBatch();
        });

        int failNo = 0;
        for (int i = 0; i < candidateList.size(); i++) {
            if (result[i] >= 0 || result[i] == Statement.SUCCESS_NO_INFO) {
                T obj = candidateList.get(i);
                BaseEntity entity = (BaseEntity) obj;
                entity.clearOption();
            } else {
                failNo ++;
            }
        }
        if (failNo > 0) {
            log.error("delete批处理SQL执行异常, 失败:{}, 总数：{}", failNo, candidateList.size());
        }

        return true;
    }

    /**
     * 清空表
     */
    public void truncateTable() {
        JdbcUtils.execute(getDataSource(), this.tableEntity.getDeleteAllSQL(), ps -> ps.executeUpdate());
    }

    /**
     * 查询id字段最大值
     */
    public long getMaxId() {
        List<Map<String, Object>> maps = queryMap(this.tableEntity.getMaxIdSQL());
        Map<String, Object> map = maps.get(0);
        Object id = map.get("id");
        if (id instanceof Long) {
            return (long) id;
        }

        return (long)((int)id);
    }

    /**
     * 将查询的结果转换成map
     * @param sql
     * @return
     */
    public List<Map<String, Object>> queryMap(String sql) {
        return JdbcUtils.execute(getDataSource(), sql, ps -> {
            ResultSet rs = ps.executeQuery();
            try {
                return convertMap(rs);
            } finally {
                JdbcUtils.close(rs);
            }
        });
    }

    /**
     * 将数据库查询结果resultSet转化为Map列表<br/>
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private List<Map<String, Object>> convertMap(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int size = metaData.getColumnCount();

        List<String> columnNameList = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            String columnName = metaData.getColumnName(i);
            columnNameList.add(columnName);
        }

        while (resultSet.next()) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 1; i <= size; i++) {
                String key = columnNameList.get(i - 1);
                Object value = resultSet.getObject(i);
                map.put(key, value);
            }

            list.add(map);
        }

        return list;
    }

    public DataSource getDataSource() {
        return PoolManager.getInstance().getDataSource(tableEntity.getDatabase());
    }

}
