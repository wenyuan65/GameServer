package com.panda.game.core.cmd;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.panda.game.common.constants.RedisKey;
import com.panda.game.common.lang.Tuple;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.utils.ScanUtil;
import com.panda.game.core.annotation.Bind;
import com.panda.game.core.annotation.HttpCommand;
import com.panda.game.core.annotation.RedisCommand;
import com.panda.game.core.cmd.annotation.Action;
import com.panda.game.core.cmd.annotation.Command;
import com.panda.game.core.annotation.Order;
import com.panda.game.core.cmd.handler.HttpCmdHandler;
import com.panda.game.core.cmd.handler.ProtoBufCmdHandler;
import com.panda.game.core.cmd.handler.RedisCmdHandler;
import com.panda.game.core.cmd.inject.pkg.ProtoBufInjector;
import com.panda.game.core.interceptor.CommandInterceptor;
import com.panda.game.core.redis.RedisUtil;
import com.panda.game.proto.CmdPb;
import com.panda.game.proto.PacketPb;
import io.netty.channel.Channel;
import org.redisson.api.listener.MessageListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class CommandManager {

    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);

    private static final CommandManager instance = new CommandManager();

    private CommandManager() {
    }

    public static CommandManager getInstance() {
        return instance;
    }

    private Map<Integer, ProtoBufCmdHandler> commandMap = new HashMap<>();
    private Map<String, HttpCmdHandler> httpCmdMap = new HashMap<>();
    private Map<Integer, RedisCmdHandler> redisCmdMap = new HashMap<>();

    private Map<Class<?>, Class<? extends Injector>> injectorClazzMap = new HashMap<>();

    private List<CommandInterceptor> interceptorList = new ArrayList<>();

    public boolean init(String scanPackage) {
        // 初始化拦截器
        if (!initInterceptors()) {
            return false;
        }

        Set<Class<?>> classes = ScanUtil.scan(scanPackage);
        for (Class<?> clazz : classes) {
            Action action = clazz.getDeclaredAnnotation(Action.class);
            if (action == null) {
                continue;
            }
            Bind actionBind = clazz.getDeclaredAnnotation(Bind.class);
            Object instance = null;
            try {
                instance = clazz.newInstance();
            } catch (Throwable e) {
                logger.error("初始化对象{}异常", e, clazz.getName());
                return false;
            }

            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Command cmd = method.getDeclaredAnnotation(Command.class);
                if (cmd != null) {
                    if (!initCmd(clazz, method, cmd, actionBind, instance)){
                        return false;
                    }
                    continue;
                }

                HttpCommand httpCmd = method.getDeclaredAnnotation(HttpCommand.class);
                if (httpCmd != null) {
                    if (!initHttpCmd(clazz, method, httpCmd, actionBind, instance)) {
                        return false;
                    }
                    continue;
                }

                RedisCommand redisCmd = method.getDeclaredAnnotation(RedisCommand.class);
                if (redisCmd != null) {
                    if (!initRedisCmd(clazz, method, redisCmd, instance)) {
                        return false;
                    }
                    continue;
                }
            }
        }

        return true;
    }

    private boolean initCmd(Class<?> clazz, Method method, Command cmd, Bind actionBind, Object instance) {
        Bind cmdBind = method.getDeclaredAnnotation(Bind.class);
        if (cmdBind == null) {
            cmdBind = actionBind;
        }

        ProtoBufCmdHandler cmdHandler = new ProtoBufCmdHandler(instance);
        cmdHandler.init(clazz, method, cmdBind);

        if (commandMap.containsKey(cmd.value())) {
            ProtoBufCmdHandler oldHandler = commandMap.get(cmd.value());
            logger.error("{}命令冲突，{}.{}()与{}.{}()命令相同", CmdPb.Cmd.forNumber(cmd.value()), clazz.getName(), method.getName(), oldHandler.getAction().getName(), oldHandler.getMethod().getName());
            return false;
        }
        commandMap.put(cmd.value(), cmdHandler);

        logger.info("注册handler: {}.{}(), {}", clazz.getName(), method.getName(), CmdPb.Cmd.forNumber(cmd.value()));
        return true;
    }

    private boolean initHttpCmd(Class<?> clazz, Method method, HttpCommand httpCmd, Bind actionBind, Object instance) {
        Bind cmdBind = method.getDeclaredAnnotation(Bind.class);
        if (cmdBind == null) {
            cmdBind = actionBind;
        }

        HttpCmdHandler cmdHandler = new HttpCmdHandler(instance);
        cmdHandler.init(clazz, method, cmdBind);

        if (httpCmdMap.containsKey(httpCmd.value())) {
            HttpCmdHandler oldHandler = httpCmdMap.get(httpCmd.value());
            logger.error("{}命令冲突，{}.{}()与{}.{}()命令相同", httpCmd.value(), clazz.getName(), method.getName(), oldHandler.getAction().getName(), oldHandler.getMethod().getName());
            return false;
        }
        httpCmdMap.put(httpCmd.value(), cmdHandler);

        logger.info("注册handler: {}.{}(), {}", clazz.getName(), method.getName(), httpCmd.value());
        return true;
    }

    private boolean initRedisCmd(Class<?> clazz, Method method, RedisCommand redisCmd, Object instance) {
        int cmd = redisCmd.value();

        if (redisCmdMap.containsKey(cmd)) {
            RedisCmdHandler oldHandler = redisCmdMap.get(cmd);
            logger.error("{}命令冲突，{}.{}()与{}.{}()命令相同", CmdPb.Cmd.forNumber(cmd), clazz.getName(), method.getName(), oldHandler.getAction().getName(), oldHandler.getMethod().getName());
            return false;
        }

        Parameter parameter = method.getParameters()[0];
        Class<?> paramType = parameter.getType();

        Parser<?> parser = null;
        if (MessageLite.class.isAssignableFrom(paramType)) {
            try {
                Method parserMethod = paramType.getDeclaredMethod("parser");
                parser = (Parser<?>) parserMethod.invoke(null);
                if (parser == null) {
                    logger.error("{}没有parser()方法", paramType.getName());
                    return false;
                }
            } catch (Throwable e) {
                logger.error("解析redis订阅命令异常, {}.{}()", clazz.getName(), method.getName());
                return false;
            }
        } else {
            logger.error("解析redis订阅命令参数异常, {}.{}()", clazz.getName(), method.getName());
            return false;
        }

        RedisCmdHandler cmdHandler = new RedisCmdHandler(cmd, instance);
        cmdHandler.init(clazz, method, null);

        // 订阅
        RedisUtil.subscribeTopic(cmd, parser, new MessageListener(){
            @Override
            public void onMessage(CharSequence channel, Object msg) {
                handleRedis(cmd, (MessageLite)msg);
            }
        });

        return false;
    }

    private boolean initInterceptors() {
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

    public void registerInjector(Class<?> clazz, Class<? extends Injector<?, ?>> injectorClazz) {
        injectorClazzMap.put(clazz, injectorClazz);
    }

    public Class<? extends Injector> getExtraInjectorClass(Class<?> paramType) {
        return injectorClazzMap.get(paramType);
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
        ProtoBufCmdHandler handler = commandMap.get(cmd);
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

    public void handleHttp(Channel channel, String cmd, Map<String, String> paramMap) {
        HttpCmdHandler handler = httpCmdMap.get(cmd);
        if (handler == null) {
            logger.error("接口{}没有找到对应的处理方法", cmd);
            return ;
        }

        try {
            handler.handle(channel, paramMap, interceptorList);
        } catch (Exception e) {
            logger.error("执行命令异常异常:{}, 参数：{}", e, cmd, paramMap);
        }
    }

    public void handleRedis(int cmd, MessageLite msg) {
        RedisCmdHandler handler = redisCmdMap.get(cmd);
        if (handler == null) {
            logger.error("接口{}没有找到对应的处理方法", cmd);
            return ;
        }

        try {
            handler.handle(null, msg, null);
        } catch (Exception e) {
            logger.error("执行命令异常异常:{}, 参数：{}", e, CmdPb.Cmd.forNumber(cmd));
        }
    }

}
