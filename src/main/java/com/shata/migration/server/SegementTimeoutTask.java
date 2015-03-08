package com.shata.migration.server;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.connPool.SerConnInstance;
import com.shata.migration.constants.Commands;
import com.shata.migration.jdbc.JdbcManager;
import com.shata.migration.utils.DateUtils;

public class SegementTimeoutTask implements Runnable {
	private final static Logger log = LoggerFactory.getLogger(SegementTimeoutTask.class);
	
	private final static String select_sql = "select id,tables,min,max,create_time from migration_id_segment where status=" + Commands.STATUS_PREPARE;

	private final static String update_sql = "update migration_id_segment set status=" + Commands.STATUS_FAIL 
			+ " where status=" + Commands.STATUS_PREPARE + " and id=";
	
	@Override
	public void run() {
		List<Map<String, String>> timeouts = JdbcManager.queryMap(SerConnInstance.getInstance(), select_sql);
		if(null == timeouts || timeouts.size() == 0) {
			return;
		}
		for(Map<String, String> segement : timeouts) {
			if(null == segement) {
				continue;
			}
			String time = segement.get("create_time");
			if(DateUtils.isTimeout(time, SegementManager.SEGEMENT_TIMEOUT)) {
				String id = segement.get("id");
				String info = "table=" + segement.get("tables") + ",min=" + segement.get("min") + ",max=" + segement.get("max") + ",create_time=" + time;
				log.info("id段超时，状态修改为失败！" + info);
				if(JdbcManager.update(SerConnInstance.getInstance(), update_sql + id)) {
					log.info("成功！id段超时，状态修改为失败！" + info);
				} else {
					log.error("失败！id段超时，状态修改为失败！" + info);
				}
			}
		}
	}

}
