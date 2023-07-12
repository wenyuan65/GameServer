package com.panda.game.core.cmd.inject.http;

import com.panda.game.core.cmd.inject.HttpParamInjector;
import io.netty.channel.Channel;

public class ChannelParamInjector implements HttpParamInjector<Channel>  {
    @Override
    public Channel inject(Channel channel, String data) {
        return channel;
    }
}
