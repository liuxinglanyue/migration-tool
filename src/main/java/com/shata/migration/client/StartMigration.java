package com.shata.migration.client;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.constants.Commands;
import com.shata.migration.netty.Client;
import com.shata.migration.netty.pool.NettyInstance;
import com.shata.migration.netty.pool.PoolFactory;
import com.shata.migration.utils.Config;
import com.shata.migration.utils.InetInfo;

public class StartMigration {
	private final static Logger log = LoggerFactory.getLogger(StartMigration.class);
	
	static {
		PropertyConfigurator.configure("config/log4j.properties");
	}
	
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configureAndWatch("config/log4j.properties", 5000L);
		log.info("\r\n\t      Migration-tool client instance start, [version 1.0-SNAPSHOT] "
				+ "\r\n\t\t host=" + Config.getSetting("host") + " port=" + Config.getSetting("port") 
				+ " ability=" + Config.getSetting("ability") + " thread_num=" + Config.getSetting("thread_num")
				+ "\r\n\t\t\t Copyright (C) 2015 JJF");
		//
		PoolFactory poolFactory = NettyInstance.getInstance();
		Client conn = poolFactory.getConnection();
		String[] bodies = (String[]) conn.invokeSync(Commands.REG_DEVICE + "|" + InetInfo.DEVICE_NAME + "|" + Thread.currentThread().getName() + "|" + Config.getSetting("ability"));
		System.out.println(bodies[0] + "  " + bodies[1] + "  " + bodies[2]);
		
		String[] ids = (String[]) conn.invokeSync(Commands.GET_SEGEMENT + "|" + bodies[1] + "|" + InetInfo.DEVICE_NAME + "|" + Thread.currentThread().getName());
		System.out.println(ids[0] + "  " + ids[1] + "  " + ids[2]);
		
		String[] status = (String[]) conn.invokeSync(Commands.UPDATE_STATUS + "|" + bodies[1] + "|" + ids[1] + "|" + ids[2] + "|" + Commands.STATUS_SUCC);
		System.out.println(status[0] + "  " + status[1]);
		
		String[] logout = (String[]) conn.invokeSync(Commands.LOGOUT_DEVICE + "|" + InetInfo.DEVICE_NAME + "|" + Thread.currentThread().getName());
		System.out.println(logout[0] + "  " + logout[1]);
		poolFactory.releaseConnection(conn);
	}
}
