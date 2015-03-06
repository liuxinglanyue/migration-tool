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
	
	private static void init() {
		String targetIP = Config.getSetting("host");
		int targetPort = Config.getInt("port");
		int connectTimeout = Config.getInt("timeout");
		_self.factory = new NettyPoolFactory(_self.config, targetIP, targetPort, connectTimeout);
	}

	private static void init_pool_config() {
		_self.config = new GenericObjectPoolConfig();
		_self.config.setLifo(Config.getBoolean("lifo"));
		_self.config.setMaxTotal(Config.getInt("maxTotal"));
		_self.config.setMaxIdle(Config.getInt("maxIdle"));
		_self.config.setMaxWaitMillis(Config.getLong("maxWait"));
		_self.config.setMinEvictableIdleTimeMillis(Config.getLong("minEvictableIdleTimeMillis"));
		_self.config.setMinIdle(Config.getInt("minIdle"));
		_self.config.setNumTestsPerEvictionRun(Config.getInt("numTestsPerEvictionRun"));
		_self.config.setTestOnBorrow(Config.getBoolean("testOnBorrow"));
		_self.config.setTestOnReturn(Config.getBoolean("testOnReturn"));
		_self.config.setTestWhileIdle(Config.getBoolean("testWhileIdle"));
		_self.config.setTimeBetweenEvictionRunsMillis(Config.getLong("timeBetweenEvictionRunsMillis"));
	}
	
	public static PoolFactory getInstance() {
		if(null == _self.factory) {
			init_pool_config();
			init();
		}
		return _self.factory;
	}
	
	
}
