package com.panda.game.core.cmd.handler;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.panda.game.common.concrrent.ServerThreadManager;
import com.panda.game.common.constants.CommandType;
import com.panda.game.core.cmd.CommandContext;
import com.panda.game.core.cmd.CommandInvoker;
import com.panda.game.core.cmd.CommandManager;
import com.panda.game.core.cmd.Injector;
import com.panda.game.core.cmd.inject.pkg.ChannelInjector;
import com.panda.game.core.cmd.inject.PacketPkgInjector;
import com.panda.game.core.cmd.inject.pkg.PlayerIdInjector;
import com.panda.game.core.cmd.inject.pkg.ProtoBufInjector;
import com.panda.game.core.interceptor.CommandInterceptor;
import com.panda.game.proto.PacketPb;
import io.netty.channel.Channel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

public class ProtoBufCmdHandler extends AbstractCmdHandler<PacketPb.Pkg> {

    protected PacketPkgInjector<?>[] injectors;

    public ProtoBufCmdHandler(Object instance) {
        super(instance);
    }

    @Override
    protected void initInjectors(Method method) {
        Parameter[] parameters = method.getParameters();

        this.injectors = new PacketPkgInjector[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            try {
                this.injectors[i] = parseInjectors(param.getType(), param.getName());
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void handle(Channel channel, PacketPb.Pkg pkg, List<CommandInterceptor> interceptorList) {
        Object[] params = new Object[injectors.length];
        for (int i = 0; i < injectors.length; i++) {
            params[i] = injectors[i].inject(channel, pkg);
        }

        // 计算绑定的线程索引
        int index = binder.calcBindIndex(pkg.getPlayerId(), pkg.getCmd(), params);

        CommandContext ctx = new CommandContext();
        ctx.setCommandType(CommandType.ProtoBuf);
        ctx.setAction(action);
        ctx.setMethod(method);
        ctx.setInstance(instance);
        ctx.setParams(params);
        ctx.setRequestId(pkg.getRequestId());
        ctx.setCmd(pkg.getCmd());
        ctx.setPlayerId(pkg.getPlayerId());
        ctx.setIndex(index);
        ctx.setChannel(channel);

        // 解决RPC循环调用，例如A -> B -> A,
        // 1.将A调用B之后的逻辑，封装成一个异步任务，放在异步线程中执行，不在核心线程中阻塞；
        // 2.如果循环调用链很短，可以将B需要的数据直接放在请求中
        ServerThreadManager.getInstance().runIn(new CommandInvoker(ctx, interceptorList.iterator()), index);
    }

    protected PacketPkgInjector<?> parseInjectors(Class<?> paramType, String paramName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        // protoBuf协议包
        if (MessageLite.class.isAssignableFrom(paramType)) {
            Method method = paramType.getDeclaredMethod("parser");
            Parser<?> parser = (Parser<?>) method.invoke(null);
            if (parser == null) {
                throw new RuntimeException(paramType.getName() + "没有parser()方法");
            }

            return new ProtoBufInjector<>(parser);
        }
        // 项目额外注入类
        Class<? extends Injector> extraInjectorClass = CommandManager.getInstance().getExtraInjectorClass(paramType);
        if (extraInjectorClass != null) {
            Constructor<? extends Injector> constructor = extraInjectorClass.getConstructor();
            return (PacketPkgInjector<?>)constructor.newInstance();
        }
        // channel
        if (Channel.class == paramType) {
            return new ChannelInjector();
        }
        // playerId
        if ("playerId".equals(paramName)) {
            return new PlayerIdInjector();
        }

        throw new RuntimeException(paramType.getSimpleName() + "/ " + paramName + "没有对应的injector");
    }

}
