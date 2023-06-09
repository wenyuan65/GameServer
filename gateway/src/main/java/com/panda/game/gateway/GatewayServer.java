package com.panda.game.gateway;

import com.panda.game.common.concrrent.ServerThreadManager;
import com.panda.game.common.config.Configuration;
import com.panda.game.core.BaseServer;
import com.panda.game.core.cmd.CommandManager;
import com.panda.game.core.common.ServerConfig;
import com.panda.game.core.interceptor.*;
import com.panda.game.core.netty.NettyServer;
import com.panda.game.core.netty.NettyServerConfig;
import com.panda.game.core.netty.handler.PacketCommandHandler;
import com.panda.game.core.netty.initializer.TcpChannelInitializer;
import com.panda.game.core.proto.ProtoManager;

import java.util.Arrays;

public class GatewayServer extends BaseServer {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try {
            GatewayServer server = new GatewayServer();
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
        if (!init(() -> ServerThreadManager.getInstance().init(8, 8), "初始化线程组")) {
            return ;
        }
        if (!init(() -> ProtoManager.getInstance().init(), "初始化协议管理器")) {
            return ;
        }

        // 数据
        if (!init(() -> initRedis(), "初始化redis")) {
            return ;
        }

        // 模块
        if (!init(() -> initCmdHandler(projectPackage), "初始化命令组件")) {
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
            NettyServer nettyServer = new NettyServer("GatewayTcpServer", serverConfig, nettyServerConfig, new TcpChannelInitializer(serverConfig));
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

        return CommandManager.getInstance().init(projectPackage);
    }

}
