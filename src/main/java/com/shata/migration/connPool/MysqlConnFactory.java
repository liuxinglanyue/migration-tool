package com.shata.migration.connPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MysqlConnFactory implements PooledObjectFactory<Connection> {
	private final static Logger log = LoggerFactory.getLogger(MysqlConnFactory.class);
	
	private String url;
	private String user;
	private String password;
	
	public MysqlConnFactory(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	@Override
	public void activateObject(PooledObject<Connection> p) throws Exception {
		
	}

	@Override
	public void destroyObject(PooledObject<Connection> p) throws Exception {
		if(null != p) {
			Connection conn = p.getObject();
			if(null != conn) {
				conn.close();
			}
		}
	}

	@Override
	public PooledObject<Connection> makeObject() throws Exception {
		Connection conn = DriverManager.getConnection(url, user, password);
		return new DefaultPooledObject<Connection>(conn);
	}

	@Override
	public void passivateObject(PooledObject<Connection> p) throws Exception {
		
	}

	@Override
	public boolean validateObject(PooledObject<Connection> p) {
		if(null == p) {
			return false;
		}
		Connection conn = p.getObject();
		try {
			return conn.isValid(1);
		} catch (SQLException e) {
			log.error("连接不可用，" + conn.toString());
			if(null != conn) {
				try {
					conn.close();
				} catch (SQLException e1) {
					log.error("连接关闭失败，" + conn.toString(), e1);
				}
			}
		}
		return false;
	}

}
