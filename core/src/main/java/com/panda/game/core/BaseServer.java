package com.panda.game.core;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;

import java.util.concurrent.Callable;

public abstract class BaseServer {

    protected static Logger log = LoggerFactory.getLogger(BaseServer.class);

    public boolean init(Callable<Boolean> callable, String name) {
        if (callable == null) {
            return false;
        }

        long start = System.currentTimeMillis();
        Boolean result = null;
        try {
            result = callable.call();
        } catch (Exception e) {
            log.error("执行异常", e);
            return false;
        }
        if (result == null || !result) {
            return false;
        }

        log.info("{}, 耗时:{} ms", name, (System.currentTimeMillis() - start));
        return true;
    }

    public abstract void start();

}
