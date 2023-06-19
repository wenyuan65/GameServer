package com.panda.game.core.interceptor;

import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.core.annotation.Order;
import com.panda.game.core.cmd.CommandContext;
import com.panda.game.proto.CmdPb;
import com.panda.game.proto.PacketPb;

import java.lang.reflect.Method;
import java.util.Iterator;

@Order(98)
public class LogInterceptor implements CommandInterceptor {

    private static final Logger dayLog = LoggerFactory.getDayLog();

    @Override
    public void invoke(CommandContext ctx, Iterator<CommandInterceptor> it) {
        if (!it.hasNext()) {
            return;
        }

        Class<?> action = ctx.getAction();
        Method method = ctx.getMethod();
        Object[] params = ctx.getParams();
        long createdTime = ctx.getCreatedTime();
        PacketPb.Pkg pkg = ctx.getPkg();
        int index = ctx.getIndex();

        CommandInterceptor interceptor = it.next();

        long beginTime = System.currentTimeMillis();
        ctx.setBeginTime(beginTime);
        interceptor.invoke(ctx, it);
        long endTime = System.currentTimeMillis();
        ctx.setEndTime(endTime);

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

        dayLog.info("#i#[{}]#{}#{}#{}#{}#{}#{}#{}#", index, pkg.getPlayerId(), sb.toString(), action.getSimpleName(), method.getName(), requestParam, beginTime - createdTime, endTime - beginTime);
    }

}
