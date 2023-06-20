package com.panda.game.core.interceptor;

import com.google.protobuf.MessageLite;
import com.panda.game.core.annotation.Order;
import com.panda.game.core.cmd.CommandContext;
import com.panda.game.proto.PacketPb;

import java.lang.reflect.Method;
import java.util.Iterator;

@Order(99)
public class BaseInterceptor implements CommandInterceptor {

    @Override
    public void invoke(CommandContext ctx, Iterator<CommandInterceptor> it) {
        Method method = ctx.getMethod();
        Object instance = ctx.getInstance();
        Object[] params = ctx.getParams();
        PacketPb.Pkg pkg = ctx.getPkg();
        Object result = null;

        try {
            result = method.invoke(instance, params);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        // 自动返回
        if (result != null) {
            PacketPb.Pkg.Builder builder = PacketPb.Pkg.newBuilder();
            builder.setRequestId(pkg.getRequestId());
            builder.setCmd(pkg.getCmd() + 1);
            builder.setPlayerId(pkg.getPlayerId());
            builder.setBody(((MessageLite)result).toByteString());

            ctx.getChannel().writeAndFlush(builder);
        }

        if (it.hasNext()) {
            it.next().invoke(ctx, it);
        }
    }

}
