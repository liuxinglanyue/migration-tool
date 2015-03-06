package com.shata.migration.connPool;

import java.sql.Connection;

public interface PoolFactory {

	public Connection getConnection() throws Exception;
	
	public void releaseConnection(Connection conn) throws Exception;
}
