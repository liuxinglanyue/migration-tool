package com.shata.migration.connPool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.shata.migration.utils.Config;


public class ConnInstance {
	private final static ConnInstance instance = new ConnInstance();
	private MysqlPoolFactory mysqlConn_from;
	private MysqlPoolFactory mysqlConn_to;
	
	private GenericObjectPoolConfig config;

	private ConnInstance() {
		init_pool_config();
		init_from();
		init_to();
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
	
	private static void init_from() {
		String url = Config.getSetting("jdbc_url_from");
		String user = Config.getSetting("jdbc_user_from");
		String password = Config.getSetting("jdbc_password_from");
		instance.mysqlConn_from = new MysqlPoolFactory(instance.config, url, user, password);
	}
	
	private static void init_to() {
		String url = Config.getSetting("jdbc_url_to");
		String user = Config.getSetting("jdbc_user_to");
		String password = Config.getSetting("jdbc_password_to");
		instance.mysqlConn_from = new MysqlPoolFactory(instance.config, url, user, password);
	}
	
	public static MysqlPoolFactory getFromInstance() {
		if(null == instance.mysqlConn_from) {
			init_pool_config();
			init_from();
		}
		return instance.mysqlConn_from;
	}
	
	public static MysqlPoolFactory getToInstance() {
		if(null == instance.mysqlConn_to) {
			init_pool_config();
			init_to();
		}
		return instance.mysqlConn_to;
	}
}
