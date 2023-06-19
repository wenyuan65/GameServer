package com.panda.game.common.config;

import com.panda.game.common.concrrent.ScheduledThread;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Configuration {
	
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	
	/** 文件的扫描路径 */
	private static final String CONFIG_PATH = "." + File.separator + "apps";
	/** 通用配置文件类型 */
	private static final String CONFIG_TYPE_COMMON = "common";
	
	/** 检查文件修改的间隔时间 */
	private static final long CHECK_MODIFIED_INTERVEL = TimeUnit.SECONDS.toMillis(10);
	/** 上次检查时间 */
	private static ConcurrentHashMap<String, Long> lastModifyTimeMap = new ConcurrentHashMap<>();
	
	/** common文件的集合 */
	private static Set<String> commonFileNames = new HashSet<>();
	
	/** 配置属性的存储 */
	private static ConcurrentHashMap<String, Map<String, String>> map = new ConcurrentHashMap<>();
	/** 线程是否启动 */
	private static volatile boolean started = false;
	/** 配置文件扫描线程 */
	private static ScheduledThread configurationScanner = null;
	
	/**
	 * 初始化通用配置文件
	 * @param commonFileNameList
	 */
	public static boolean init(List<String> commonFileNameList) {
		if (started) {
			return false;
		}
		
		commonFileNames.addAll(commonFileNameList);
		
		// 扫描文件
		reloadConfig(CONFIG_PATH);
		started = true;

		// 配置文件监控线程
		configurationScanner = new ScheduledThread("Configuration-scanner", new ConfigurationScanTask(), CHECK_MODIFIED_INTERVEL);
		configurationScanner.start();

		return true;
	}
	
	/**
	 * 重载配置文件
	 * @param path
	 */
	private static void reloadConfig(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			String[] subFiles = file.list();
			for (String subPath : subFiles) {
				reloadConfig(path + File.separator + subPath);
			}
		} else {
			reloadFileConfig(file, path);
		}
	}
	
	/**
	 * 加载文件
	 * @param file
	 */
	private static void reloadFileConfig(File file, String filePath) {
		if (filePath.endsWith("/common.properties")) {
			logger.error("配置文件不能取名为common.properties: {}", CONFIG_TYPE_COMMON, filePath);
			return;
		}
		if (!filePath.endsWith(".properties")) {
			return;
		}

		Long lastModifyTime = lastModifyTimeMap.get(file.getPath());
		if (lastModifyTime != null && lastModifyTime == file.lastModified()) {
			return;
		} else if (lastModifyTime != null) {
			logger.info("重新加载配置文件: {}", filePath);
		} else {
			logger.info("加载配置文件: {}", filePath);
		}
		
		String name = file.getName();
		String configFileType = "";
		if (commonFileNames.contains(name)) {
			configFileType = CONFIG_TYPE_COMMON;
		} else {
			configFileType = getFileNameWithoutPrefix(name);
		}
		
		logger.info("found properties file:{}, flag: {}", name, configFileType);
		map.putIfAbsent(configFileType, new ConcurrentHashMap<>());
		Map<String, String> currConfigMap = map.get(configFileType);
		
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(file.getAbsolutePath()));
			
			for (Entry<Object, Object> entry : prop.entrySet()) {
				// 更新属性
				currConfigMap.put((String)entry.getKey(), (String)entry.getValue());
			}
			
			lastModifyTimeMap.put(file.getPath(), file.lastModified());
		} catch (IOException e) {
			logger.error("load properties error: {} ", e, filePath);
		}
	}
	
	/**
	 * 获取common中的key对应值
	 * @param key
	 * @return
	 */
	public static String getProperty(String key) {
		return getProperty(CONFIG_TYPE_COMMON, key);
	}
	
	/**
	 * 从yx配置文件中获取key对应的值
	 * @param yx
	 * @param key
	 * @return
	 */
	public static String getProperty(String yx, String key) {
		Map<String, String> map2 = map.get(yx);
		return map2 != null ? map2.get(key) : null;
	}
	
	/**
	 * 优先从yx配置文件中获取channelId.key对应的值,其次取channelId.key对应的值，最后取key对应的值
	 * @param yx
	 * @param channelId
	 * @param key
	 * @return
	 */
	public static String getProperty(String yx, String channelId, String key) {
		String value = null;
		String channelKey = getKey(channelId, key);
		if (StringUtils.isNotBlank(yx)) {
			value = getProperty(yx, channelKey);
			if (StringUtils.isBlank(value)) {
				value = getProperty(yx, key);
			}
		}
		
		if (StringUtils.isBlank(value)) {
			value = getProperty(channelKey);
		} 
		
		if (StringUtils.isBlank(value)) {
			value = getProperty(key);
		} 
		
		return value;
	}
	
	/**
	 * 使用两段字符串拼接起来的key，查找配置
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	public static String getKey(String prefix, String suffix) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append('.').append(suffix);
		
		return getProperty(sb.toString());
	}
	
	public static Integer getIntProperty(String key, Integer defaultValue) {
		String propValue = getProperty(key);
		if (propValue != null) {
			return Integer.parseInt(propValue);
		}

		return defaultValue;
	}

	public static Integer getIntProperty(String yx, String key, Integer defaultValue) {
		String propValue = getProperty(yx, key);
		if (propValue != null) {
			return Integer.parseInt(propValue);
		}

		return defaultValue;
	}

	public static Integer getIntProperty(String yx, String channelId, String key, Integer defaultValue) {
		String propValue = getProperty(yx, channelId, key);
		if (propValue != null) {
			return Integer.parseInt(propValue);
		}

		return defaultValue;
	}
	
	/**
	 * 获取文件名，例如 server.properties == > server
	 * @param fileName
	 * @return
	 */
	private static String getFileNameWithoutPrefix(String fileName) {
		int index = fileName.indexOf(".");
		return index >= 0 ? fileName.substring(0, index) : fileName;
	}
	
	public static class ConfigurationScanTask implements Runnable {

		@Override
		public void run() {
			if (!started) {
				return;
			}
			
			reloadConfig(CONFIG_PATH);
		}
		
	}
	
}
