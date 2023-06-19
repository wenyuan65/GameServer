package com.panda.game.common.timer.impl;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.timer.Timeout;
import com.panda.game.common.timer.TimerTask;
import com.panda.game.common.timer.quartz.CronTrigger;
import com.panda.game.common.timer.quartz.Job;
import com.panda.game.common.utils.DateUtil;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CronTimerTask implements TimerTask {
	
	private static final Logger log = LoggerFactory.getRtLog();
	
	private Job job;
	private ThreadPoolExecutor executor;
	private CronTrigger cronTrigger;
	private Date nextFireTime;

	public CronTimerTask(Job job, ThreadPoolExecutor executor) {
		// 检查定时任务的参数是否正确
		this.job = job;
		this.executor = executor;
		try {
			this.cronTrigger = new CronTrigger(job.getCronExpression());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		this.nextFireTime = this.cronTrigger.getFireTimeAfter(null);
		
		log.info("#execute#job#{}#add#{}#", job.getJobName(), DateUtil.format(this.nextFireTime));
	}

	@Override
	public void run(Timeout timeout) throws Exception {
		executor.submit(new Runnable() {

			@Override
			public void run() {
				log.info("#execute#job#{}#{}#{}#{}#begin", job.getJobName());

				try {
					Object target = job.getTarget();
					Method method = job.getMethod();

					method.invoke(target);
				} catch (Throwable e) {
					log.error("定时任务执行异常， {}", e, job.getJobName());
				} finally {
					// 设置下一次的定时任务的时间
					Date fireTimeAfter = CronTimerTask.this.cronTrigger.getFireTimeAfter(nextFireTime);
					CronTimerTask.this.nextFireTime = fireTimeAfter;
					long delay = fireTimeAfter.getTime() - System.currentTimeMillis();
					timeout.timer().newTimeout(CronTimerTask.this, delay, TimeUnit.MILLISECONDS);
					
					log.info("#execute#job#{}#{}#{}#{}#finish#{}#", job.getJobName(), DateUtil.format(fireTimeAfter));
				}
			}
		});
	}
	
	public Date getNextFireTime() {
		return nextFireTime;
	}
	
}
