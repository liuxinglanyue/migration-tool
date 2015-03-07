package com.shata.migration.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.exception.ConfigException;

public class Config {
	private final static Logger log = LoggerFactory.getLogger(Config.class);

	private static Map<String, String> setting = new ConcurrentHashMap<String, String>();

	static {
		iniSetting();
	}

	public static synchronized void iniSetting() {
		File file = new File("config.properties");
		if (!(file.exists()))
			iniSetting("config/config.properties");
		else
			iniSetting("config.properties");
		
		File poolFile = new File("config/pool.properties");
		if(poolFile.exists()) {
			iniSetting("config/pool.properties");
		}
	}

	public static synchronized void iniSetting(String path) {
		File file = new File(path);
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			Properties p = new Properties();
			p.load(in);

			Enumeration<?> item = p.propertyNames();
			while (item.hasMoreElements()) {
				String key = (String) item.nextElement();
				setting.put(key, p.getProperty(key).trim());
			}
			in.close();
		} catch (FileNotFoundException e) {
			log.error("config file not found at" + file.getAbsolutePath());
			throw new ConfigException("FileNotFoundException", e);
		} catch (IOException e) {
			log.error("config file not found at" + file.getAbsolutePath());
			throw new ConfigException("IOException", e);
		} catch (Exception e) {
			throw new ConfigException("Exception", e);
		}
	}

	public static void reload() {
		try {
			iniSetting();
		} catch (ConfigException e) {
			throw new ConfigException(e.getMessage(), e);
		}
	}

	public static String getSetting(String key) {
		return ((String) setting.get(key));
	}

	public static void setSetting(String key, String value) {
		setting.put(key, value);
	}

	public static int getInt(String key) {
		return Integer.parseInt(setting.get(key));
	}
	
	public static long getLong(String key) {
		return Long.parseLong(setting.get(key));
	}
	
	public static boolean getBoolean(String key) {
		return Boolean.parseBoolean(setting.get(key));
	}
}
