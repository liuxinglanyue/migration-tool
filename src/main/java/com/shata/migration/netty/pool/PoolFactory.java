package com.shata.migration.netty.pool;

import com.shata.migration.netty.Client;

public interface PoolFactory {

	public Client getConnection() throws Exception;

	public void releaseConnection(Client conn) throws Exception;
}
