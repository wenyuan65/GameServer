package com.panda.game.common.timer;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class ScheduleExecutor {
	
	private static final Logger log = LoggerFactory.getRtLog();
	
	/** 处理任务的线程池 */
	private ThreadPoolExecutor executor = null;
	/** 任务拦截器 */
	private static List<Object> interceptors = new ArrayList<>();
	
	public ScheduleExecutor(ThreadPoolExecutor executor) {
		this.executor = executor;
	}
	
	public Future<?> execute(Runnable task) {
		return executor.submit(task);
	}
	
}
