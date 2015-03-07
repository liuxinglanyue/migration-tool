package com.shata.migration.constants;

public class Commands {
	
	//注册设备 reg_device|devicename|threadname|ability|id --> id|tables
	public final static String REG_DEVICE = "reg_device"; // 10
	
	//注销设备 logout_device|devicename|threadname|id --> id|succ
	public final static String LOGOUT_DEVICE = "logout_device"; //13
	
	//获取id段 get_segment|tables|id --> id|min|max
	public final static String GET_SEGMENT = "get_segment"; // 11
	
	//更新状态 update_status|tables|min|max|status|id --> id|succ
	public final static String UPDATE_STATUS = "update_status"; //13
	
	//错误
	public final static String ERROR = "error";
	
	//正确
	public final static String SUCC = "succ";
	
	//万表之王
	public final static String TABLE_TABLE = "table_table";
	
	
	public static String return_response(String id, String body) {
		return id + "|" + body;
	}
}
