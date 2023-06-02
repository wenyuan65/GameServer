package com.panda.game.core.interceptor;

import com.panda.game.core.annotation.Order;
import com.panda.game.core.cmd.CommandContext;

import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * 权限拦截器
 */
@Order(0)
public class AuthInterceptor implements CommandInterceptor {

    @Override
    public void invoke(CommandContext ctx, Iterator<CommandInterceptor> it) {
        // TODO: 拦截策略

        if (it.hasNext()) {
            it.next().invoke(ctx, it);
        }
    }

}
