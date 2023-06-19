package com.panda.game.core.rpc;

import com.google.protobuf.GeneratedMessageV3;
import com.panda.game.common.concrrent.ServerThreadManager;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.timer.Scheduler;
import com.panda.game.core.netty.NettyClientConfig;
import com.panda.game.core.rpc.connection.*;
import com.panda.game.core.rpc.future.InvokeFuture;
import com.panda.game.proto.PacketPb;

import java.util.concurrent.atomic.AtomicInteger;

public class RpcManager {

    private static final Logger logger = LoggerFactory.getLogger(RpcManager.class);

    private static final RpcManager instance = new RpcManager();

    private RpcManager() {
    }

    public static RpcManager getInstance() {
        return instance;
    }

    private ConnectionManager connectionManager;

    private AtomicInteger rpcRequestIdCounter = new AtomicInteger(0);

    private long timeout = 5000;

    public boolean init() {
        NettyClientConfig config = new NettyClientConfig();
        config.setEpoll(false);
        config.setEventGroupNum(4);
        config.setUsePool(true);

        RpcClientInitializer clientInitializer = new RpcClientInitializer();
        ConnectionFactory connectionFactory = new DefaultConnectionFactory(config, clientInitializer);
        ConnectionManager connectionManager = new DefaultConnectionManager(connectionFactory);
        connectionManager.init();

        return true;
    }

    public Object send(String host, int port, int cmd, GeneratedMessageV3 param) {
        PacketPb.Pkg.Builder pkg = PacketPb.Pkg.newBuilder();
        pkg.setCmd(cmd);
        pkg.setRequestId(getNextRequestId());
        pkg.setBody(param.toByteString());

        RpcRequest request = new RpcRequest(getNextRequestId(), cmd, param);
        request.setHost(host);
        request.setPort(port);

        RpcResponse response = new RpcResponse(request.getRequestId());
        try {
            client.invokeSync(request, response, timeout);
            if (response.getCause() != null) {

            }
            return response.getResult();
        } catch (Throwable e) {
            logger.error("RPC执行异常, {}, {}:{}", cmd, host, port);
        }

        return null;
    }

    public void invokeSync(RpcRequest request, RpcResponse response, long timeoutMs) throws InterruptedException {
        String address = request.getHost() + ":" + request.getPort();
        Connection connection = connectionManager.get(address);
        InvokeFuture future = connection.sendRequest(request, response, null);

        future.waitResponse(timeoutMs);

        if (!future.isDone() || future.getCause() != null) {
            connection.removeInvokeFuture(future);
        }
    }

    public void invokeAsync(RpcRequest request, Callback callback, long timeoutMs) {
        String address = request.getHost() + ":" + request.getPort();
        Connection connection = connectionManager.get(address);

        connection.sendRequest(request, new RpcResponse(request.getRequestId()), callback);

//        ServerThreadManager.getInstance().runAsync()

        Scheduler.schedule(() -> {
            InvokeFuture invokeFuture = connection.getInvokeFuture(request.getRequestId());
            if (invokeFuture == null) {
                return;
            }
            connection.removeInvokeFuture(invokeFuture);
        }, timeoutMs);
    }

    public void invokeOneway(RpcRequest request) {
        String address = request.getHost() + ":" + request.getPort();
        Connection connection = connectionManager.get(address);
        connection.sendRequest(request, null, null);
    }

    private int getNextRequestId() {
        return rpcRequestIdCounter.decrementAndGet();
    }

}
