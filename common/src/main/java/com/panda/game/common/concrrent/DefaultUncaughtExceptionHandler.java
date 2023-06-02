package com.panda.game.common.concrrent;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;

public class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger("UncaughtExceptionHandler");
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		log.error("UncaughtException", e);
	}
	
}
