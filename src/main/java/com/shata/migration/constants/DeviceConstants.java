package com.shata.migration.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.entity.DeviceEntity;
import com.shata.migration.entity.TableEntity;
import com.shata.migration.server.ServerHandler;
import com.shata.migration.utils.Config;
import com.shata.migration.utils.DateUtils;

public class DeviceConstants {
	private final static Logger log = LoggerFactory.getLogger(DeviceConstants.class);
	
	//设备的能力值 超时时间（单位min）
	public final static int DEVICE_TIMEOUT = Config.getInt("device_timeout");
	
	public final static Map<String, DeviceEntity> devices = new ConcurrentHashMap<String, DeviceEntity>();
	
	/**
	 * 注册设备
	 * @param request
	 * @return
	 */
	public static String reg_device(String request) {
		String[] bodies = ServerHandler.splitCommand(request, 4);
		if(null == bodies) {
			log.error("请求命令" + Commands.REG_DEVICE + "的参数错误！");
			return "请求命令" + Commands.REG_DEVICE + "的参数错误！";
		}
		int ability = Integer.parseInt(bodies[2]);
		String table = TableConstants.nextTable();
		DeviceEntity device = new DeviceEntity(bodies[0], bodies[1], table
				, ability, DateUtils.currentDate(), DateUtils.currentLong());
		devices.put(device.getKey(), device);
		
		//将设备能力值 添加到migration_id_current表上
		TableConstants.addAbility(table, ability);
		
		log.info("请求命令" + Commands.REG_DEVICE + ",注册成功。" + device.toString());
		
		TableEntity te = TableConstants.tables.get(table);
		if(null == te) {
			return Commands.return_response(bodies[3], table + "|null|null|null");
		}
		return Commands.return_response(bodies[3], table + "|" + te.getTable_to() + "|" + te.getColumn_from() + "|" + te.getColumn_to());
	}
	
	/**
	 * 注销设备
	 * @param request
	 * @return
	 */
	public static String logout_device(String request) {
		String[] bodies = ServerHandler.splitCommand(request, 3);
		if(null == bodies) {
			log.error("请求命令" + Commands.REG_DEVICE + "的参数错误！");
			return "请求命令" + Commands.REG_DEVICE + "的参数错误！";
		}
		DeviceEntity device = devices.get(bodies[0] + bodies[1]);
		if(TableConstants.reduceAbility(device)) {
			device = null;
			return Commands.return_response(bodies[2], Commands.SUCC);
		}
		
		return Commands.return_response(bodies[2], Commands.ERROR);
	}
}
