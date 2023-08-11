package com.panda.game.core.interceptor;

import com.panda.game.core.annotation.Order;
import com.panda.game.core.cmd.CommandContext;

import java.util.Iterator;

/**
 * TODO: 分布式追踪埋点日志拦截器
 */
@Order(97)
public class TraceInterceptor extends AbstractCommandInterceptor {

    @Override
    public void doInvoke(CommandContext ctx, Iterator<CommandInterceptor> it) {
        // TODO: 埋点日志


        if (it.hasNext()) {
            it.next().invoke(ctx, it);
            return;
        }
    }

}
