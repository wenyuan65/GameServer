package com.panda.game.core.interceptor;

import com.panda.game.core.annotation.Order;
import com.panda.game.core.cmd.CommandContext;

import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * 限流拦截器
 */
@Order(1)
public class RateLimitInterceptor implements CommandInterceptor {

    @Override
    public void invoke(CommandContext ctx, Iterator<CommandInterceptor> it) {
        if (!it.hasNext()) {
            return;
        }

        CommandInterceptor interceptor = it.next();
        interceptor.invoke(ctx, it);
    }

}
