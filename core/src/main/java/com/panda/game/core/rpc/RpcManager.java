package com.panda.game.core.rpc;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import com.googlecode.protobuf.format.JsonFormat;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.timer.Scheduler;
import com.panda.game.core.netty.NettyClientConfig;
import com.panda.game.core.rpc.connection.*;
import com.panda.game.core.rpc.future.RpcFuture;
import com.panda.game.proto.CmdPb;
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
    // rpc请求超时时间
    private long timeout = 5000;

    public boolean init(NettyClientConfig config) {
        this.timeout = config.getTimeout();

        RpcClientInitializer clientInitializer = new RpcClientInitializer();
        ConnectionFactory connectionFactory = new DefaultConnectionFactory(config, clientInitializer);
        ConnectionManager connectionManager = new DefaultConnectionManager(connectionFactory);
        connectionManager.init();

        return true;
    }

    /**
     * 发送同步rpc请求，会阻塞当前进程
     * @param host ip
     * @param port 端口
     * @param cmd 接口id
     * @param requestMessage 请求消息
     * @param parser 返回消息协议的解析器
     * @return
     * @param <T>
     */
    public <T extends GeneratedMessageV3> T sendSync(String host, int port, int cmd, GeneratedMessageV3 requestMessage, Parser<T> parser) {
        RpcRequest request = createRequest(host, port, cmd, requestMessage);
        RpcResponse response = new RpcResponse(request.getRequestId());

        logger.info("#RPC#{}#1#{}", request.getRequestId(), JsonFormat.printToString(requestMessage));
        try {
            Connection connection = connectionManager.get(request.getHost(), request.getPort());
            RpcFuture future = connection.sendRequest(request, response, null);
            future.waitResponse(timeout);
            // 移除
            connection.removeFuture(future);

            if (!future.isDone()) { // 超时
                response.setErrorCode(CmdPb.ErrorCode.RpcTimeOut_VALUE);
            } else if (future.getErrorCode() != CmdPb.ErrorCode.Ok_VALUE) {
                response.setErrorCode(future.getErrorCode());
            }

            if (response.getErrorCode() != CmdPb.ErrorCode.Ok_VALUE) {
                logger.info("#RPC#{}#3#{}", request.getRequestId(), CmdPb.ErrorCode.forNumber(response.getErrorCode()).name());
                return null;
            }

            // 解析rpc返回的协议
            T result = parser.parseFrom(response.getResult());
            logger.info("#RPC#{}#2#{}", request.getRequestId(), result != null ? JsonFormat.printToString(result) : "NULL");

            return result;
        } catch (Throwable e) {
            logger.info("#RPC#{}#3#{}", request.getRequestId(), e.getMessage());
        }

        return null;
    }

    /**
     * 发送异步rpc请求，不会阻塞当前进程
     * @param host ip
     * @param port 端口
     * @param cmd 接口id
     * @param requestMessage 请求消息
     * @param callback 异步执行的回调，回调的执行在rpc客户端的netty消息处理线程中执行，如果需要保证执行环境，需要通过ServerThreadManager中的runIn()等方法去执行实际逻辑<br/>
     *                 例如：<code>sendAsync("127.0.0.1", 8808, cmd, SomeMessage, (request, response) ->
     *                          ServerThreadManager.getInstance().runIn(() -> {
     *                              system.out.println("success");
     *                       }, 0))<code/>
     */
    public void sendAsync(String host, int port, int cmd, GeneratedMessageV3 requestMessage, Callback callback) {
        RpcRequest request = createRequest(host, port, cmd, requestMessage);

        logger.info("#RPC#{}#1#{}", request.getRequestId(), JsonFormat.printToString(requestMessage));
        try {
            Connection connection = connectionManager.get(request.getHost(), request.getPort());
            connection.sendRequest(request, new RpcResponse(request.getRequestId()), callback);

            // 添加定时移除的逻辑
            Scheduler.schedule(() -> {
                RpcFuture invokeFuture = connection.getFuture(request.getRequestId());
                if (invokeFuture == null) {
                    return;
                }
                connection.removeFuture(invokeFuture);
            }, timeout);
        } catch (Throwable e) {
            logger.info("#RPC#{}#3#{}", request.getRequestId(), e.getMessage());
        }
    }

    /**
     * 发送rpc请求，不需要关心是否发送成功和返回结果
     * @param host
     * @param port
     * @param cmd
     * @param requestMessage
     */
    public void sendOneway(String host, int port, int cmd, GeneratedMessageV3 requestMessage) {
        RpcRequest request = createRequest(host, port, cmd, requestMessage);

        logger.info("#RPC#{}#1#{}", request.getRequestId(), JsonFormat.printToString(requestMessage));
        try {
            Connection connection = connectionManager.get(request.getHost(), request.getPort());
            RpcFuture future = connection.sendRequest(request, null, null);
            connection.removeFuture(future);
        } catch (Throwable e) {
            logger.info("#RPC#{}#3#{}", request.getRequestId(), e.getMessage());
        }
    }

    private int getNextRequestId() {
        return rpcRequestIdCounter.decrementAndGet();
    }

    private RpcRequest createRequest(String host, int port, int cmd, GeneratedMessageV3 requestMessage) {
        PacketPb.Pkg.Builder pkg = PacketPb.Pkg.newBuilder();
        pkg.setCmd(cmd);
        pkg.setRequestId(getNextRequestId());
        pkg.setBody(requestMessage.toByteString());

        RpcRequest request = new RpcRequest(getNextRequestId(), pkg.build());
        request.setHost(host);
        request.setPort(port);

        return request;
    }

}
