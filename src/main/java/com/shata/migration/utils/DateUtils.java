package com.shata.migration.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

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
	
	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -2);
		long old = cal.getTimeInMillis();
		
		System.out.println(isTimeout(old, 1));
		System.out.println(isTimeout(old, 2));
		System.out.println(isTimeout(old, 3));
	}
}
