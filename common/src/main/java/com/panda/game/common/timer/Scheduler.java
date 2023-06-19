package com.panda.game.common.timer;

import com.panda.game.common.concrrent.ServerThreadManager;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.timer.future.Future;
import com.panda.game.common.timer.future.FutureImpl;
import com.panda.game.common.timer.impl.CronTimerTask;
import com.panda.game.common.timer.impl.ScheduledTimerTask;
import com.panda.game.common.timer.impl.SimpleTimeTask;
import com.panda.game.common.timer.quartz.Job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 调度器
 * @author wenyuan
 */
public final class Scheduler {
	
	private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
	
	private static final Logger rtLog = LoggerFactory.getRtLog();
	
	/** 定时器 */
	private static final Timer TIMER = new HashedWheelTimer(Executors.defaultThreadFactory(), 1, TimeUnit.MILLISECONDS, 2048);

	public static ThreadPoolExecutor getThreadPool() {
		return ServerThreadManager.getInstance().getSchedulerThreadPool();
	}

	/**
	 * 批量添加crontab定时任务
	 * @param jobList
	 * @throws Exception 
	 */
	public static void addJobList(List<Job> jobList) {
		for (Job job : jobList) {
			if (job != null) {
				addJob(job);
			}
		}
	}
	
	/**
	 * 添加crontab定时任务
	 * @param job
	 * @throws Exception 
	 */
	public static void addJob(Job job) {
		if (job.getTarget() == null || job.getMethod() == null) {
			throw new IllegalArgumentException("定时任务执行的对象和方法为null, " + job.getJobName());
		}

		CronTimerTask timerTask = new CronTimerTask(job, getThreadPool());
		long delay = timerTask.getNextFireTime().getTime() - System.currentTimeMillis();
		
		TIMER.newTimeout(timerTask, delay, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 延迟{@code delay}(单位：{@code unit})后，执行任务{@code task}, 可取消
	 * @param task
	 * @param delay
	 * @param unit
	 * @return
	 */
	public static Future schedule(Runnable task, long delay, TimeUnit unit) {
		SimpleTimeTask timerTask = new SimpleTimeTask(task, getThreadPool());
		Timeout timeout = TIMER.newTimeout(timerTask, delay, unit);
		
		return new FutureImpl(timeout);
	}
	
	/**
	 * 延迟{@code delay}(单位：ms)后，执行任务{@code task}
	 * @param task
	 * @param delayMs
	 * @return
	 */
	public static Future schedule(Runnable task, long delayMs) {
		return schedule(task, delayMs, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 延迟一定时间后，定时执行任务{@code task}
	 * @param task 任务
	 * @param delay 第一次执行的延迟时间
	 * @param ratio 第一次执行后，每次执行的时间间隔
	 * @param unit 时间单位
	 * @return
	 */
	public static Future scheduleWithFixRatio(Runnable task, long delay, long ratio, TimeUnit unit) {
		ScheduledTimerTask timerTask = new ScheduledTimerTask(task, getThreadPool(), ratio, unit);
		Timeout timeout = TIMER.newTimeout(timerTask, delay, unit);
		
		return new FutureImpl(timeout);
	}
	
	/**
	 * 延迟一定时间后，定时执行任务{@code task}
	 * @param task 任务
	 * @param delayMS 第一次执行的延迟时间，单位：ms
	 * @param ratioMS 第一次执行后，每次执行的时间间隔，单位：ms
	 * @return 定时任务的执行结果
	 */
	public static Future scheduleWithFixRatio(Runnable task, long delayMS, long ratioMS) {
		return scheduleWithFixRatio(task, delayMS, ratioMS, TimeUnit.MILLISECONDS);
	}
	
	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Runnable task = () -> {
			Date date = new Date();
			System.out.println("hello:" + sdf.format(date)); 
		};
		
		task.run();
//		Scheduler.schedule(task, 5000);
		final Future f = Scheduler.scheduleWithFixRatio(task, 2000, 5000);
		
		new Thread(() -> {
			try {
				Thread.sleep(11000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			f.cancel();
			
			System.out.println("cancelled");
		}).start();
	}
	
}
