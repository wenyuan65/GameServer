package com.panda.game.common.config;

import com.panda.game.common.utils.MessageFormat;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class LocalMessage {
	
	/** 语言缓存 */
	private static ConcurrentHashMap<String, String> msgMap = new ConcurrentHashMap<>();
	
	/**
	 * 获取语言
	 * @param key
	 * @return
	 */
	public static String getText(String key) {
		String value = msgMap.get(key);
		if (value == null) {
			ResourceBundle bundle = ResourceBundle.getBundle("package", Locale.getDefault(), LocalMessage.class.getClassLoader());
			value = bundle.getString(key);
			
			msgMap.putIfAbsent(key, value);
		}
		
		return value;
	}
	
	/**
	 * 获取带参数的语言
	 * @param key
	 * @param args
	 * @return
	 */
	public static String getText(String key, Object... args) {
		String text = getText(key);
		
		return MessageFormat.format(text, args);
	}
	
}
