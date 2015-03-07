package com.shata.migration.entity;

public class TableEntity implements Comparable<TableEntity> {

	private String table_from;
	
	private String table_to;
	
	private String column_from;
	
	private String column_to;
	
	private long min_id;
	
	private long max_id;
	
	private int ability;
	
	private long current_id;
	
	//标记，数据库中是否存在
	private int mark;
	
	public TableEntity() {
		
	}
	
	public TableEntity(String table_from, String table_to) {
		this.table_from = table_from;
		this.table_to = table_to;
	}
	
	public TableEntity(String table_from, String table_to, int ability) {
		this.table_from = table_from;
		this.table_to = table_to;
		this.ability = ability;
	}
	
	public String getTable_from() {
		return table_from;
	}

	public void setTable_from(String table_from) {
		this.table_from = table_from;
	}

	public String getTable_to() {
		return table_to;
	}

	public void setTable_to(String table_to) {
		this.table_to = table_to;
	}

	public String getColumn_from() {
		return column_from;
	}

	public void setColumn_from(String column_from) {
		this.column_from = column_from;
	}

	public String getColumn_to() {
		return column_to;
	}

	public void setColumn_to(String column_to) {
		this.column_to = column_to;
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

	public int getAbility() {
		return ability;
	}

	public void setAbility(int ability) {
		this.ability = ability;
	}

	public long getCurrent_id() {
		return current_id;
	}

	public void setCurrent_id(long current_id) {
		this.current_id = current_id;
	}

	@Override
	public int compareTo(TableEntity t) {
		return this.ability - t.ability;
	}
	
	public String toString() {
		return table_from + "|" + table_to + "|" + column_from + "|" 
				+ column_to + "|" + min_id + "|" + max_id + "|" + ability + "|" + current_id;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}
}
