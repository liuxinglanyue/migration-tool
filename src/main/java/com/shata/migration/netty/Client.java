package com.shata.migration.netty;

public interface Client {

	public Object invokeSync(String message) throws Exception;

	public void putResponse(String response) throws Exception;

	public String getServerIP();

	public int getServerPort();

	public int getConnectTimeout();
	
	public void close();

	public boolean validate();

}
