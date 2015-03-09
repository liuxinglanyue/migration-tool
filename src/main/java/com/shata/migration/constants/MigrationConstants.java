package com.shata.migration.constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.exception.MigrationException;
import com.shata.migration.utils.Config;

public class MigrationConstants {
	private final static Logger log = LoggerFactory.getLogger(MigrationConstants.class);
	
	public final static String EMPTY_COLUMN = "," + Config.getSetting("empty_column") + ",";
	
	public final static long migration_min_time;
	public final static long migration_max_time;
	
	static {
		String migration_end_span = Config.getSetting("migration_end_span");
		String end_time = Config.getSetting("migration_end_time");

		long span = 0;
		try {
			span = Long.parseLong(migration_end_span);
		} catch (NumberFormatException e) {
			throw new MigrationException("配置错误！migration_end_span=" + migration_end_span, e);
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			long time = sdf.parse(end_time).getTime();
			migration_min_time = time - span * 1000 * 60;
			migration_max_time = time + span * 1000 * 60;
		} catch (ParseException e) {
			throw new MigrationException("配置错误！migration_end_time=" + end_time, e);
		}
	}
	
	public static boolean isEmpty(String column, String value) {
		if(-1 == EMPTY_COLUMN.indexOf("," + column + ",")) {
			return false;
		}
		if(StringUtils.isBlank(value)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 1  大于给定范围
	 * 0  等于给定范围
	 * -1 小于给定范围
	 * @param time
	 * @return
	 */
	public static int compare(String date) {
		int flag = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			long time = sdf.parse(date).getTime();
			if(time < migration_min_time) {
				flag = -1;
			} else if(time > migration_max_time) {
				flag = 1;
			}
		} catch (ParseException e) {
			log.error("日期错误！默认返回0，date=" + date, e);
		}
		return flag;
	}
	
	public static void main(String[] args) {
		System.out.println(migration_min_time);
		System.out.println(migration_max_time);
	}
}
