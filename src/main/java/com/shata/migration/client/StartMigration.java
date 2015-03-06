package com.shata.migration.client;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartMigration {
	private final static Logger log = LoggerFactory.getLogger(StartMigration.class);
	
	static {
		PropertyConfigurator.configure("config/log4j.properties");
	}
	
	public static void main(String[] args) {
		PropertyConfigurator.configureAndWatch("config/log4j.properties", 5000L);
		//StartNewQueue.newQueueInstance(Integer.parseInt(Config.getSetting("port")));
	}
}
