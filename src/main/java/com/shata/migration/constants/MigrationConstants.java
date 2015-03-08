package com.shata.migration.constants;

import org.apache.commons.lang.StringUtils;

import com.shata.migration.utils.Config;

public class MigrationConstants {
	
	public final static String EMPTY_COLUMN = "," + Config.getSetting("empty_column") + ",";
	
	public static boolean isEmpty(String column, String value) {
		if(-1 == EMPTY_COLUMN.indexOf("," + column + ",")) {
			return false;
		}
		if(StringUtils.isBlank(value)) {
			return true;
		}
		return false;
	}
}
