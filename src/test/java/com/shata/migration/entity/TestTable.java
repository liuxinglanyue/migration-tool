package com.shata.migration.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TestTable {

	public static void main(String[] args) {
		Map<String, TableEntity> map = new HashMap<String, TableEntity>();
		map.put("table1", new TableEntity("table1", "table1", 3));
		map.put("table2", new TableEntity("table2", "table2", 5));
		map.put("table3", new TableEntity("table3", "table3", 1));
		map.put("table4", new TableEntity("table4", "table4", 8));
		map.put("table5", new TableEntity("table5", "table5", 2));
		
		Collection<TableEntity> tables = map.values();
		TableEntity[] ts = tables.toArray(new TableEntity[tables.size()]);
		Arrays.sort(ts);
		
		for(TableEntity t : ts) {
			System.out.println(t.getTable_from() + " " + t.getAbility());
		}
	}
}
