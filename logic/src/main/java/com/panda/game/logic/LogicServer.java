package com.panda.game.logic;

import com.panda.game.common.concrrent.ServerThreadManager;
import com.panda.game.common.config.Configuration;
import com.panda.game.common.constants.DataBaseType;
import com.panda.game.core.BaseServer;
import com.panda.game.core.cache.CacheFactory;
import com.panda.game.core.cmd.CommandManager;
import com.panda.game.core.common.ServerConfig;
import com.panda.game.core.interceptor.*;
import com.panda.game.core.netty.NettyServer;
import com.panda.game.core.netty.NettyServerConfig;
import com.panda.game.core.netty.handler.PacketCommandHandler;
import com.panda.game.core.netty.initializer.TcpChannelInitializer;
import com.panda.game.core.proto.ProtoManager;
import com.panda.game.logic.base.ModuleServiceHelper;
import com.panda.game.logic.common.EntityIdManager;
import com.panda.game.logic.common.GamePlayer;
import com.panda.game.logic.common.GamePlayerInjector;
import com.panda.game.logic.world.ScheduleTaskManager;
import com.panda.game.logic.world.WorldManager;

import java.util.Arrays;
import java.util.List;

public class LogicServer extends BaseServer {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try {
            LogicServer server = new LogicServer();
            server.start();
        } catch (Throwable e) {
            log.error("服务器启动异常");
            System.err.println("服务器启动异常");
        }
        log.info("服务器启动结束， 耗时：{}ms", (System.currentTimeMillis() - start));
    }

    @Override
    public void start() {
        super.start();

        // 基础
        if (!init(() -> Configuration.init(Arrays.asList("node", "server")), "初始化配置文件")) {
            return ;
        }
        if (!init(() -> EntityIdManager.getInstance().init(), "初始化ID管理组件")) {
            return ;
        }
        if (!init(() -> ServerThreadManager.getInstance().init(8, 8), "初始化线程组")) {
            return ;
        }
        if (!init(() -> ProtoManager.getInstance().init(), "初始化协议管理器")) {
            return ;
        }

        // 数据
        List<DataBaseType> databaseList = Arrays.asList(DataBaseType.Logic);
        if (!init(() -> initDatabase(databaseList), "初始化命令组件")) {
            return ;
        }
        if (!init(() -> CacheFactory.init(projectPackage), "加载静态数据")) {
            return ;
        }
        if (!init(() -> initRedis(), "初始化redis")) {
            return ;
        }

        // 模块
        if (!init(() -> initCmdHandler(projectPackage), "初始化命令组件")) {
            return ;
        }
        if (!init(() -> ModuleServiceHelper.init(), "加载玩家模块类")) {
            return ;
        }
        if (!init(() -> WorldManager.getInstance().init(), "初始化公共模块类")) {
            return ;
        }
        if (!init(() -> ScheduleTaskManager.getInstance().init(), "初始化定时任务")) {
            return ;
        }

        // 网络
        if (!init(() -> initRpc(), "初始化RPC组件")) {
            return ;
        }
        if (!init(() -> initServer(), "初始化Netty服务器")) {
            return ;
        }
        if (!init(() -> registerServers(), "服务注册")) {
            return ;
        }
    }

    private boolean initServer() {
        // 初始化Netty服务器
        ServerConfig serverConfig = new ServerConfig();
        NettyServerConfig nettyServerConfig = new NettyServerConfig();

        try {
            NettyServer nettyServer = new NettyServer("LogicTcpServer", serverConfig, nettyServerConfig, new TcpChannelInitializer(serverConfig, PacketCommandHandler.class));
            nettyServer.init();
            nettyServer.start();
        } catch (Exception e) {
            log.error("服务器启动异常", e);
            return false;
        }

        return true;
    }

    private boolean initCmdHandler(String projectPackage) {
        // 添加拦截器
        CommandManager.getInstance().addInterceptor(new AuthInterceptor());
        CommandManager.getInstance().addInterceptor(new RateLimitInterceptor());
        CommandManager.getInstance().addInterceptor(new TraceInterceptor());
        CommandManager.getInstance().addInterceptor(new LogInterceptor());
        CommandManager.getInstance().addInterceptor(new BaseInterceptor());
        // 添加参数注入器
        CommandManager.getInstance().registerInjector(GamePlayer.class, GamePlayerInjector.class);

        return CommandManager.getInstance().init(projectPackage);
    }


}
