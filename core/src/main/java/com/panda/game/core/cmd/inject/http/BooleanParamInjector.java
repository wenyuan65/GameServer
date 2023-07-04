package com.panda.game.core.cmd.inject.http;

import com.panda.game.core.cmd.inject.HttpParamInjector;
import io.netty.channel.Channel;

public class BooleanParamInjector implements HttpParamInjector<Boolean> {

    @Override
    public Boolean inject(Channel channel, String data) {
        return data != null ? Boolean.parseBoolean(data) : Boolean.FALSE;
    }
    
}
