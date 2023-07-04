package com.panda.game.core.cmd.inject.http;

import com.panda.game.core.cmd.inject.HttpParamInjector;
import io.netty.channel.Channel;

public class IntParamInjector implements HttpParamInjector<Integer> {

    @Override
    public Integer inject(Channel channel, String data) {
        return data != null ? Integer.parseInt(data) : 0;
    }
}
