package com.shata.migration.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.entity.TableEntity;
import com.shata.migration.exception.ConfigException;
import com.shata.migration.utils.Config;

public class TableConstants {
	private final static Logger log = LoggerFactory.getLogger(TableConstants.class);

	public final static Map<String, TableEntity> tables = new ConcurrentHashMap<String, TableEntity>();

	public static void init() throws Exception {
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
				table.setCurrent_id(1);
				tables.put(tables_from_arr[i], table);
			} catch(Exception e) {
				log.error("数据库表的配置文件错误！", e);
				throw e;
			}
		}
	}
}
