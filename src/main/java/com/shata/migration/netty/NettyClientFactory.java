package com.shata.migration.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.utils.NamedThreadFactory;

public class NettyClientFactory extends AbstractClientFactory {
	private final static Logger log = LoggerFactory.getLogger(NettyClientFactory.class);

	private static AbstractClientFactory _self = new NettyClientFactory();

	private final static ThreadFactory bossThreadFactory = new NamedThreadFactory("NETTYCLIENT-BOSS-");

	private final static ThreadFactory workerThreadFactory = new NamedThreadFactory("NETTYCLIENT-WORKER-");

	private static NioClientSocketChannelFactory nioClient = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(bossThreadFactory),
			Executors.newCachedThreadPool(workerThreadFactory));

	private NettyClientFactory() {
	}

	public static AbstractClientFactory getInstance() {
		return _self;
	}

	public Client createClient(String targetIP, int targetPort, int connectTimeout) throws Exception {
		ClientBootstrap bootstrap = new ClientBootstrap(nioClient);
		bootstrap.setOption("tcpNoDelay", Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.nodelay", "true")));
		bootstrap.setOption("reuseAddress", Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.reuseaddress", "true")));
		if (connectTimeout < 1000) {
			bootstrap.setOption("connectTimeoutMillis", 1000);
		} else {
			bootstrap.setOption("connectTimeoutMillis", connectTimeout);
		}
		NettyClientHandler handler = new NettyClientHandler(this);
		bootstrap.setPipelineFactory(new NettyClientPipelineFactory(handler));
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(targetIP, targetPort));
		future.awaitUninterruptibly(connectTimeout);
		if (!future.isDone()) {
			log.error("Create connection to " + targetIP + ":" + targetPort + " timeout!");
			throw new Exception("Create connection to " + targetIP + ":" + targetPort + " timeout!");
		}
		if (future.isCancelled()) {
			log.error("Create connection to " + targetIP + ":" + targetPort + " cancelled by user!");
			throw new Exception("Create connection to " + targetIP + ":" + targetPort + " cancelled by user!");
		}
		if (!future.isSuccess()) {
			log.error("Create connection to " + targetIP + ":" + targetPort + " error", future.getCause());
			throw new Exception("Create connection to " + targetIP + ":" + targetPort + " error", future.getCause());
		}
		NettyClient client = new NettyClient(future, connectTimeout);
		handler.setClient(client);
		return client;
	}

	public void removeClient(Client client) {
		//no to do
	}
}
