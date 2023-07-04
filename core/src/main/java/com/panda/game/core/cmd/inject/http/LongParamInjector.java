package com.panda.game.core.cmd.inject.http;

import com.panda.game.core.cmd.inject.HttpParamInjector;
import io.netty.channel.Channel;

public class LongParamInjector implements HttpParamInjector<Long> {

    @Override
    public Long inject(Channel channel, String data) {
        return data != null ? Long.parseLong(data) : 0;
    }

}
