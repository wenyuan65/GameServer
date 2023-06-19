package com.panda.game.core;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.panda.game.common.config.Configuration;
import com.panda.game.common.constants.DataBaseType;
import com.panda.game.common.constants.GlobalType;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.utils.MixUtil;
import com.panda.game.common.utils.StringUtils;
import com.panda.game.core.jdbc.PoolManager;
import com.panda.game.core.jdbc.TableEntityManager;
import com.panda.game.core.jdbc.common.JdbcUtils;
import com.panda.game.core.nacos.NodeManager;
import com.panda.game.core.redis.RedisManager;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class BaseServer {

    protected static Logger log = LoggerFactory.getLogger(BaseServer.class);

    protected static Map<DataBaseType, String> databasePackageMap = new HashMap<>();
    protected static Map<DataBaseType, String> databaseVersionMap = new HashMap<>();

    static {
        databasePackageMap.put(DataBaseType.Logic, "com.panda.game.dao.entity.logic");
        databasePackageMap.put(DataBaseType.Login, "com.panda.game.dao.entity.login");

        databaseVersionMap.put(DataBaseType.Logic, "0.0.1.0");
        databaseVersionMap.put(DataBaseType.Login, "0.0.1.0");
    }

    protected boolean initDatabase(List<DataBaseType> databaseList) {
        if (!PoolManager.getInstance().init()) {
            log.error("初始化数据库连接池异常");
            return false;
        }

        for (DataBaseType database : databaseList) {
            if (!TableEntityManager.getInstance().init(getScanPackage(database), database)) {
                log.error("数据库扫描异常");
                return false;
            }

            String logicVersion = getLocalDatabaseVersion(database);
            String acceptVersion = getVersion(database);
            if (StringUtils.isBlank(logicVersion) || StringUtils.isBlank(acceptVersion) || MixUtil.compareVersion(logicVersion, acceptVersion) != 0) {
                log.error("数据库版本号与服务器不一致, 数据库版本号{}，配置版本号：{}", logicVersion, acceptVersion);
                return false;
            }
        }

        return true;
    }

    protected String getLocalDatabaseVersion(DataBaseType database) {
        DataSource dataSource = PoolManager.getInstance().getDataSource(database);
        String sql = "select value as version from global_value where id = " + GlobalType.Version.getId();

        List<Map<String, Object>> mapList = JdbcUtils.queryMapList(dataSource, sql);
        if (mapList == null || mapList.size() <= 0) {
            return null;
        }

        return (String)mapList.get(0).get("version");
    }

    protected boolean registerServers() {
        try {
            String clusterServerList = Configuration.getProperty("cluster.server.list");

            String gameName = Configuration.getProperty("node.app");
            String nodeType = Configuration.getProperty("node.type");
            String cluster = Configuration.getProperty("node.cluster");
            int nodeId = Configuration.getIntProperty("node.id", 0);
            String ip = Configuration.getProperty("node.ip");
            int port = Configuration.getIntProperty("node.port", 0);

            NodeManager.getInstance().init(clusterServerList, gameName);
            List<Instance> instances = new ArrayList<>();

            Instance instance1 = NodeManager.createInstance(cluster, nodeType, nodeId, ip, port);
            instances.add(instance1);

            NodeManager.getInstance().register(nodeType, instances);
        } catch (Exception e) {
            log.error("服务器启动异常", e);
            return false;
        }

        return true;
    }

    protected boolean initRedis() {
        try {
            String host = Configuration.getProperty("redis.host");
            String password = Configuration.getProperty("redis.password");
            String database = Configuration.getProperty("redis.database");
            String idleSize = Configuration.getProperty("redis.idleSize");

            Config config = new Config();
            config.setCodec(JsonJacksonCodec.INSTANCE);
            config.useSingleServer()
                    .setAddress(host)
                    .setPassword(password)
                    .setConnectionMinimumIdleSize(Integer.parseInt(idleSize))
                    .setDatabase(Integer.parseInt(database));
            RedisManager.getInstance().init(config);
        } catch (Throwable e) {
            log.error("初始化异常", e);
            return false;
        }

        return true;
    }

    public abstract void start();

    protected boolean init(Callable<Boolean> callable, String name) {
        if (callable == null) {
            return false;
        }

        long start = System.currentTimeMillis();
        Boolean result = null;
        try {
            result = callable.call();
        } catch (Exception e) {
            log.error("执行异常", e);
            return false;
        }
        if (result == null || !result) {
            return false;
        }

        log.info("{}, 耗时:{} ms", name, (System.currentTimeMillis() - start));
        return true;
    }

    protected String getVersion(DataBaseType database) {
        return databaseVersionMap.get(database);
    }

    protected String getScanPackage(DataBaseType database) {
        return databasePackageMap.get(database);
    }

}
