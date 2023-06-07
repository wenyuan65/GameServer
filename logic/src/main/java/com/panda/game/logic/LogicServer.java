package com.panda.game.logic;

import com.panda.game.common.concrrent.ServerThreadManager;
import com.panda.game.common.config.Configuration;
import com.panda.game.common.utils.MixUtil;
import com.panda.game.core.BaseServer;
import com.panda.game.core.cache.CacheFactory;
import com.panda.game.core.cmd.CommandManager;
import com.panda.game.core.common.ServerConfig;
import com.panda.game.core.interceptor.*;
import com.panda.game.core.jdbc.PoolManager;
import com.panda.game.core.jdbc.TableEntityManager;
import com.panda.game.core.netty.NettyServer;
import com.panda.game.core.netty.NettyServerConfig;
import com.panda.game.core.netty.handler.LogicHandler;
import com.panda.game.core.netty.initializer.TcpChannelInitializer;
import com.panda.game.core.redis.RedisManager;
import com.panda.game.logic.base.ModuleServiceHelper;
import com.panda.game.logic.common.GamePlayerInjector;
import com.panda.game.logic.common.WorldManager;
import com.panda.game.logic.player.GamePlayer;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

public class LogicServer extends BaseServer {

    public static void main(String[] args) {
        LogicServer server = new LogicServer();
        server.start();
    }

    @Override
    public void start() {
        // 获取项目的总路径
        String projectPackage = MixUtil.getParent(LogicServer.class.getPackage());

        // 初始化线程组
        ServerThreadManager.getInstance().init(8, 8);
        // 初始化命令组件
        initCmdHandler(projectPackage);

        // 初始化数据库连接池
        PoolManager.getInstance().init();
        // 扫描表
        TableEntityManager.getInstance().init(projectPackage, "logic");
        // 初始化redis
        initRedis();

        // TODO: 初始化ID管理组件

        // 加载静态数据
        CacheFactory.init(projectPackage);
        // 加载玩家模块类
        ModuleServiceHelper.init();
        // 初始化公共模块类
        WorldManager.getInstance().init();

        // TODO: 初始化定时器、加载定时任务

        // 初始化Netty服务器
        ServerConfig serverConfig = new ServerConfig();
        NettyServerConfig nettyServerConfig = new NettyServerConfig();

        NettyServer nettyServer = new NettyServer("LogicTcpServer", nettyServerConfig, new TcpChannelInitializer(serverConfig, LogicHandler.class));
        nettyServer.start();

        // TODO: 服务注册
    }

    private static void initCmdHandler(String projectPackage) {
        // 添加拦截器
        CommandManager.getInstance().addInterceptor(new AuthInterceptor());
        CommandManager.getInstance().addInterceptor(new RateLimitInterceptor());
        CommandManager.getInstance().addInterceptor(new TraceInterceptor());
        CommandManager.getInstance().addInterceptor(new LogInterceptor());
        CommandManager.getInstance().addInterceptor(new BaseInterceptor());
        // 添加参数注入器
        CommandManager.getInstance().registerInjector(GamePlayer.class, GamePlayerInjector.class);

        CommandManager.getInstance().init(projectPackage);
    }

    public static boolean initRedis() {
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

}
