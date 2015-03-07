package com.shata.migration.entity;

import java.util.Date;

public class DeviceEntity {

	/**
	 * 设备名称，pid@hostname@ip
	 */
	private String devicename;
	
	/**
	 * 线程名称
	 */
	private String threadname;
	
	/**
	 * 数据库表名
	 */
	private String tables;
	
	/**
	 * 能力值
	 */
	private int ability;
	
	/**
	 * 创建时间
	 */
	private Date create_time;
	
	/**
	 * 更新时间，用于判断 组件是否超时
	 */
	private long update_time;
	
	public DeviceEntity() {
		
	}
	
	public DeviceEntity(String devicename, String threadname, String tables, int ability, Date create_time, long update_time) {
		this.devicename = devicename;
		this.threadname = threadname;
		this.tables = tables;
		this.ability = ability;
		this.create_time = create_time;
		this.update_time = update_time;
	}

	public String getDevicename() {
		return devicename;
	}

	public void setDevicename(String devicename) {
		this.devicename = devicename;
	}

	public String getThreadname() {
		return threadname;
	}

	public void setThreadname(String threadname) {
		this.threadname = threadname;
	}

	public String getTables() {
		return tables;
	}

	public void setTables(String tables) {
		this.tables = tables;
	}

	public int getAbility() {
		return ability;
	}

	public void setAbility(int ability) {
		this.ability = ability;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public long getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(long update_time) {
		this.update_time = update_time;
	}
	
	public String getKey() {
		return devicename + threadname;
	}
	
	public String toString() {
		return "设备信息:" + devicename + ",线程信息:" + threadname + ",能力值:" + ability + ",表:" + tables;
	}
}
