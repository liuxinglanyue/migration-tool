package com.shata.migration.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.constants.TableConstants;
import com.shata.migration.netty.NettyServer;
import com.shata.migration.utils.Config;
import com.shata.migration.utils.NamedThreadFactory;

public class StartServer {
	private final static Logger log = LoggerFactory.getLogger(StartServer.class);
	
	static {
		PropertyConfigurator.configure("config/log4j.properties");
	}
	
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configureAndWatch("config/log4j.properties", 5000L);
		log.info("\r\n\t      Migration-tool instance start, port:" + Config.getInt("port") + " [version 1.0-SNAPSHOT] \r\n\t\t\t Copyright (C) 2015 JJF");
		//insert db
		TableConstants.init();
		
		final NettyServer nettyServer = new NettyServer();
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					nettyServer.stop();
					log.info("shutdown server");
				} catch (Exception e) {
					log.error("nettyServer stop error!", e);
				}
			}
		}));
		
		ThreadFactory tf = new NamedThreadFactory("BUSINESSTHREAEFACTORY");
		ExecutorService threadPool = new ThreadPoolExecutor(20, 100, 30, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), tf);
		nettyServer.start(Config.getInt("port"), threadPool, Config.getLong("timeout"));
	}

}
