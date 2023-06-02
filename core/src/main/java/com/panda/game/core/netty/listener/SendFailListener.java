package com.panda.game.core.netty.listener;

import com.panda.game.proto.CmdPb;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendFailListener implements ChannelFutureListener {

    private static final Logger logger = LoggerFactory.getLogger(SendFailListener.class);

    private long playerId;
    private int cmd;

    public SendFailListener(long playerId, int cmd) {
        this.playerId = playerId;
        this.cmd = cmd;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
            logger.info("send message error fail, {}, {}", playerId, CmdPb.Cmd.forNumber(cmd).name());
        }
    }
}
