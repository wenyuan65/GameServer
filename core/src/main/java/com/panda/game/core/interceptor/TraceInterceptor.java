package com.panda.game.core.interceptor;

import com.panda.game.core.annotation.Order;
import com.panda.game.core.cmd.CommandContext;
import com.panda.game.proto.PacketPb;

import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * TODO: 分布式追踪埋点日志拦截器
 */
@Order(97)
public class TraceInterceptor implements CommandInterceptor {

    @Override
    public void invoke(CommandContext ctx, Iterator<CommandInterceptor> it) {
        if (!it.hasNext()) {
            return;
        }

        CommandInterceptor interceptor = it.next();
        interceptor.invoke(ctx, it);
    }

}
