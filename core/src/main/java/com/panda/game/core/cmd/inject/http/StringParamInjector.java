package com.panda.game.core.cmd.inject.http;

import com.panda.game.core.cmd.inject.HttpParamInjector;
import io.netty.channel.Channel;

public class StringParamInjector implements HttpParamInjector<String> {

    @Override
    public String inject(Channel channel, String data) {
        return data;
    }
    
}
