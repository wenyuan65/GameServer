package com.panda.game.common.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageV3;
import com.panda.game.proto.PacketPb;

public class PbUtil {

    public static PacketPb.Pkg.Builder createPkg(int cmd) {
        PacketPb.Pkg.Builder builder = PacketPb.Pkg.newBuilder();
        builder.setCmd(cmd);
        return builder;
    }

    public static PacketPb.Pkg.Builder createPkg(int cmd, GeneratedMessageV3 message) {
        PacketPb.Pkg.Builder builder = PacketPb.Pkg.newBuilder();
        builder.setCmd(cmd);
        builder.setBody(message.toByteString());

        return builder;
    }

    public static PacketPb.Pkg.Builder createPkg(int cmd, long playerId, GeneratedMessageV3 message) {
        PacketPb.Pkg.Builder builder = PacketPb.Pkg.newBuilder();
        builder.setCmd(cmd);
        builder.setPlayerId(playerId);
        builder.setBody(message.toByteString());

        return builder;
    }

    public static PacketPb.Pkg.Builder createPkg(PacketPb.Pkg rq, int cmd, byte[] result) {
        PacketPb.Pkg.Builder builder = PacketPb.Pkg.newBuilder();
        builder.setRequestId(rq.getRequestId());
        builder.setCmd(cmd);
        if (result != null) {
            builder.setBody(ByteString.copyFrom(result));
        }

        return builder;
    }

    public static PacketPb.Pkg createErrorPkg(int cmd, int errorCode) {
        PacketPb.Pkg.Builder builder = PacketPb.Pkg.newBuilder();
        builder.setCmd(cmd);
        builder.setErrorCode(errorCode);

        return builder.build();
    }

}
