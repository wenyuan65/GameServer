package com.panda.game.core.interceptor;

import com.panda.game.core.cmd.CommandContext;

import java.util.Iterator;

public interface CommandInterceptor {

    void invoke(CommandContext ctx, Iterator<CommandInterceptor> it);

}
