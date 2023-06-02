package com.panda.game.core.cmd.inject;

import com.panda.game.core.cmd.Injector;
import com.panda.game.proto.PacketPb;
import io.netty.channel.Channel;

public class ChannelInjector implements Injector<Channel> {

    @Override
    public Channel inject(Channel channel, PacketPb.Pkg pkg) {
        return channel;
    }

}
