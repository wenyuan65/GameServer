package com.panda.game.logic;

import com.panda.game.common.concrrent.ServerThreadManager;
import com.panda.game.common.utils.MixUtil;
import com.panda.game.core.cache.CacheFactory;
import com.panda.game.core.cmd.CommandManager;
import com.panda.game.core.common.ServerConfig;
import com.panda.game.core.interceptor.*;
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

import java.util.Arrays;

public class LogicServer {

    public static void main(String[] args) {
        // 获取项目的总路径
        String projectPackage = MixUtil.getParent(LogicServer.class.getPackage());

        // 初始化线程组
        ServerThreadManager.getInstance().init(8, 8);
        // 初始化命令组件
        CommandManager.getInstance().addInterceptor(Arrays.asList(new AuthInterceptor(), new BaseInterceptor(),
                new LogInterceptor(), new TraceInterceptor(), new RateLimitInterceptor()));
        CommandManager.getInstance().registerInjector(GamePlayer.class, GamePlayerInjector.class);
        CommandManager.getInstance().init(projectPackage);

        // TODO: 初始化数据库连接池

        TableEntityManager.getInstance().init(projectPackage);

        // 初始化redis
        Config config = new Config();
        config.setCodec(JsonJacksonCodec.INSTANCE);
        config.useSingleServer().setAddress("127.0.0.1:6799").setPassword("123456").setConnectionMinimumIdleSize(4).setDatabase(0);
        RedisManager.getInstance().init(config);

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

}
