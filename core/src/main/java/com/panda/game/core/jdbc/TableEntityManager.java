package com.panda.game.core.jdbc;

import com.panda.game.common.constants.DataBaseType;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.utils.ScanUtil;
import com.panda.game.core.jdbc.annotation.TableField;
import com.panda.game.core.jdbc.entity.TableEntity;
import com.panda.game.core.jdbc.name.DefaultNameStrategy;
import com.panda.game.core.jdbc.name.NameStrategy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TableEntityManager {

    private static Logger log = LoggerFactory.getLogger(TableEntityManager.class);

    private static final TableEntityManager instance = new TableEntityManager();

    private TableEntityManager() {
    }

    public static TableEntityManager getInstance() {
        return instance;
    }

    private Map<Class<?>, TableEntity> tableMap = new HashMap<>();

    private NameStrategy nameStrategy = new DefaultNameStrategy();

    public boolean init(String path, DataBaseType database) {
        DataSource ds = PoolManager.getInstance().getDataSource(database);
        if (ds == null) {
            throw new RuntimeException("没有找到对应的数据源, " + database);
        }

        Set<Class<?>> classSet = ScanUtil.scan(path);
        log.info("初始化数据库{}实体表", database.getName());
        for (Class<?> clazz : classSet) {
            TableField tableField = clazz.getDeclaredAnnotation(TableField.class);
            if (tableField == null) {
                continue;
            }

            try {
                TableEntity<?> table = new TableEntity<>(clazz);
                table.setDatabase(database);
                table.setNameStrategy(nameStrategy);
                table.init(ds);
                tableMap.put(clazz, table);

                log.info("init table {}", clazz.getName());
            } catch (Exception e) {
                log.error("初始化{}异常", e, clazz.getName());
                return false;
            }
        }

        return true;
    }

    public <T> TableEntity<T> getTableEntity(Class<T> clazz) {
        return tableMap.get(clazz);
    }

}
