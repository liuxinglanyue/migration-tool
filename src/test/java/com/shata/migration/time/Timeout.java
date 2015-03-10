package com.shata.migration.time;

public class Timeout {

	public static void main(String[] args) throws InterruptedException {
		String id = "0";
		Long.parseLong(id);
		long start = System.currentTimeMillis();
		//Thread.sleep(10000);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
}
