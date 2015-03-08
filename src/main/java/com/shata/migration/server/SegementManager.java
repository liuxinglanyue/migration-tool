package com.shata.migration.server;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.shata.migration.connPool.SerConnInstance;
import com.shata.migration.constants.Commands;
import com.shata.migration.constants.DeviceConstants;
import com.shata.migration.constants.TableConstants;
import com.shata.migration.entity.DeviceEntity;
import com.shata.migration.entity.TableEntity;
import com.shata.migration.jdbc.JdbcManager;
import com.shata.migration.utils.Config;
import com.shata.migration.utils.DateUtils;

public class SegementManager {
	private final static Logger log = LoggerFactory.getLogger(SegementManager.class);
	
	//迁移id段的超时时间（单位min）
	public final static int SEGEMENT_TIMEOUT = Config.getInt("segment_timeout");
	//判读id段是否超时，线程的间隔时间（单位min）
	public final static int SEGEMENT_INTERVAL = 1;
	//id段 跨度 默认100
	public final static int SEGEMENT_ID = Config.getInt("segment");
	//用于string的synchronized
	private final static Interner<String> pool = Interners.newWeakInterner();
	
	public static String get_segment(String request) {
		String[] bodies = ServerHandler.splitCommand(request, 4);
		if(null == bodies) {
			log.error("请求命令" + Commands.GET_SEGEMENT + "的参数错误！");
			return "请求命令" + Commands.GET_SEGEMENT + "的参数错误！";
		}
		//每次请求id段，都对连接设备的时间进行更新，便于判断是否超时
		DeviceEntity de = DeviceConstants.devices.get(bodies[1] + bodies[2]);
		if(null != de) {
			de.setUpdate_time(DateUtils.currentLong());
		}
		
		String table = bodies[0];
		long min = 0;
		long max = 0;
		synchronized (pool.intern(table)) {
			TableEntity te = TableConstants.tables.get(table);
			if(null != te && te.getCurrent_id() <= te.getMax_id()) {
				min = te.getCurrent_id();
				max = min + SEGEMENT_ID - 1;
				if(max > te.getMax_id()) {
					max = te.getMax_id();
				}
				String sql = "insert into migration_id_segment(tables,min,max,status,create_time) values ('" 
						+ table + "'," + min + "," + max + "," + Commands.STATUS_PREPARE + ",'" + DateUtils.currentDateStr() + "');";
				String update_current_id = "update migration_id_current set current=" + (max + 1) + " where tables='" + table + "'";
				if(JdbcManager.update(SerConnInstance.getInstance(), sql)) {
					if(JdbcManager.update(SerConnInstance.getInstance(), update_current_id)) {
						te.setCurrent_id(max + 1);
					} else {
						//删除上面插入的migration_id_segment
						//这里删除失败了也没事儿， 因为有超时设置在，超时将置为失败
						update_status_db(table, String.valueOf(min), String.valueOf(max), Commands.STATUS_SUCC);
					}
				} else {
					//获取失败
					min = -1;
					max = -1;
				}
			}
		}
		if(0 != min && 0 != max) {
			return Commands.return_response(bodies[3], min + "|" + max);
		}
		//current id全部迁移完成，接下来获取迁移失败的
		String sql = "select min,max from migration_id_segment where status=" + Commands.STATUS_FAIL 
				+ " and tables='" + table + "';";
		synchronized (pool.intern(table)) {
			Map<String, String> map = JdbcManager.queryOneMap(SerConnInstance.getInstance(), sql);
			if(null != map) {
				String min_id = map.get("min");
				String max_id = map.get("max");
				if(update_status_db(table, min_id, max_id, Commands.STATUS_PREPARE)) {
					min = Long.parseLong(min_id);
					max = Long.parseLong(max_id);
				} else {
					//获取失败
					min = -1;
					max = -1;
				}
			}
		}
		if(min == 0 && max == 0) {
			return Commands.return_response(bodies[3], Commands.SEGEMENT_SUCC);
		} else {
			return Commands.return_response(bodies[3], min + "|" + max + "|fail");
		}
	}
	
	public static String update_status(String request) {
		String[] bodies = ServerHandler.splitCommand(request, 5);
		if(null == bodies) {
			log.error("请求命令" + Commands.UPDATE_STATUS + "的参数错误！");
			return "请求命令" + Commands.UPDATE_STATUS + "的参数错误！";
		}
		String status = bodies[3];
		if(update_status_db(bodies[0], bodies[1], bodies[2], status)) {
			return Commands.return_response(bodies[4], Commands.SUCC);
		}
		
		return Commands.return_response(bodies[4], Commands.ERROR);
	}
	
	private static boolean update_status_db(String table, String min, String max, String status) {
		if(Commands.STATUS_SUCC.equals(status)) {
			String sql = "delete from migration_id_segment where tables='"
					+ table + "' and min=" + min + " and max=" + max;
			if(JdbcManager.delete(SerConnInstance.getInstance(), sql)) {
				return true;
			}
		} else if(Commands.STATUS_FAIL.equals(status) || Commands.STATUS_PREPARE.equals(status)) {
			String sql = "update migration_id_segment set status=" + status + " where tables='" 
					+ table + "' and min=" + min + " and max=" + max;
			if(JdbcManager.update(SerConnInstance.getInstance(), sql)) {
				return true;
			}
		}
		return false;
	}
}
