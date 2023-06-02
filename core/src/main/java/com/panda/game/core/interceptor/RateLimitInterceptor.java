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
        Method method = ctx.getMethod();
        Object instance = ctx.getInstance();
        Object[] params = ctx.getParams();

        try {
            method.invoke(instance, params);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        if (it.hasNext()) {
            it.next().invoke(ctx, it);
        }
    }

}