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
	
	private void init_pool_config() {
		config = new GenericObjectPoolConfig();
		config.setLifo(Config.getBoolean("lifo"));
		config.setMaxTotal(Config.getInt("maxTotal"));
		config.setMaxIdle(Config.getInt("maxIdle"));
		config.setMaxWaitMillis(Config.getLong("maxWait"));
		config.setMinEvictableIdleTimeMillis(Config.getLong("minEvictableIdleTimeMillis"));
		config.setMinIdle(Config.getInt("minIdle"));
		config.setNumTestsPerEvictionRun(Config.getInt("numTestsPerEvictionRun"));
		config.setTestOnBorrow(Config.getBoolean("testOnBorrow"));
		config.setTestOnReturn(Config.getBoolean("testOnReturn"));
		config.setTestWhileIdle(Config.getBoolean("testWhileIdle"));
		config.setTimeBetweenEvictionRunsMillis(Config.getLong("timeBetweenEvictionRunsMillis"));
	}
	
	private void init() {
		String url = Config.getSetting("jdbc_url_migrantion");
		String user = Config.getSetting("jdbc_user_migrantion");
		String password = Config.getSetting("jdbc_password_migrantion");
		mysqlConn = new MysqlPoolFactory(config, url, user, password);
	}
	
	public static MysqlPoolFactory getInstance() {
		return instance.mysqlConn;
	}
	
}
