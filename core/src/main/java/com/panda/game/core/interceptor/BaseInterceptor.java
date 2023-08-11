package com.panda.game.core.interceptor;

import com.google.protobuf.MessageLite;
import com.panda.game.common.constants.CommandType;
import com.panda.game.core.annotation.Order;
import com.panda.game.core.cmd.CommandContext;
import com.panda.game.proto.PacketPb;

import java.lang.reflect.Method;
import java.util.Iterator;

@Order(99)
public class BaseInterceptor extends AbstractCommandInterceptor {

    @Override
    public void doInvoke(CommandContext ctx, Iterator<CommandInterceptor> it) {
        Method method = ctx.getMethod();
        Object instance = ctx.getInstance();
        Object[] params = ctx.getParams();
        Object result = null;

        try {
            result = method.invoke(instance, params);
            ctx.setResult(result);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        // 自动返回
        if (result != null && ctx.getCommandType() == CommandType.ProtoBuf) {
            PacketPb.Pkg.Builder builder = PacketPb.Pkg.newBuilder();
            builder.setRequestId(ctx.getRequestId());
            builder.setCmd(ctx.getCmd() + 1);
            builder.setPlayerId(ctx.getPlayerId());
            builder.setBody(((MessageLite)result).toByteString());

            ctx.getChannel().writeAndFlush(builder);
        }
    }

}
