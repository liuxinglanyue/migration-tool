package com.shata.migration.netty;

public interface ClientFactory {

	public Client createClient(String targetIP, int targetPort, int connectTimeout) throws Exception;
	
	public void removeClient(Client client);
}
