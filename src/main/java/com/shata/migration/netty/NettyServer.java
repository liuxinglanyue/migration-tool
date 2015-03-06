package com.shata.migration.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.utils.NamedThreadFactory;

public class NettyServer {
	private final static Logger log = LoggerFactory.getLogger(NettyServer.class);

	private ServerBootstrap bootstrap = null;

	private AtomicBoolean startFlag = new AtomicBoolean(false);

	public NettyServer() {
		ThreadFactory serverBossTF = new NamedThreadFactory("NETTYSERVER-BOSS-");
		ThreadFactory serverWorkerTF = new NamedThreadFactory("NETTYSERVER-WORKER-");
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(serverBossTF),
				Executors.newCachedThreadPool(serverWorkerTF)));
		bootstrap.setOption("tcpNoDelay", Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.nodelay", "true")));
		bootstrap.setOption("reuseAddress", Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.reuseaddress", "true")));
	}

	public void start(int listenPort, final ExecutorService threadPool, final long timeout) throws Exception {
		if (!startFlag.compareAndSet(false, true)) {
			return;
		}
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = new DefaultChannelPipeline();
				pipeline.addLast("decoder", new StringDecoder());
				pipeline.addLast("encoder", new StringEncoder());
				pipeline.addLast("handler", new NettyServerHandler(threadPool, timeout));
				return pipeline;
			}
		});
		bootstrap.bind(new InetSocketAddress(listenPort));
		log.warn("Server started,listen at: " + listenPort);
	}

	public void stop() throws Exception {
		log.warn("Server stop!");
		bootstrap.releaseExternalResources();
		startFlag.set(false);
	}
}
