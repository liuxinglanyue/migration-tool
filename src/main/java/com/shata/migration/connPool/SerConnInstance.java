package com.shata.migration.connPool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.shata.migration.utils.Config;

public class SerConnInstance {
	private final static SerConnInstance instance = new SerConnInstance();
	private MysqlPoolFactory mysqlConn;
	
	private GenericObjectPoolConfig config;

	private SerConnInstance() {
		init_pool_config();
		init();
	}
	
	private static void init_pool_config() {
		instance.config = new GenericObjectPoolConfig();
		instance.config.setLifo(Config.getBoolean("lifo"));
		instance.config.setMaxTotal(Config.getInt("maxTotal"));
		instance.config.setMaxIdle(Config.getInt("maxIdle"));
		instance.config.setMaxWaitMillis(Config.getLong("maxWait"));
		instance.config.setMinEvictableIdleTimeMillis(Config.getLong("minEvictableIdleTimeMillis"));
		instance.config.setMinIdle(Config.getInt("minIdle"));
		instance.config.setNumTestsPerEvictionRun(Config.getInt("numTestsPerEvictionRun"));
		instance.config.setTestOnBorrow(Config.getBoolean("testOnBorrow"));
		instance.config.setTestOnReturn(Config.getBoolean("testOnReturn"));
		instance.config.setTestWhileIdle(Config.getBoolean("testWhileIdle"));
		instance.config.setTimeBetweenEvictionRunsMillis(Config.getLong("timeBetweenEvictionRunsMillis"));
	}
	
	private static void init() {
		String url = Config.getSetting("jdbc_url_migrantion");
		String user = Config.getSetting("jdbc_user_migrantion");
		String password = Config.getSetting("jdbc_password_migrantion");
		instance.mysqlConn = new MysqlPoolFactory(instance.config, url, user, password);
	}
	
	public static MysqlPoolFactory getInstance() {
		if(null == instance.mysqlConn) {
			init_pool_config();
			init();
		}
		return instance.mysqlConn;
	}
	
}
