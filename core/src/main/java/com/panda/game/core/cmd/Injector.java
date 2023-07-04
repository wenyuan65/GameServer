package com.panda.game.core.cmd;

import io.netty.channel.Channel;

public interface Injector<I, O> {

    O inject(Channel channel, I data);

}
