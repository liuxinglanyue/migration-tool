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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.connPool.MysqlPoolFactory;
import com.shata.migration.constants.MigrationConstants;
import com.shata.migration.exception.ConnectionException;


public class JdbcManager {
	private final static Logger log = LoggerFactory.getLogger(JdbcManager.class);
	
	public static boolean exist(MysqlPoolFactory pool, String sql) {
		boolean flag = false;
		Connection connection = getConnection(pool);
		if(null != connection) {
			Statement stmt = null;
			try {
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				while(rs.next()) {
					flag = true;
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
		return flag;
	}

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
			, String sql, String insert_sql, String select_sql, String column_append, boolean fail, boolean exist_all_content) {
		boolean is_sp_code = false;
		boolean is_all_content = false;
		if(null != column_append) {
			if("sp_code".equals(column_append)) {
				is_sp_code = true;
			} else if("all_content".equals(column_append)) {
				is_all_content = true;
			}
		}
		//1 查询数据
		List<List<String>> segementValues = new ArrayList<List<String>>();
		
		Connection connection = getConnection(pool_from);
		if(null == connection) {
			return false;
		}
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			
			ResultSet rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				List<String> list = new ArrayList<String>(rsmd.getColumnCount());
				String sp_code = "";
				String all_content = "";
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String column = rsmd.getColumnName(i).toLowerCase();
					String value = rs.getString(i);
					if(MigrationConstants.isEmpty(column, value)) {
						list = null;
						break;
					}
					//判断日期record_time
					if(!fail && "record_time".equals(column)) {
						int flag = MigrationConstants.compare(value);
						if(flag == 0) {
							log.debug("record_time=" + value + ",危险数据，需要校验。");
							fail = true;
						} else if(flag == 1) {
							//大于跨度时间的上限，不插入到数据库
							list = null;
							break;
						}
					}
					
					//获取sp_code
					if(is_sp_code && "channel_idn".equals(column)) {
						sp_code = StringUtils.split(value, "-")[0];
					}
					//获取all_content
					if(is_all_content && !"sp_code".equals(column) && !"all_content".equals(column)) {
						all_content += value;
					}
					
					list.add(value);
				}
				if(null != list) {
					//这里只要为true，就需要sp_code 或 all_content 哪怕为 空
					if(is_sp_code) {
						list.add(sp_code);
					} else if(is_all_content) {
						list.add(all_content);
					}
					segementValues.add(list);
				}
			}

			rs.close();
		} catch (SQLException e) {
			log.error("sql:" + sql + "执行失败！", e);
			return false;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					log.error("Statement关闭异常", e);
				}
			}
			releaseConnection(pool_from, connection);
		}
				
		
		//segementValues中无数据，返回成功
		if(null == segementValues || segementValues.size() == 0) {
			return true;
		}
		
		//2 分析数据
		//当fail为true时，需要判断数据库中是否已存在此数据
		List<List<String>> succValues = new ArrayList<List<String>>(segementValues.size());
		if(fail) {
			Connection conn_to = getConnection(pool_to);
			if(null == conn_to) {
				return false;
			}
			PreparedStatement ps = null;
			try {
				ps = conn_to.prepareStatement(select_sql);
				for(List<String> list : segementValues) {
					int size = exist_all_content ? list.size() - 1 : list.size();
					for(int i=0; i<size; i++) {
						ps.setString(i + 1, list.get(i));
					}
					ResultSet rs = ps.executeQuery();
					if(!rs.next()) {
						succValues.add(list);
					}
					rs.close();
				}
				
			} catch (SQLException e) {
				log.error("select_sql:" + select_sql + "执行失败！", e);
				return false;
			} finally {
				if (ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {
						log.error("PreparedStatement关闭异常", e);
					}
				}
				releaseConnection(pool_to, conn_to);
			}
		} else {
			succValues = segementValues;
		}
		
		if(null == succValues || succValues.size() == 0) {
			return true;
		}
		
		//3 插入数据
		Connection conn_to = getConnection(pool_to);
		if(null == conn_to) {
			return false;
		}
		PreparedStatement ps = null;
		try {
			ps = conn_to.prepareStatement(insert_sql);
			for(List<String> list : succValues) {
				for(int i=0; i<list.size(); i++) {
					ps.setString(i + 1, list.get(i));
				}
				ps.addBatch();
			}
			
			ps.executeBatch();
			ps.clearBatch();
		} catch (SQLException e) {
			log.error("insert_sql:" + insert_sql + "执行失败！", e);
			return false;
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					log.error("PreparedStatement关闭异常", e);
				}
			}
			releaseConnection(pool_to, conn_to);
		}
		
		return true;
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
