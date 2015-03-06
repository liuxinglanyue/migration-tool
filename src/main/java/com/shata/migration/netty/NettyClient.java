package com.shata.migration.netty;

import java.net.InetSocketAddress;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClient extends AbstractClient {
	private final static Logger log = LoggerFactory.getLogger(NettyClient.class);

	private ChannelFuture cf;

	private int connectTimeout;

	public NettyClient(ChannelFuture cf, int connectTimeout) {
		this.cf = cf;
		this.connectTimeout = connectTimeout;
	}

	@Override
	public void sendRequest(final int requestId, final String message, final int timeout) throws Exception {
		final long beginTime = System.currentTimeMillis();
		final Client self = this;
		ChannelFuture writeFuture = cf.getChannel().write(message);
		writeFuture.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					return;
				}
				String errorMsg = "";
				// write timeout
				if (System.currentTimeMillis() - beginTime >= timeout) {
					errorMsg = "write to send buffer consume too long time(" + (System.currentTimeMillis() - beginTime) + "),request id is:"
							+ requestId;
				}
				if (future.isCancelled()) {
					errorMsg = "Send request to " + cf.getChannel().toString() + " cancelled by user,request id is:" + requestId;
				}
				if (!future.isSuccess()) {
					if (cf.getChannel().isConnected()) {
						// maybe some exception,so close the channel
						cf.getChannel().close();
					} else {
						NettyClientFactory.getInstance().removeClient(self);
					}
					errorMsg = "Send request to " + cf.getChannel().toString() + " error" + future.getCause();
				}
				log.error(errorMsg);
				self.putResponse(requestId + "|error|" + errorMsg);
			}
		});
	}
	
	@Override
	public void close() {
		if (cf.getChannel().isConnected()) {
			cf.getChannel().close();
		}
	}
	
	@Override
	public boolean validate() {
		return cf.getChannel().isConnected();
	}

	@Override
	public String getServerIP() {
		return ((InetSocketAddress) cf.getChannel().getRemoteAddress()).getHostName();
	}

	@Override
	public int getServerPort() {
		return ((InetSocketAddress) cf.getChannel().getRemoteAddress()).getPort();
	}

	@Override
	public int getConnectTimeout() {
		return connectTimeout;
	}

}
