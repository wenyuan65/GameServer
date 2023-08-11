package com.panda.game.core.interceptor;

import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import com.panda.game.common.constants.CommandType;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.core.annotation.Order;
import com.panda.game.core.cmd.CommandContext;
import com.panda.game.proto.CmdPb;
import com.panda.game.proto.PacketPb;

import java.lang.reflect.Method;
import java.util.Iterator;

@Order(98)
public class LogInterceptor extends AbstractCommandInterceptor {

    private static final Logger dayLog = LoggerFactory.getDayLog();

    @Override
    public void doInvoke(CommandContext ctx, Iterator<CommandInterceptor> it) {
        if (!it.hasNext()) {
            return;
        }
        CommandInterceptor interceptor = it.next();

        boolean isError = false;
        Class<?> action = ctx.getAction();
        Method method = ctx.getMethod();
        Object[] params = ctx.getParams();
        long createdTime = ctx.getCreatedTime();
        long beginTime = System.currentTimeMillis();
        ctx.setBeginTime(beginTime);
        try {
            interceptor.invoke(ctx, it);
        } catch (Exception e) {
            isError = true;
            dayLog.error("{}.{}()执行异常", e, action.getSimpleName(), method.getName());
        }
        long endTime = System.currentTimeMillis();
        ctx.setEndTime(endTime);


        CommandType commandType = ctx.getCommandType();
        if (commandType == CommandType.ProtoBuf) {
            // 打印参数
            StringBuilder sb = new StringBuilder();
            String requestParam = "";
            for (Object param : params) {
                if (param instanceof Message) {
                    requestParam = JsonFormat.printToString((Message)param);
                } else {
                    if (sb.length() > 0) {
                        sb.append('#');
                    }
                    sb.append(param.toString());
                }
            }

            dayLog.info("#i#{}#{}#{}#{}#{}#{}#{}#{}#{}#{}#TCP#", ctx.getIndex(), ctx.getRequestId(), ctx.getPlayerId(), sb.toString(),
                    action.getSimpleName(), method.getName(), requestParam, beginTime - createdTime, endTime - beginTime, isError ? "Error" : "");
        } else if (commandType == CommandType.Http) {
            String requestParam = "";
            StringBuilder sb = new StringBuilder();
            for (Object param : params) {
                if (param instanceof Message) {
                    requestParam = JsonFormat.printToString((Message)param);
                } else {
                    if (sb.length() > 0) {
                        sb.append('#');
                    }
                    sb.append(param.toString());
                }
            }

            dayLog.info("#i#{}#{}#{}#{}#{}#{}#{}#{}#{}#{}#HTTP#", ctx.getIndex(), ctx.getRequestId(), ctx.getPlayerId(), sb.toString(),
                    action.getSimpleName(), method.getName(), requestParam, beginTime - createdTime, endTime - beginTime, isError ? "Error" : "");
        } else if (commandType == CommandType.Redis) {
            String requestParam = "";
            StringBuilder sb = new StringBuilder();
            for (Object param : params) {
                if (param instanceof Message) {
                    requestParam = JsonFormat.printToString((Message)param);
                } else {
                    if (sb.length() > 0) {
                        sb.append('#');
                    }
                    sb.append(param.toString());
                }
            }

            dayLog.info("#i#{}#{}#{}#{}#{}#{}#{}#{}#{}#{}#Redis#", ctx.getIndex(), ctx.getRequestId(), ctx.getPlayerId(), sb.toString(),
                    action.getSimpleName(), method.getName(), requestParam, beginTime - createdTime, endTime - beginTime, isError ? "Error" : "");
        }
    }

}
