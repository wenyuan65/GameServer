package com.panda.game.core;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.panda.game.common.config.Configuration;
import com.panda.game.common.constants.DataBaseType;
import com.panda.game.common.constants.GlobalType;
import com.panda.game.common.constants.NodeType;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.utils.MixUtil;
import com.panda.game.common.utils.StringUtils;
import com.panda.game.core.jdbc.PoolManager;
import com.panda.game.core.jdbc.TableEntityManager;
import com.panda.game.core.jdbc.common.JdbcUtils;
import com.panda.game.core.nacos.NodeHelper;
import com.panda.game.core.nacos.NodeManager;
import com.panda.game.core.netty.NettyClientConfig;
import com.panda.game.core.proto.ProtoManager;
import com.panda.game.core.redis.RedisManager;
import com.panda.game.core.rpc.RpcManager;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class BaseServer {

    protected static Logger log = LoggerFactory.getLogger(BaseServer.class);

    protected Map<DataBaseType, String> databasePackageMap = new HashMap<>();
    protected Map<DataBaseType, String> databaseVersionMap = new HashMap<>();

    // 总项目的公共包路径
    protected String projectPackage;
    // 子模块的包路径
    protected String modulePackage;
    // 获取当前项目的顶级目录
    protected String dir;

    public void start() {
        // 设置数据库实体类扫描范围
        databasePackageMap.put(DataBaseType.Logic, "com.panda.game.dao.entity.logic");
        databasePackageMap.put(DataBaseType.Login, "com.panda.game.dao.entity.login");
        // 设置数据库版本号
        databaseVersionMap.put(DataBaseType.Logic, "0.0.1.0");
        databaseVersionMap.put(DataBaseType.Login, "0.0.1.0");

        // 获取公共参数
        projectPackage = MixUtil.getParent(BaseServer.class.getPackage());
        modulePackage = getClass().getPackage().getName();
        dir = System.getProperty("dir");
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

            Instance instance1 = NodeHelper.createInstance(cluster, nodeType, nodeId, ip, port);
            instances.add(instance1);

            NodeManager.getInstance().register(NodeType.getNodeType(nodeType), instances);
        } catch (Exception e) {
            log.error("服务器启动异常", e);
            return false;
        }

        return true;
    }

    protected boolean initRpc() {
        NettyClientConfig config = new NettyClientConfig();
        config.setEpoll(false);
        config.setEventGroupNum(4);
        config.setUsePool(true);
        config.setTimeout(5000);

        return RpcManager.getInstance().init(config);
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

    protected boolean init(Callable<Boolean> callable, String name) {
        if (callable == null) {
            return false;
        }

        log.info("{} 开始", name);
        long start = System.currentTimeMillis();
        Boolean result = null;
        try {
            result = callable.call();
        } catch (Exception e) {
            log.error("执行异常", e);
            log.info("{} 异常", name);
            return false;
        }
        if (result == null || !result) {
            log.info("{} 失败", name);
            return false;
        }
        log.info("{} 结束, 耗时:{} ms", name, (System.currentTimeMillis() - start));

        return true;
    }

    protected String getVersion(DataBaseType database) {
        return databaseVersionMap.get(database);
    }

    protected String getScanPackage(DataBaseType database) {
        return databasePackageMap.get(database);
    }

}
