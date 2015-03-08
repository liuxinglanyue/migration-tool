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

	public static boolean update(MysqlPoolFactory pool, String sql) {
		boolean flag = false;
		Connection connection = getConnection(pool);
		if(null != connection) {
			Statement stmt = null;
			try {
				stmt = connection.createStatement();
				stmt.executeUpdate(sql);
				flag = true;
			} catch (SQLException e) {
				log.error("sql:" + sql + "执行失败！", e);
				flag = false;
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
		return flag;
	}
	
	public static boolean delete(MysqlPoolFactory pool, String sql) {
		boolean flag = false;
		Connection connection = getConnection(pool);
		if(null != connection) {
			Statement stmt = null;
			try {
				stmt = connection.createStatement();
				stmt.execute(sql);
				flag = true;
			} catch (SQLException e) {
				log.error("sql:" + sql + "执行失败！", e);
				flag = false;
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
		return flag;
	}
	
	public static Map<String, String> queryOneMap(MysqlPoolFactory pool, String sql) {
		Map<String, String> hashMap = new HashMap<String, String>();
		
		Connection connection = getConnection(pool);
		if(null != connection) {
			Statement stmt = null;
			try {
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				ResultSetMetaData rsmd = rs.getMetaData();

				while (rs.next()) {
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						String column = rsmd.getColumnName(i).toLowerCase();
						hashMap.put(column, rs.getString(i));
					}
					break;
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
		return hashMap;
	}
	
	public static List<Map<String, String>> queryMap(MysqlPoolFactory pool, String sql) {
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
	
	public static boolean migration(MysqlPoolFactory pool_from, String table_from, String column_from
			, MysqlPoolFactory pool_to, String table_to, String column_to, boolean fail) {
		
		return false;
	}
	
	
	
	
	public static Connection getConnection(MysqlPoolFactory pool) {
		try {
			return pool.getConnection();
		} catch (Exception e) {
			log.error("获取连接异常", e);
			throw new ConnectionException("获取连接异常");
		}
	}
	
	public static void releaseConnection(MysqlPoolFactory pool, Connection connection) {
		try {
			pool.releaseConnection(connection);
		} catch (Exception e) {
			log.error("回收连接异常！", e);
		}
	}
}
