package com.shata.migration.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.connPool.MysqlPoolFactory;
import com.shata.migration.exception.ConnectionException;


public class JdbcManager {
	private final static Logger log = LoggerFactory.getLogger(JdbcManager.class);

	public void update(MysqlPoolFactory pool, String sql) {
		Connection connection = getConnection(pool);
		if(null != connection) {
			Statement stmt = null;
			try {
				stmt = connection.createStatement();
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				log.error("sql:" + sql + "执行失败！", e);
			} finally {
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						log.error("Statement关闭异常", e);
					}
				}
				releaseConnection(pool, connection);
			}
				
		}
	}
	
	public List<Map<String, String>> queryMap(MysqlPoolFactory pool, String sql) {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		
		Connection connection = getConnection(pool);
		if(null != connection) {
			Statement stmt = null;
			try {
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				ResultSetMetaData rsmd = rs.getMetaData();

				while (rs.next()) {
					Map<String, String> hashMap = new HashMap<String, String>();
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						String column = rsmd.getColumnName(i).toLowerCase();
						hashMap.put(column, rs.getString(i));
					}
					dataList.add(hashMap);
				}

				rs.close();
			} catch (SQLException e) {
				log.error("sql:" + sql + "执行失败！", e);
			} finally {
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						log.error("Statement关闭异常", e);
					}
				}
				releaseConnection(pool, connection);
			}
				
		}
		return dataList;
	}
	
	
	public Connection getConnection(MysqlPoolFactory pool) {
		try {
			return pool.getConnection();
		} catch (Exception e) {
			log.error("获取连接异常", e);
			throw new ConnectionException("获取连接异常");
		}
	}
	
	public void releaseConnection(MysqlPoolFactory pool, Connection connection) {
		try {
			pool.releaseConnection(connection);
		} catch (Exception e) {
			log.error("回收连接异常！", e);
		}
	}
}
