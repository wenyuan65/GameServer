package com.panda.game.core.cmd;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.core.interceptor.CommandInterceptor;

import java.util.Iterator;

public class CommandInvoker implements Runnable {

    private static final Logger dayLog = LoggerFactory.getDayLog();

    private CommandContext ctx;
    private Iterator<CommandInterceptor> it;

    public CommandInvoker(CommandContext ctx, Iterator<CommandInterceptor> it) {
        this.ctx = ctx;
        this.it = it;
    }

    @Override
    public void run() {
        if (it.hasNext()) {
            CommandInterceptor interceptor = it.next();
            interceptor.invoke(ctx, it);
        }
    }

}
