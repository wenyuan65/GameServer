package com.panda.game.core.cmd;

import com.panda.game.core.interceptor.CommandInterceptor;
import io.netty.channel.Channel;

import java.util.List;

public interface CmdHandler<T> {

    void handle(Channel channel, T data, List<CommandInterceptor> interceptorList);

}
