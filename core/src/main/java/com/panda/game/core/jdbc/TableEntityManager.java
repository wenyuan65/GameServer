package com.panda.game.core.jdbc;

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

    private String databaseName;
    private DataSource dataSource;
    private Map<String, TableEntity> tableMap = new HashMap<>();

    private NameStrategy nameStrategy = new DefaultNameStrategy();

    public boolean init(String path) {
        Set<Class<?>> classSet = ScanUtil.scan(path);
        for (Class<?> clazz : classSet) {
            TableField tableField = clazz.getDeclaredAnnotation(TableField.class);
            if (tableField == null) {
                continue;
            }

            try {
                TableEntity table = new TableEntity(clazz);
                table.setDataSource(this.dataSource);
                table.setNameStrategy(nameStrategy);
                table.init();
                tableMap.put(clazz.getName(), table);

                log.info("init table {}", clazz.getName());
            } catch (Exception e) {
                log.error("初始化{}异常", e, clazz.getName());
                return false;
            }
        }

        return true;
    }

}
