package com.shata.migration.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.entity.SegementFailEntity;

@Deprecated
public class SegementConstants {
	private final static Logger log = LoggerFactory.getLogger(SegementConstants.class);
	
	public final static Map<String, SegementFailEntity> failMap = new ConcurrentHashMap<String, SegementFailEntity>();
	
	
}
