package com.panda.game.core.cmd.handler;

import com.panda.game.common.concrrent.ServerThreadManager;
import com.panda.game.common.constants.CommandType;
import com.panda.game.core.cmd.CommandContext;
import com.panda.game.core.cmd.CommandInvoker;
import com.panda.game.core.cmd.inject.HttpParamInjector;
import com.panda.game.core.cmd.inject.http.*;
import com.panda.game.core.interceptor.CommandInterceptor;
import io.netty.channel.Channel;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public class HttpCmdHandler extends AbstractCmdHandler<Map<String, String>> {

    protected String[] injectorNames;
    protected HttpParamInjector<?>[] injectors;
    private int playerIdIndex = -1;

    /**
     * 生成参数的注入类
     * @param method
     */
    protected void initInjectors(Method method) {
        Parameter[] parameters = method.getParameters();

        this.injectors = new HttpParamInjector[parameters.length];
        this.injectorNames = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            try {
                this.injectorNames[i] = param.getName();
                this.injectors[i] = parseInjectors(param.getType(), param.getName());

                if (param.getType() == long.class && "playerId".equals(param.getName())) {
                    playerIdIndex = i;
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void handle(Channel channel, Map<String, String> paramMap, List<CommandInterceptor> interceptorList) {
        long playerId = 0L;
        Object[] params = new Object[injectors.length];
        for (int i = 0; i < injectors.length; i++) {
            HttpParamInjector<?> injector = injectors[i];
            String name = this.injectorNames[i];
            String value = paramMap.get(name);

            params[i] = injector.inject(channel, value);

            if (playerIdIndex == i) {
                playerId = (long)params[i];
            }
        }

        // 计算绑定的线程索引
        int index = binder.calcBindIndex(playerId, 0, params);

        CommandContext ctx = new CommandContext();
        ctx.setCommandType(CommandType.Http);
        ctx.setAction(action);
        ctx.setMethod(method);
        ctx.setInstance(instance);
        ctx.setParams(params);
        ctx.setRequestId(-1);
        ctx.setCmd(0);
        ctx.setPlayerId(playerId);
        ctx.setIndex(index);
        ctx.setChannel(channel);

        // 解决RPC循环调用，例如A -> B -> A,
        // 1.将A调用B之后的逻辑，封装成一个异步任务，放在异步线程中执行，不在核心线程中阻塞；
        // 2.如果循环调用链很短，可以将B需要的数据直接放在请求中
        ServerThreadManager.getInstance().runIn(new CommandInvoker(ctx, interceptorList.iterator()), index);
    }

    protected HttpParamInjector<?> parseInjectors(Class<?> paramType, String paramName) {
        if (paramType == String.class) {
            return new StringParamInjector();
        }
        if (paramType == int.class || paramType == Integer.class) {
            return new IntParamInjector();
        }
        if (paramType == long.class || paramType == Long.class) {
            return new LongParamInjector();
        }
        if (paramType == boolean.class || paramType == Boolean.class) {
            return new BooleanParamInjector();
        }
        if (paramType == byte.class || paramType == Byte.class) {
            return new ByteParamInjector();
        }
        if (paramType == double.class || paramType == Double.class) {
            return new DoubleParamInjector();
        }
        if (paramType == float.class || paramType == Float.class) {
            return new FloatParamInjector();
        }

        throw new RuntimeException(paramType.getSimpleName() + "/ " + paramName + "没有对应的injector");
    }

}
