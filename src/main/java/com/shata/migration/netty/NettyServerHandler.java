package com.shata.migration.netty;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shata.migration.constants.Commands;
import com.shata.migration.server.ServerHandler;

public class NettyServerHandler extends SimpleChannelUpstreamHandler {

	private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

	private ExecutorService threadpool;
	
	private long timeout;

	public NettyServerHandler(ExecutorService threadpool, long timeout) {
		this.threadpool = threadpool;
		this.timeout = timeout;
	}

	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		if (!(e.getCause() instanceof IOException)) {
			log.error("catch some exception not IOException", e.getCause());
		}
	}

	public void messageReceived(final ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		Object message = e.getMessage();
		if (!(message instanceof String)) {
			log.error("receive message error,only support String");
			throw new Exception("receive message error,only support String");
		}
		handleRequest(ctx, message);
	}

	private void handleRequest(final ChannelHandlerContext ctx, final Object message) {
		try {
			threadpool.execute(new HandlerRunnable(ctx, message, threadpool, timeout));
		} catch (RejectedExecutionException exception) {
			log.error("server threadpool full,threadpool maxsize is:" + ((ThreadPoolExecutor) threadpool).getMaximumPoolSize());
			sendErrorResponse(ctx, message);
		}
	}

	private void sendErrorResponse(final ChannelHandlerContext ctx, final Object message) {
		ChannelFuture wf = ctx.getChannel().write(Commands.ERROR);
		wf.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				if (!future.isSuccess()) {
					log.error("server write response error! request message is:" + message);
				}
			}
		});
	}

	class HandlerRunnable implements Runnable {

		private ChannelHandlerContext ctx;

		private Object message;

		private ExecutorService threadPool;
		
		private long timeout;

		public HandlerRunnable(ChannelHandlerContext ctx, Object message, ExecutorService threadPool, long timeout) {
			this.ctx = ctx;
			this.message = message;
			this.threadPool = threadPool;
			this.timeout = timeout;
		}

		@SuppressWarnings("rawtypes")
		public void run() {
			//暂不支持List
			if (message instanceof List) {
				List messages = (List) message;
				for (Object messageObject : messages) {
					threadPool.execute(new HandlerRunnable(ctx, messageObject, threadPool, timeout));
				}
			} else {
				long beginTime = System.currentTimeMillis();
				String response = ServerHandler.handleRequest(message);
				log.debug("request:" + (String)message + " response:" + response);
				// already timeout,so not return
				if ((System.currentTimeMillis() - beginTime) >= timeout) {
					log.warn("timeout,so give up send response to client,request message is:" + message + ",client is:" + ctx.getChannel().getRemoteAddress()
							+ ",consumetime is:" + (System.currentTimeMillis() - beginTime) + ",timeout is:" + timeout);
					return;
				}
				ChannelFuture wf = ctx.getChannel().write(response);
				wf.addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture future) throws Exception {
						if (!future.isSuccess()) {
							log.error("server write response error,request message is:" + message);
						}
					}
				});
			}
		}

	}

}