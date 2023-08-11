package com.panda.game.core.interceptor;

import com.panda.game.common.constants.CommandType;
import com.panda.game.core.cmd.CommandContext;

import java.util.Iterator;

public interface CommandInterceptor {

    boolean isValidate(CommandType commandType);

    void invoke(CommandContext ctx, Iterator<CommandInterceptor> it);

}
