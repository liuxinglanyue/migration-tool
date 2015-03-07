package com.shata.migration.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.utils.Config;

public class SegementManager {
	private final static Logger log = LoggerFactory.getLogger(SegementManager.class);
	
	//迁移id段的超时时间（单位min）
	public final static int SEGEMENT_TIMEOUT = Config.getInt("segment_timeout");
	
	
}
