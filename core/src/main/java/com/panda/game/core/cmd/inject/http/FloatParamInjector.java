package com.panda.game.core.cmd.inject.http;

import com.panda.game.core.cmd.inject.HttpParamInjector;
import io.netty.channel.Channel;

public class FloatParamInjector implements HttpParamInjector<Float> {

    @Override
    public Float inject(Channel channel, String data) {
        return data != null ? Float.parseFloat(data) : 0;
    }

}
