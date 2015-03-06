package com.shata.migration.netty;

import java.io.IOException;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientHandler extends SimpleChannelUpstreamHandler {
	private final static Logger log = LoggerFactory.getLogger(NettyClientHandler.class);

	private NettyClientFactory factory;

	private NettyClient client;

	public NettyClientHandler(NettyClientFactory factory) {
		this.factory = factory;
	}

	public void setClient(NettyClient client) {
		this.client = client;
	}

	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if (e.getMessage() instanceof String) {
			client.putResponse((String) e.getMessage());
		} else {
			log.error("receive message error,only support String");
			throw new Exception("receive message error,only support String");
		}
	}

	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		if (!(e.getCause() instanceof IOException)) {
			log.error("catch some exception not IOException", e.getCause());
		}
	}

	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		log.warn("connection closed: " + ctx.getChannel().getRemoteAddress());
		factory.removeClient(client);
	}
}
