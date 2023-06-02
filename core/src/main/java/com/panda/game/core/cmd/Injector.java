package com.panda.game.core.cmd;

import com.panda.game.proto.PacketPb;
import io.netty.channel.Channel;

public interface Injector<T> {

    T inject(Channel channel, PacketPb.Pkg pkg);

}
