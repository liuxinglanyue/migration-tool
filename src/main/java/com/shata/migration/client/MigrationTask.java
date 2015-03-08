package com.shata.migration.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.connPool.ConnInstance;
import com.shata.migration.constants.Commands;
import com.shata.migration.exception.MigrationException;
import com.shata.migration.jdbc.JdbcManager;
import com.shata.migration.netty.Client;
import com.shata.migration.netty.pool.NettyInstance;
import com.shata.migration.utils.Config;
import com.shata.migration.utils.InetInfo;

public class MigrationTask implements Runnable {
	private final static Logger log = LoggerFactory.getLogger(MigrationTask.class);

	private String table;
	private String table_to;
	private String column_from;
	private String column_to;
	
	private long min;
	private long max;
	private boolean fail;
	
	private Client conn = null;
	
	
	@Override
	public void run() {
		try {
			conn = NettyInstance.getInstance().getConnection();
		} catch (Exception e) {
			log.error("获取netty连接失败！", e);
			throw new MigrationException("获取netty连接失败！");
		}
		while(true) {
			//1 注册设备
			reg_device();
			if(Commands.TABLE_TABLE.equals(table)) {
				log.info("所有表都迁移完成！");
				break;
			}
			
			//2 获取迁移的id段
			get_segement();
			if(min == 0 && max == 0) {
				//当前表迁移完成，先注销当前连接，重新注册设备
				logout_device();
				reg_device();
				if(Commands.TABLE_TABLE.equals(table)) {
					log.info("所有表都迁移完成！");
					break;
				}
				continue;
			}
			
			//3 迁移
			boolean flag = JdbcManager.migration(ConnInstance.getFromInstance(), table, column_from
					, ConnInstance.getToInstance(), table_to, column_to, fail);
			
			//4 状态更新
			update_status(flag ? Commands.STATUS_SUCC : Commands.STATUS_FAIL);
		}
		
		//归还连接
		try {
			NettyInstance.getInstance().releaseConnection(conn);
		} catch (Exception e) {
			log.error("归还netty连接失败！", e);
		}
	}
	
	
	public boolean get_segement() {
		String[] bodies = null;
		for(int i = 0; i < 10; i++) {
			try {
				bodies = (String[]) conn.invokeSync(Commands.GET_SEGEMENT + "|" + table + "|" + InetInfo.DEVICE_NAME + "|" + Thread.currentThread().getName());
				if(null != bodies && (bodies.length == 3 || bodies.length == 4) 
						&& !"-1".equals(bodies[1]) && !"-1".equals(bodies[2])) {
					break;
				}
			} catch (Exception e) {
				log.error("获取id段异常！" + table + "|" + InetInfo.DEVICE_NAME + "|" + Thread.currentThread().getName(), e);
			}
		}
		if(null == bodies || (bodies.length != 5 && bodies.length != 4) || "-1".equals(bodies[1]) || "-1".equals(bodies[2])) {
			throw new MigrationException("获取id段，重试10次还是失败！" + table + "|" + InetInfo.DEVICE_NAME + "|" + Thread.currentThread().getName());
		}
		
		min = Long.parseLong(bodies[1]);
		max = Long.parseLong(bodies[2]);
		if(bodies.length == 4) {
			fail = true;
		}
		
		return true;
	}
	
	public boolean update_status(String status) {
		String[] bodies = null;
		for(int i = 0; i < 10; i++) {
			try {
				bodies = (String[]) conn.invokeSync(Commands.UPDATE_STATUS + "|" + table + "|" + min + "|" + max + "|" + status);
				if(null != bodies && bodies.length == 2) {
					break;
				}
			} catch (Exception e) {
				log.error("更新状态异常！" + table + "|" + min + "|" + max + "|" + status, e);
			}
		}
		if(null == bodies || bodies.length != 2) {
			log.error("更新状态，重试10次还是失败！" + table + "|" + min + "|" + max + "|" + status);
			return false;
			//更新状态 异常，不将线程中断， 逻辑上不影响程序的正确性
		}
		
		if(Commands.SUCC.equals(bodies[1])) {
			return true;
		}
		
		return false;
	}
	
	public boolean logout_device() {
		String[] bodies = null;
		for(int i = 0; i < 10; i++) {
			try {
				bodies = (String[]) conn.invokeSync(Commands.LOGOUT_DEVICE + "|" + InetInfo.DEVICE_NAME + "|" + Thread.currentThread().getName());
				if(null != bodies && bodies.length == 2) {
					break;
				}
			} catch (Exception e) {
				log.error("注销设备异常！" + InetInfo.DEVICE_NAME + "|" + Thread.currentThread().getName(), e);
			}
		}
		if(null == bodies || bodies.length != 2) {
			log.error("注销设备，重试10次还是失败！" + InetInfo.DEVICE_NAME + "|" + Thread.currentThread().getName());
			return false;
			//注销失败，不将 线程中断，因为会对 下线的设备进行能力值回收
			//throw new MigrationException("注销设备，重试10次还是失败！" + InetInfo.DEVICE_NAME + "|" + Thread.currentThread().getName());
		}
		
		if(Commands.SUCC.equals(bodies[1])) {
			return true;
		}
		
		return false;
	}
	
	public boolean reg_device() {
		String[] bodies = null;
		for(int i = 0; i < 10; i++) {
			try {
				bodies = (String[]) conn.invokeSync(Commands.REG_DEVICE + "|" + InetInfo.DEVICE_NAME + "|" + Thread.currentThread().getName() + "|" + Config.getSetting("ability"));
				if(null != bodies && bodies.length == 5) {
					break;
				}
			} catch (Exception e) {
				log.error("注册设备异常！" + InetInfo.DEVICE_NAME + "|" + Thread.currentThread().getName(), e);
			}
		}
		if(null == bodies || bodies.length != 5) {
			throw new MigrationException("注册设备，重试10次还是失败！" + InetInfo.DEVICE_NAME + "|" + Thread.currentThread().getName());
		}
		
		table = bodies[1];
		table_to = bodies[2];
		column_from = bodies[3];
		column_to = bodies[4];
		
		return true;
	}

}
