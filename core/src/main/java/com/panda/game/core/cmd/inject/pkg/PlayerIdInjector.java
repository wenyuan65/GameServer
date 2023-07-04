package com.panda.game.core.cmd.inject.pkg;

import com.panda.game.core.cmd.Injector;
import com.panda.game.core.cmd.inject.PacketPkgInjector;
import com.panda.game.proto.PacketPb;
import io.netty.channel.Channel;

public class PlayerIdInjector implements PacketPkgInjector<Long> {

    @Override
    public Long inject(Channel channel, PacketPb.Pkg pkg) {
        return pkg.getPlayerId();
    }

}
