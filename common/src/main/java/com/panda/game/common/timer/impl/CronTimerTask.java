package com.panda.game.common.timer.impl;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.timer.Timeout;
import com.panda.game.common.timer.TimerTask;
import com.panda.game.common.timer.quartz.CronTrigger;
import com.panda.game.common.timer.quartz.Job;
import com.panda.game.common.utils.DateUtil;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CronTimerTask implements TimerTask {
	
	private static final Logger log = LoggerFactory.getRtLog();
	
	private Job job;
	private ThreadPoolExecutor executor;
	private CronTrigger cronTrigger;
	private Date nextFireTime;
	private Method method;
	
	public CronTimerTask(Job job, ThreadPoolExecutor executor) throws Exception {
		// 检查定时任务的参数是否正确
		// TODO:
		this.method = null;
		
		this.job = job;
		this.executor = executor;
		this.cronTrigger = new CronTrigger(job.getCronExpression());
		this.nextFireTime = this.cronTrigger.getFireTimeAfter(null);
		
		log.info("#execute#job#{}#{}#{}#{}#first#{}#", job.getJobId(), job.getJobName(), job.getBeanName(), 
			job.getMethodName(), DateUtil.format(this.nextFireTime));
	}

	@Override
	public void run(Timeout timeout) throws Exception {
		executor.submit(new Runnable() {

			@Override
			public void run() {
				log.info("#execute#job#{}#{}#{}#{}#begin", job.getJobId(), job.getJobName(), 
						job.getBeanName(), job.getMethodName());
				try {
					// TODO:
//					Object bean = ctx.getBean(job.getBeanName());
					method.invoke(null);
				} catch (Throwable e) {
					log.error("job {}", e, job.getJobName());
				} finally {
					// 设置下一次的定时任务的时间
					Date fireTimeAfter = CronTimerTask.this.cronTrigger.getFireTimeAfter(nextFireTime);
					CronTimerTask.this.nextFireTime = fireTimeAfter;
					long delay = fireTimeAfter.getTime() - System.currentTimeMillis();
					timeout.timer().newTimeout(CronTimerTask.this, delay, TimeUnit.MILLISECONDS);
					
					log.info("#execute#job#{}#{}#{}#{}#finish#{}#", job.getJobId(), job.getJobName(), job.getBeanName(), 
						job.getMethodName(), DateUtil.format(fireTimeAfter));
				}
			}
		});
	}
	
	public Date getNextFireTime() {
		return nextFireTime;
	}
	
	
	
}
