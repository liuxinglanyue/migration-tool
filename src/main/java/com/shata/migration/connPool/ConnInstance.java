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
	
	private void init_from() {
		String url = Config.getSetting("jdbc_url_from");
		String user = Config.getSetting("jdbc_user_from");
		String password = Config.getSetting("jdbc_password_from");
		mysqlConn_from = new MysqlPoolFactory(config, url, user, password);
	}
	
	private void init_to() {
		String url = Config.getSetting("jdbc_url_to");
		String user = Config.getSetting("jdbc_user_to");
		String password = Config.getSetting("jdbc_password_to");
		mysqlConn_to = new MysqlPoolFactory(config, url, user, password);
	}
	
	public static MysqlPoolFactory getFromInstance() {
		return instance.mysqlConn_from;
	}
	
	public static MysqlPoolFactory getToInstance() {
		return instance.mysqlConn_to;
	}
}
