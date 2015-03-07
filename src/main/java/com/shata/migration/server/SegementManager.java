package com.shata.migration.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.connPool.SerConnInstance;
import com.shata.migration.constants.Commands;
import com.shata.migration.jdbc.JdbcManager;
import com.shata.migration.utils.Config;

public class SegementManager {
	private final static Logger log = LoggerFactory.getLogger(SegementManager.class);
	
	//迁移id段的超时时间（单位min）
	public final static int SEGEMENT_TIMEOUT = Config.getInt("segment_timeout");
	
	public static String get_segment(String request) {
		String[] bodies = ServerHandler.splitCommand(request, 2);
		if(null == bodies) {
			log.error("请求命令" + Commands.UPDATE_STATUS + "的参数错误！");
			return "请求命令" + Commands.UPDATE_STATUS + "的参数错误！";
		}
		//
		return Commands.return_response(bodies[1], Commands.SEGMENT_FAIL);
	}
	
	public static String update_status(String request) {
		String[] bodies = ServerHandler.splitCommand(request, 5);
		if(null == bodies) {
			log.error("请求命令" + Commands.UPDATE_STATUS + "的参数错误！");
			return "请求命令" + Commands.UPDATE_STATUS + "的参数错误！";
		}
		String status = bodies[3];
		if(Commands.STATUS_SUCC.equals(status)) {
			String sql = "delete from migration_id_segment where tables='"
					+ bodies[0] + "' and min=" + bodies[1] + " and max=" + bodies[2];
			if(JdbcManager.delete(SerConnInstance.getInstance(), sql)) {
				return Commands.return_response(bodies[4], Commands.SUCC);
			}
		} else if(Commands.STATUS_FAIL.equals(status)) {
			String sql = "update migration_id_segment set status=" + status + " where tables='" 
					+ bodies[0] + "' and min=" + bodies[1] + " and max=" + bodies[2];
			if(JdbcManager.update(SerConnInstance.getInstance(), sql)) {
				return Commands.return_response(bodies[4], Commands.SUCC);
			}
		}
		
		return Commands.return_response(bodies[4], Commands.ERROR);
	}
}
