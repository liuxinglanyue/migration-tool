package com.shata.migration.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.utils.Config;
import com.shata.migration.utils.NamedThreadFactory;

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
		//提前加载驱动
		Class.forName("com.mysql.jdbc.Driver");
		//
		int nThreads = Integer.parseInt(Config.getSetting("thread_num"));
		ExecutorService threadPool = Executors.newFixedThreadPool(nThreads, new NamedThreadFactory("migration"));
		
		for(int i=0; i<nThreads; i++) {
			threadPool.execute(new MigrationTask());
			try {
				//暂停1s是为了 能力值处理
				Thread.sleep(1000);
			} catch (Exception e) {
				log.error("main thread sleep 1000ms error!",e);
			}
		}
	}
}
