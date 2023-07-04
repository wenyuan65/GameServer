package com.panda.game.core.cmd.inject.http;

import com.panda.game.core.cmd.inject.HttpParamInjector;
import io.netty.channel.Channel;

public class ByteParamInjector implements HttpParamInjector<Byte> {

    @Override
    public Byte inject(Channel channel, String data) {
        return data != null ? Byte.parseByte(data) : 0;
    }

}
