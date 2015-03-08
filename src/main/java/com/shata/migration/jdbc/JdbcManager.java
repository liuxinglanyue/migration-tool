package com.shata.migration.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
import com.shata.migration.constants.MigrationConstants;
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
	
	public static boolean migration(MysqlPoolFactory pool_from, MysqlPoolFactory pool_to
			, String sql, String insert_sql, String select_sql, boolean fail) {
		
		Connection connection = getConnection(pool_from);
		Connection conn_to = getConnection(pool_to);
		if(null != connection) {
			Statement stmt = null;
			PreparedStatement ps = null;
			try {
				stmt = connection.createStatement();
				ps = conn_to.prepareStatement(insert_sql);
				
				ResultSet rs = stmt.executeQuery(sql);
				ResultSetMetaData rsmd = rs.getMetaData();

				while (rs.next()) {
					List<String> list = new ArrayList<String>();
					boolean flag = true;
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						String column = rsmd.getColumnName(i).toLowerCase();
						String value = rs.getString(i);
						if(MigrationConstants.isEmpty(column, value)) {
							flag = false;
							break;
						}
						list.add(value);
					}
					
					if(flag && list.size() > 0) {
						for(int i=0; i<list.size(); i++) {
							ps.setString(i + 1, list.get(i));
						}
						ps.addBatch();
					}
				}
				ps.executeBatch();
				ps.clearBatch();

				rs.close();
			} catch (SQLException e) {
				log.error("sql:" + sql + "执行失败！", e);
			} finally {
				if (stmt != null) {
					try {
						stmt.close();
						ps.close();
					} catch (SQLException e) {
						log.error("Statement关闭异常", e);
					}
				}
				releaseConnection(pool_to, conn_to);
				releaseConnection(pool_from, connection);
			}
				
		}
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
