package com.panda.game.core.netty;

import com.panda.game.core.rpc.connection.Connection;
import io.netty.util.AttributeKey;

public class NettyConstants {

    public static final AttributeKey<String> Session_Key = AttributeKey.valueOf("session");

    public static final AttributeKey<Connection> CONNECTION = AttributeKey.valueOf("connection");

}
