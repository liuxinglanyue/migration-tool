package com.shata.migration.constants;

public class Commands {
	
	//注册设备 reg_device|devicename|threadname|ability|id --> id|tables  (table_table 万表之王)
	public final static String REG_DEVICE = "reg_device"; // 10
	
	//注销设备 logout_device|devicename|threadname|id --> id|succ
	public final static String LOGOUT_DEVICE = "logout_device"; //13
	
	//获取id段 get_segment|tables|id --> id|min|max ( min|max  0|0 表示tables完成迁移了  -1|-1 表示获取失败)
	public final static String GET_SEGMENT = "get_segment"; // 11
	
	//更新状态 update_status|tables|min|max|status|id --> id|succ
	public final static String UPDATE_STATUS = "update_status"; //13
	
	//错误
	public final static String ERROR = "error";
	
	//正确
	public final static String SUCC = "succ";
	
	//几种异常情况
	
	//万表之王
	public final static String TABLE_TABLE = "table_table";
	//tables完成迁移了
	public final static String SEGMENT_SUCC = "0|0";
	//获取失败
	public final static String SEGMENT_FAIL = "-1|-1";
	
	
	public static String return_response(String id, String body) {
		return id + "|" + body;
	}
	
	public final static String STATUS_PREPARE = "0";
	public final static String STATUS_FAIL = "1";
	public final static String STATUS_SUCC = "2";
	
	//暂未实现
	public final static String STATUS_RETRY = "3"; 
	public final static String STATUS_RETRY_MIN = "4";
	public final static String STATUS_RETRY_MAX = "9"; // 重试的最大次数   4-9 总共6次
}
