package com.panda.game.core.common;

import com.panda.game.common.json.JsonDocument;
import com.panda.game.proto.CmdPb;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class BaseHttpAction {

    public void sendOk(Channel channel) {
        sendOk(channel, null);
    }

    public void sendOk(Channel channel, String result) {
        JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("state", CmdPb.ErrorCode.Ok_VALUE);

        if (result != null) {
            doc.append("msg", result);
        }

        doc.endObject();

        sendMessage(channel, doc.toByte());
    }

    public void sendError(Channel channel, int errorCode) {
        JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("state", errorCode);
        doc.endObject();

        sendMessage(channel, doc.toByte());
    }

    public void sendMessage(Channel channel, byte[] content) {
        sendMessage(channel, content, null);
    }

    public void sendMessage(Channel channel, byte[] content, Map<String, String> extraHeaders) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(content));
        HttpHeaders headers = response.headers();
        headers.set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
        headers.set(HttpHeaderNames.CONTENT_LENGTH, content.length);

        if (extraHeaders != null) {
            extraHeaders.forEach((key, value) -> {
                headers.set(key, value);
            });
        }

        channel.writeAndFlush(response);
    }

    public void sendDownloadFile(Channel channel, String fileName, byte[] content) {
        sendDownloadFile(channel, fileName, content, null);
    }

    public void sendDownloadFile(Channel channel, String fileName, byte[] content, Map<String, String> extraHeaders) {
        String encodeFileName = Base64.getEncoder().encodeToString(fileName.getBytes(StandardCharsets.UTF_8));

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(content));
        HttpHeaders headers = response.headers();
        headers.set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        headers.set(HttpHeaderNames.CONTENT_LENGTH, content.length);
        headers.set(HttpHeaderNames.CONTENT_DISPOSITION, "attachment;filename=" + encodeFileName);

        if (extraHeaders != null) {
            extraHeaders.forEach((key, value) -> {
                headers.set(key, value);
            });
        }

        channel.writeAndFlush(response);
    }

}
