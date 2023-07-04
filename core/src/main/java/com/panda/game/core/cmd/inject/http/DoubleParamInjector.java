package com.panda.game.core.cmd.inject.http;

import com.panda.game.core.cmd.inject.HttpParamInjector;
import io.netty.channel.Channel;

public class DoubleParamInjector implements HttpParamInjector<Double> {

    @Override
    public Double inject(Channel channel, String data) {
        return data != null ? Double.parseDouble(data) : 0;
    }

}
