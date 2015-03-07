package com.shata.migration.constants;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.connPool.SerConnInstance;
import com.shata.migration.entity.TableEntity;
import com.shata.migration.exception.ConfigException;
import com.shata.migration.jdbc.JdbcManager;
import com.shata.migration.utils.Config;
import com.shata.migration.utils.DateUtils;

public class TableConstants {
	private final static Logger log = LoggerFactory.getLogger(TableConstants.class);

	public final static Map<String, TableEntity> tables = new ConcurrentHashMap<String, TableEntity>();

	public static void init() throws Exception {
		init_config();
		init_db();
	}
	
	public static void init_db() {
		List<Map<String, String>> results = JdbcManager.queryMap(SerConnInstance.getInstance(), "select * from migration_id_current");
		if(null != results && results.size() > 0) {
			for(Map<String, String> map : results) {
				if(null == map) {
					continue;
				}
				String table_from = map.get("tables");
				TableEntity table = tables.get(table_from);
				if(null == table) {
					log.error("数据库记录和配置文件不一致！表名:" + table_from);
					continue;
				}
				long current_id = Long.parseLong(map.get("current"));
				int ability = Integer.parseInt(map.get("ability"));
				//说明 当前表迁移完成
				if(current_id == -1) {
					tables.remove(table_from);
					log.info("数据库表 " + table_from + " 已经迁移完成。");
					continue;
				}
				//异常数据处理
				if(current_id < 1 || ability < 0) {
					log.error("数据库记录异常，current_id=" + current_id + " , ability=" + ability);
					current_id = 1;
					ability = 0;
				}
				table.setCurrent_id(current_id);
				table.setAbility(ability);
				//标记下，数据库中存在
				table.setMark(1);
				log.info("表" + table_from + " 在数据库中已经存在！");
			}
		}
		//将配置文件中存在，数据库中不存在的数据 存储
		for(TableEntity table : tables.values()) {
			if(table.getMark() == 1) {
				continue;
			}
			String sql = "insert into migration_id_current(tables,current,ability,create_time) "
					+ "values ('" + table.getTable_from() + "'," + table.getCurrent_id() + "," 
					+ table.getAbility() + ",'" + DateUtils.currentDate() + "');";
			if(JdbcManager.update(SerConnInstance.getInstance(), sql)) {
				log.info("表 " + table.getTable_from() + " 成功插入！");
			} else {
				log.error("表 " + table.getTable_from() + " 插入失败，请检查！");
			}
		}
	}
	
	public static void init_config() throws Exception {
		String tables_from = Config.getSetting("tables_from");
		String tables_to = Config.getSetting("tables_to");

		String[] tables_from_arr = StringUtils.split(tables_from, "|");
		String[] tables_to_arr = StringUtils.split(tables_to, "|");
		if (null == tables_from_arr || null == tables_to_arr || tables_from_arr.length == 0 
				|| tables_from_arr.length != tables_to_arr.length) {
			throw new ConfigException("config tables_from or tables_to error!");
		}
		
		for(int i=0; i<tables_from_arr.length; i++) {
			TableEntity table = new TableEntity(tables_from_arr[i], tables_to_arr[i]);
			try {
				table.setColumn_from(Config.getSetting(tables_from_arr[i] + "_from"));
				table.setColumn_to(Config.getSetting(tables_from_arr[i] + "_to"));
				table.setMin_id(Config.getLong(tables_from_arr[i] + "_minId"));
				table.setMax_id(Config.getLong(tables_from_arr[i] + "_maxId"));
				table.setAbility(0);
				table.setCurrent_id(1);
				tables.put(tables_from_arr[i], table);
			} catch(Exception e) {
				log.error("数据库表的配置文件错误！", e);
				throw e;
			}
		}
	}
}
