package com.panda.game.core.interceptor;

import com.panda.game.common.constants.CommandType;
import com.panda.game.core.cmd.CommandContext;

import java.util.Iterator;

public abstract class AbstractCommandInterceptor implements CommandInterceptor {

    @Override
    public void invoke(CommandContext ctx, Iterator<CommandInterceptor> it) {
        if (!isValidate(ctx.getCommandType())) {
            if (it.hasNext()) {
                it.next().invoke(ctx, it);
            }
            return;
        }

        doInvoke(ctx, it);
    }

    @Override
    public boolean isValidate(CommandType commandType) {
        return true;
    }

    abstract void doInvoke(CommandContext ctx, Iterator<CommandInterceptor> it);

}
