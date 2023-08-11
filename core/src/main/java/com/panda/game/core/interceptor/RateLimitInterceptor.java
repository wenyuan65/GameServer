package com.panda.game.core.interceptor;

import com.panda.game.common.constants.CommandType;
import com.panda.game.core.annotation.Order;
import com.panda.game.core.cmd.CommandContext;

import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * 限流拦截器
 */
@Order(1)
public class RateLimitInterceptor extends AbstractCommandInterceptor {

    @Override
    public void doInvoke(CommandContext ctx, Iterator<CommandInterceptor> it) {
        // TODO: 限流策略


        if (it.hasNext()) {
            it.next().invoke(ctx, it);
            return;
        }
    }

    @Override
    public boolean isValidate(CommandType commandType) {
        return commandType == CommandType.ProtoBuf;
    }

}
