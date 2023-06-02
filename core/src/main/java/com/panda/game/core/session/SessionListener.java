package com.panda.game.core.session;

public interface SessionListener {
	
	public void fireEvent(Session session, String type);
	
}
