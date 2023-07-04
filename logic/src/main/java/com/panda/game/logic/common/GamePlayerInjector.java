package com.panda.game.logic.common;

import com.panda.game.core.cmd.inject.PacketPkgInjector;
import com.panda.game.logic.world.WorldManager;
import com.panda.game.proto.PacketPb;
import io.netty.channel.Channel;

public class GamePlayerInjector implements PacketPkgInjector<GamePlayer> {

    @Override
    public GamePlayer inject(Channel channel, PacketPb.Pkg pkg) {
        return WorldManager.getInstance().getPlayer(pkg.getPlayerId());
    }

}
