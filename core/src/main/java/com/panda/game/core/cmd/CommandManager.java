package com.panda.game.core.cmd;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.panda.game.common.lang.Tuple;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.utils.ScanUtil;
import com.panda.game.core.cmd.annotation.Action;
import com.panda.game.core.cmd.annotation.Command;
import com.panda.game.core.annotation.Order;
import com.panda.game.core.cmd.inject.ChannelInjector;
import com.panda.game.core.cmd.inject.PlayerIdInjector;
import com.panda.game.core.cmd.inject.ProtoBufInjector;
import com.panda.game.core.interceptor.CommandInterceptor;
import com.panda.game.proto.CmdPb;
import com.panda.game.proto.PacketPb;
import io.netty.channel.Channel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandManager {

    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);

    private static final CommandManager instance = new CommandManager();

    private CommandManager() {
    }

    public static CommandManager getInstance() {
        return instance;
    }

    private Map<Integer, CmdHandler> commandMap = new HashMap<>();

    private Map<Class<?>, Class<? extends Injector>> injectorClazzMap = new HashMap<>();

    private List<CommandInterceptor> interceptorList = new ArrayList<>();

    public boolean init(String scanPackage) {
        Set<Class<?>> classes = ScanUtil.scan(scanPackage);
        for (Class<?> clazz : classes) {
            Action action = clazz.getDeclaredAnnotation(Action.class);
            if (action == null) {
                continue;
            }

            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Command cmd = method.getDeclaredAnnotation(Command.class);
                if (cmd == null) {
                    continue;
                }

                CmdHandler cmdHandler = new CmdHandler();
                cmdHandler.init(clazz, method, cmd);
                commandMap.put(cmd.value(), cmdHandler);

                logger.info("注册handler: {}.{}(), {}", clazz.getName(), method.getName(), CmdPb.Cmd.forNumber(cmd.value()).name());
            }
        }

        List<CommandInterceptor> tmpList = new ArrayList<>(interceptorList);
        List<Tuple<CommandInterceptor, Integer>> list = new ArrayList<>(tmpList.size() + 1);
        for (CommandInterceptor interceptor : tmpList) {
            Class<? extends CommandInterceptor> clazz = interceptor.getClass();
            Order order = clazz.getDeclaredAnnotation(Order.class);
            if (order == null) {
                logger.error("拦截器{}必须声明@Order注解", clazz.getName());
                return false;
            }

            list.add(new Tuple<>(interceptor, order.value()));
        }
        Collections.sort(list, Comparator.comparing(t -> t.getRight()));

        interceptorList.clear();
        for (Tuple<CommandInterceptor, Integer> t : list) {
            CommandInterceptor interceptor = t.left;
            Integer order = t.right;

            interceptorList.add(interceptor);
            logger.info("拦截器：{}， order:{}", interceptor.getClass().getName(), order);
        }

        return true;
    }

    public void registerInjector(Class<?> clazz, Class<? extends Injector<?>> injectorClazz) {
        injectorClazzMap.put(clazz, injectorClazz);
    }

    public void addInterceptor(List<CommandInterceptor> list) {
        for (CommandInterceptor interceptor : list) {
            addInterceptor(interceptor);
        }
    }

    public void addInterceptor(CommandInterceptor interceptor) {
        if (interceptor == null) {
            return;
        }

        interceptorList.add(interceptor);
    }

    public void handle(Channel channel, PacketPb.Pkg pkg) {
        int cmd = pkg.getCmd();
        CmdHandler handler = commandMap.get(cmd);
        if (handler == null) {
            logger.error("接口{}没有找到对应的处理方法", cmd);
            return ;
        }

        try {
            handler.handle(channel, pkg, interceptorList);
        } catch (Exception e) {
            logger.error("执行命令异常异常:{}", CmdPb.Cmd.forNumber(cmd).name(), e);
        }
    }

    public <T> Injector<?> parse(Class<T> paramType, String paramName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        if (MessageLite.class.isAssignableFrom(paramType)) {
            Method method = paramType.getDeclaredMethod("parser");
            Parser<T> parser = (Parser<T>) method.invoke(null);
            if (parser == null) {
                throw new RuntimeException(paramType.getName() + "没有parser()方法");
            }

            return new ProtoBufInjector<>(parser);
        }
        if (injectorClazzMap.containsKey(paramType)) {
            Class<? extends Injector> clazz = injectorClazzMap.get(paramType);
            Constructor<? extends Injector> constructor = clazz.getConstructor();
            return constructor.newInstance();
        }
        if (Channel.class == paramType) {
            return new ChannelInjector();
        }
        if ("playerId".equals(paramName)) {
            return new PlayerIdInjector();
        }

        throw new RuntimeException(paramType.getSimpleName() + "/ " + paramName + "没有对应的injector");
    }

}
