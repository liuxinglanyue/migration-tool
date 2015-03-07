package com.shata.migration.netty.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.utils.Config;

public class NettyInstance {
	private final static Logger log = LoggerFactory.getLogger(NettyInstance.class);
	
	private final static NettyInstance _self = new NettyInstance();
	
	private GenericObjectPoolConfig config;
	
	private PoolFactory factory;
	
	private NettyInstance() {
		init_pool_config();
		init();
	}
	
	private void init() {
		String targetIP = Config.getSetting("host");
		int targetPort = Config.getInt("port");
		int connectTimeout = Config.getInt("timeout");
		factory = new NettyPoolFactory(config, targetIP, targetPort, connectTimeout);
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
	
	public static PoolFactory getInstance() {
		return _self.factory;
	}
	
	
}
