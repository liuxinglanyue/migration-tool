package com.shata.migration.entity;

@Deprecated
public class SegementFailEntity {

	private String table;
	
	private long min_id;
	
	private long max_id;
	
	public SegementFailEntity() {
		
	}
	
	public SegementFailEntity(String table) {
		this.table = table;
	}
	
	public SegementFailEntity(String table, long min_id, long max_id) {
		this.table = table;
		this.min_id = min_id;
		this.max_id = max_id;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public long getMin_id() {
		return min_id;
	}

	public void setMin_id(long min_id) {
		this.min_id = min_id;
	}

	public long getMax_id() {
		return max_id;
	}

	public void setMax_id(long max_id) {
		this.max_id = max_id;
	}
	
	
}
