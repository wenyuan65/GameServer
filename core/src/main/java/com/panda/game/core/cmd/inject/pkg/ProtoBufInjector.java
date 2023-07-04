package com.panda.game.core.cmd.inject.pkg;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import com.panda.game.core.cmd.Injector;
import com.panda.game.core.cmd.inject.PacketPkgInjector;
import com.panda.game.proto.PacketPb;
import io.netty.channel.Channel;

public class ProtoBufInjector<T> implements PacketPkgInjector<T> {

    private Parser<T> parser;

    public ProtoBufInjector(Parser<T> parser) {
        this.parser = parser;
    }

    @Override
    public T inject(Channel channel, PacketPb.Pkg pkg) {
        try {
            return parser.parseFrom(pkg.getBody());
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
