package com.panda.game.common.timer.quartz;

import java.lang.reflect.Method;

public class Job {
	
	private int jobId;
	private String jobName;
	private Object target;
	private Method method;
	private String cronExpression;
	
	public Job(int jobId, String jobName, Object target, Method method, String cronExpression) {
		this.jobId = jobId;
		this.jobName = "job_" + jobName + "_" + jobId;
		this.target = target;
		this.method = method;
		this.cronExpression = cronExpression;
	}

	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
}
