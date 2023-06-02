package com.panda.game.core.session;

import com.panda.game.common.concrrent.ScheduledThread;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.utils.RandomUtils;
import com.panda.game.common.utils.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
	
	private static final Logger log = LoggerFactory.getLogger(SessionManager.class);
	
	public SessionManager() {
	}

	private static final SessionManager INSTANCE = new SessionManager();

	public static SessionManager getInstance() {
		return INSTANCE;
	}
	
	/** sessionId的长度 */
	private static final int SESSIONID_LENGTH = 16;
	
	private ConcurrentHashMap<String, Session> sessionHolder = new ConcurrentHashMap<>();
	
	/** 执行session检查的耗时 */
	private long processingTime = 0;
	/** 执行session检查次数 */
	private int processedCount = 0;
	
	/** session管理线程 */
	private ScheduledThread sessionManagerTicker = null;
	
	/**
	 * 启动session管理
	 */
	public void start() {
		sessionManagerTicker = new ScheduledThread("session-ticker", new SessionCheckTask(), 10000);
		sessionManagerTicker.start();
	}
	
	public Session getSession(String sessionId, boolean createIfNotExist) {
		if (StringUtils.isBlank(sessionId) && createIfNotExist) {
			sessionId = generateSessionId();
			return new DefaultSession(sessionId);
		}
		
		Session session = sessionHolder.get(sessionId);
		if (session == null && createIfNotExist) {
			session = new DefaultSession(sessionId);
		}
		
		return session;
	}
	
	public void addSession(Session session) {
		sessionHolder.put(session.getSessionId(), session);
	}
	
	private String generateSessionId() {
		String sessionId = RandomUtils.generateRandomString(SESSIONID_LENGTH);
		if (!sessionHolder.containsKey(sessionId)) {
			return sessionId;
		}

		// 重试
		int n = 5;
		while (n > 0) {
			sessionId = RandomUtils.generateRandomString(SESSIONID_LENGTH);
			if (!sessionHolder.containsKey(sessionId)) {
				return sessionId;
			}
			n --;
		}

		return sessionId;
	}
	
	private class SessionCheckTask implements Runnable {

		@Override
		public void run() {
			long now = System.currentTimeMillis();
			Session[] sessions = sessionHolder.values().toArray(new Session[0]);
			
	        int expireHere = 0 ;
	        log.info("Start expire sessions SessionManager, at {}, sessioncount {}", now, sessions.length);
	        for (Session session : sessions) {
	            if (session != null && !session.isValid()) {
	                expireHere++;
	            }
	        }
	        long timeEnd = System.currentTimeMillis();
	        long costTime = timeEnd - now;
	        log.info("End expire sessions SessionManager, processingTime: {}, expired sessions: {}", costTime, expireHere);
	        processingTime += costTime;
	        processedCount ++;
	        
	        log.info("Session Check, cost time:{} ms, avg:{} ms, total:{} ms", costTime, processingTime * 1.0 / processedCount, processingTime);
		}
		
	}
}
