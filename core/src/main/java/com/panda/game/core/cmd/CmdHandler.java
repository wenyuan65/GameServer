package com.panda.game.core.cmd;

import com.panda.game.common.concrrent.ServerThreadManager;
import com.panda.game.core.cmd.annotation.Command;
import com.panda.game.core.cmd.bind.*;
import com.panda.game.core.interceptor.CommandInterceptor;
import com.panda.game.proto.PacketPb;
import io.netty.channel.Channel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class CmdHandler {

    private Class<?> action;
    private Method method;
    private Object instance;

    private Injector<?>[] injectors;
    private Binder binder;

    public CmdHandler() {
    }

    public void init(Class<?> clazz, Method method, Command command) {
        this.action = clazz;
        this.method = method;

        initBinder(clazz, method, command);
        initInjectors(method);
        initInstance();
    }

    /**
     * 生成实例对象
     */
    private void initInstance() {
        try {
            Constructor<?> constructor = action.getConstructor();
            this.instance = constructor.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成参数的注入类
     * @param method
     */
    private void initInjectors(Method method) {
        List<Injector<?>> paramInjectors = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (Parameter param : parameters) {
            try {
                Injector<?> injector = CommandManager.getInstance().parse(param.getType(), param.getName());
                paramInjectors.add(injector);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        this.injectors = paramInjectors.toArray(new Injector<?>[0]);
    }

    /**
     * 绑定实例
     * @param clazz
     * @param method
     * @param command
     */
    private void initBinder(Class<?> clazz, Method method, Command command) {
        switch(command.bindType()) {
            case Bind_PlayerId:
                binder = new PlayerIdBinder();
                break;
            case Bind_Fields:
                binder = parseFieldsBinder(clazz, method, command);
                break;
            case Bind_Cmd:
                binder = new CmdBinder();
                break;
            case Bind_Group:
                binder = new GroupBinder(command.group());
                break;
            case Random:
                binder = new RandomBinder();
                break;
            default: throw new RuntimeException("not implemented bind type");
        }
    }

    private Binder parseFieldsBinder(Class<?> clazz, Method method, Command command) {
        int index = command.index();
        String[] bindFieldNames = command.bind();
        Parameter[] parameters = method.getParameters();
        if (index < 0 || index >= parameters.length) {
            throw new RuntimeException(clazz.getName() + "." + method.getName() + "()方法参数数量与index值不匹配,实际数量" + parameters.length + ", index:"+ index);
        }
        Parameter parameter = parameters[index];
        Class<?> parameterType = parameter.getType();

        Method[] bindFieldsMethod = new Method[bindFieldNames.length];
        for (int i = 0; i < bindFieldNames.length; i++) {
            String fieldName = bindFieldNames[i];
            try {
                String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method getMethod = parameterType.getDeclaredMethod(getMethodName);

                getMethod.setAccessible(true);
                bindFieldsMethod[i] = getMethod;
            } catch (Throwable e) {
                throw new RuntimeException(parameterType.getName() + "类中找不到字段" + fieldName);
            }
        }

        return new FieldsBinder(index, bindFieldsMethod);
    }

    public void handle(Channel channel, PacketPb.Pkg pkg, List<CommandInterceptor> interceptorList) {
        Object[] params = new Object[injectors.length];
        for (int i = 0; i < injectors.length; i++) {
            params[i] = injectors[i].inject(channel, pkg);
        }

        // 计算绑定的线程索引
        int index = binder.calcBindIndex(pkg.getPlayerId(), pkg.getCmd(), params);

        CommandContext ctx = new CommandContext();
        ctx.setAction(action);
        ctx.setMethod(method);
        ctx.setInstance(instance);
        ctx.setParams(params);
        ctx.setPkg(pkg);
        ctx.setIndex(index);

        // 解决RPC循环调用，例如A -> B -> A,
        // 1.将A调用B之后的逻辑，封装成一个异步任务，放在异步线程中执行，不在核心线程中阻塞；
        // 2.如果循环调用链很短，可以将B需要的数据直接放在请求中
        ServerThreadManager.getInstance().runIn(new CommandInvoker(ctx, interceptorList.iterator()), index);
    }


}
