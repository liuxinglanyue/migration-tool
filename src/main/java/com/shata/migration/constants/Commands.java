package com.shata.migration.constants;

public class Commands {
	
	//注册设备 reg_device|devicename --> tables
	public final static String REG_DEVICE = "reg_device"; // 10
	
	//获取id段 get_segment|tables --> min|max
	public final static String GET_SEGMENT = "get_segment"; // 11
	
	//更新状态 update_status|tables|min|max|status
	public final static String UPDATE_STATUS = "update_status"; //13
	
	//错误
	public final static String ERROR = "error";
}
