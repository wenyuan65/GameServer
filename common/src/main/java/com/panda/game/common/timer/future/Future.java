package com.panda.game.common.timer.future;

public interface Future {
	
	public boolean isSuccess();
	
	public boolean isCancelled();
	
	public boolean cancel();
}
