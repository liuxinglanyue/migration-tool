package com.shata.migration.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {
	private final static Logger log = LoggerFactory.getLogger(DateUtils.class);

	public static String currentDateStr() {
		Date currentDate = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(currentDate);
	}
	
	public static Date currentDate() {
		return Calendar.getInstance().getTime();
	}
	
	public static long currentLong() {
		return Calendar.getInstance().getTimeInMillis();
	}
	
	public static boolean isTimeout(long time, int min) {
		return (Calendar.getInstance().getTimeInMillis() - time) > min * 60 * 1000;
	}
	
	public static boolean isTimeout(String time, int min) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			log.error("日期解析错误，" + time, e);
		}
		if(null == date) {
			return false;
		}
		return (Calendar.getInstance().getTimeInMillis() - date.getTime()) > min * 60 * 1000;
	}
	
	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -2);
		long old = cal.getTimeInMillis();
		
		System.out.println(isTimeout(old, 1));
		System.out.println(isTimeout(old, 2));
		System.out.println(isTimeout(old, 3));
	}
}
