package com.panda.game.core.cmd.inject;

import com.panda.game.core.cmd.Injector;
import com.panda.game.proto.PacketPb;
import io.netty.channel.Channel;

public class PlayerIdInjector implements Injector<Long> {

    @Override
    public Long inject(Channel channel, PacketPb.Pkg pkg) {
        return pkg.getPlayerId();
    }

}
