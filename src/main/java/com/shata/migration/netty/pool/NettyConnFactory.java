package com.shata.migration.netty.pool;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.netty.Client;
import com.shata.migration.netty.NettyClientFactory;

public class NettyConnFactory implements PooledObjectFactory<Client> {
	private final static Logger log = LoggerFactory.getLogger(NettyConnFactory.class);

	private String targetIP;
	private int targetPort;
	private int connectTimeout;

	public NettyConnFactory(String targetIP, int targetPort, int connectTimeout) {
		this.targetIP = targetIP;
		this.targetPort = targetPort;
		this.connectTimeout = connectTimeout;
	}

	@Override
	public void activateObject(PooledObject<Client> p) throws Exception {

	}

	@Override
	public void destroyObject(PooledObject<Client> p) throws Exception {
		if(null != p) {
			Client client = p.getObject();
			if(null != client) {
				client.close();
			}
		}
	}

	@Override
	public PooledObject<Client> makeObject() throws Exception {
		return new DefaultPooledObject<Client>(NettyClientFactory.getInstance().createClient(targetIP, targetPort, connectTimeout));
	}

	@Override
	public void passivateObject(PooledObject<Client> p) throws Exception {

	}

	@Override
	public boolean validateObject(PooledObject<Client> p) {
		if(null != p) {
			Client client = p.getObject();
			if(null != client) {
				return client.validate();
			}
		}
		return false;
	}

}
