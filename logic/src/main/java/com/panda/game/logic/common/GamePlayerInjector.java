package com.panda.game.logic.common;

import com.panda.game.core.cmd.Injector;
import com.panda.game.logic.player.GamePlayer;
import com.panda.game.proto.PacketPb;
import io.netty.channel.Channel;

public class GamePlayerInjector implements Injector<GamePlayer> {

    @Override
    public GamePlayer inject(Channel channel, PacketPb.Pkg pkg) {
        return WorldManager.getInstance().getPlayer(pkg.getPlayerId());
    }

}
