package com.shata.migration.connPool;

import java.sql.Connection;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class MysqlPoolFactory implements PoolFactory {
	private GenericObjectPool<Connection> pool;

	public MysqlPoolFactory(GenericObjectPoolConfig config, String url, String user, String password) {
		MysqlConnFactory factory = new MysqlConnFactory(url, user, password);
		pool = new GenericObjectPool<Connection>(factory, config);
	}
	
	@Override
	public Connection getConnection() throws Exception {
		return pool.borrowObject();
	}

	@Override
	public void releaseConnection(Connection conn) throws Exception {
		pool.returnObject(conn);
	}

}
