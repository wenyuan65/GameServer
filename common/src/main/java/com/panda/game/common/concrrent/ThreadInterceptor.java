package com.panda.game.common.concrrent;

public interface ThreadInterceptor {

	public void executeBefore(ThreadContext context);
	
	public void executeAfter(ThreadContext context);
}
