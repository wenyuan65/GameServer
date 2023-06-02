package com.panda.game.common.timer.impl;

import com.panda.game.common.timer.Timeout;
import com.panda.game.common.timer.TimerTask;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class SimpleTimeTask implements TimerTask {
	
	private Runnable task;
	private ThreadPoolExecutor executor;
	private Future<?> future;

	public SimpleTimeTask(Runnable task, ThreadPoolExecutor executor) {
		this.task = task;
		this.executor = executor;
	}
	
	@Override
	public void run(Timeout timeout) throws Exception {
		this.future = executor.submit(task);
	}
	
	public Future<?> getFuture() {
		return future;
	}

}
