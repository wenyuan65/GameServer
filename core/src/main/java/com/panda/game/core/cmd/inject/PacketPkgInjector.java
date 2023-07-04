package com.panda.game.core.cmd.inject;

import com.panda.game.core.cmd.Injector;
import com.panda.game.proto.PacketPb;

public interface PacketPkgInjector<O> extends Injector<PacketPb.Pkg, O> {
}
