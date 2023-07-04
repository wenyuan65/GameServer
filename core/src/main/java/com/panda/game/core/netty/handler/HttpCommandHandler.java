package com.panda.game.core.netty.handler;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.utils.StringUtils;
import com.panda.game.core.cmd.CommandManager;
import com.panda.game.proto.PacketPb;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpCommandHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(HttpCommandHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest)msg;
            String uri = request.uri();
            ByteBuf buf = request.content();
//            HttpHeaders headers = request.headers();

            Map<String, String> paramMap = new HashMap<>();
            int index = uri.indexOf("?");
            if (index >= 0) {
                String param = uri.substring(index + 1);
                param = URLDecoder.decode(param, "utf-8");

                Map<String, String> map = parseParam(param);
                paramMap.putAll(map);
                uri = uri.substring(0, index);
            }

            String command = uri;
            if (buf.readableBytes() > 0 && request.method() == HttpMethod.POST) {
                String param = buf.toString(StandardCharsets.UTF_8);
                param = URLDecoder.decode(param, "utf-8");

                Map<String, String> map = parseParam(param);
                paramMap.putAll(map);
            }

            CommandManager.getInstance().handleHttp(ctx.channel(), command, paramMap);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("un-catch exception in netty", cause);
    }

    private Map<String, String> parseParam(String data) {
        Map<String, String> paramMap = new HashMap<>();
        String[] pairs = StringUtils.split(data, "&");
        for (String pair : pairs) {
            int index = pair.indexOf("=");
            String name = pair.substring(0, index);
            String value = pair.substring(index + 1);
            paramMap.put(name, value);
        }

        return paramMap;
    }

}
