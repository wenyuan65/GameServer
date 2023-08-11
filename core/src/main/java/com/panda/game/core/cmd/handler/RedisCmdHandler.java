package com.panda.game.core.cmd.handler;

import com.google.protobuf.MessageLite;
import com.panda.game.common.concrrent.ServerThreadManager;
import com.panda.game.common.constants.CommandType;
import com.panda.game.core.annotation.Bind;
import com.panda.game.core.cmd.CommandContext;
import com.panda.game.core.cmd.CommandInvoker;
import com.panda.game.core.interceptor.CommandInterceptor;
import io.netty.channel.Channel;

import java.lang.reflect.Method;
import java.util.List;

public class RedisCmdHandler extends AbstractCmdHandler<MessageLite> {

    private final int cmd;

    public RedisCmdHandler(int cmd, Object instance) {
        super(instance);
        this.cmd = cmd;
    }

    @Override
    public void handle(Channel channel, MessageLite msg, List<CommandInterceptor> interceptorList) {
        Object[] params = new Object[1];
        params[0] = msg;
        int index = 0;

        CommandContext ctx = new CommandContext();
        ctx.setCommandType(CommandType.Redis);
        ctx.setAction(action);
        ctx.setMethod(method);
        ctx.setInstance(instance);
        ctx.setParams(params);
        ctx.setRequestId(0);
        ctx.setCmd(cmd);
        ctx.setPlayerId(0L);
        ctx.setIndex(index);
        ctx.setChannel(channel);

        // 解决RPC循环调用，例如A -> B -> A,
        // 1.将A调用B之后的逻辑，封装成一个异步任务，放在异步线程中执行，不在核心线程中阻塞；
        // 2.如果循环调用链很短，可以将B需要的数据直接放在请求中
        ServerThreadManager.getInstance().runAsync(new CommandInvoker(ctx, interceptorList.iterator()));
    }

    @Override
    protected void initInjectors(Method method) {
    }

    @Override
    protected void initBinder(Bind bind) {
    }
}
