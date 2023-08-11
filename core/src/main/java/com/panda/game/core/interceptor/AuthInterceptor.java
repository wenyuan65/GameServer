package com.panda.game.core.interceptor;

import com.panda.game.common.constants.CommandType;
import com.panda.game.core.annotation.Order;
import com.panda.game.core.cmd.CommandContext;

import java.util.Iterator;

/**
 * 权限拦截器
 */
@Order(0)
public class AuthInterceptor extends AbstractCommandInterceptor {

    @Override
    public void doInvoke(CommandContext ctx, Iterator<CommandInterceptor> it) {
        // TODO: 拦截策略

        if (it.hasNext()) {
            it.next().invoke(ctx, it);
        }
    }

    @Override
    public boolean isValidate(CommandType commandType) {
        return commandType == CommandType.ProtoBuf;
    }

}
