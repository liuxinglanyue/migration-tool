package com.shata.migration.netty;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractClient implements Client {
	private final static Logger log = LoggerFactory.getLogger(AbstractClient.class);

	private static AtomicInteger incId = new AtomicInteger(0);

	private final static boolean isDebugEnabled = log.isDebugEnabled();

	protected static ConcurrentHashMap<Integer, ArrayBlockingQueue<String[]>> responses = new ConcurrentHashMap<Integer, ArrayBlockingQueue<String[]>>();

	public Object invokeSync(String message) throws Exception {
		if (null == message) {
			throw new Exception("参数错误！" + message);
		}
		int requestId = incId.incrementAndGet();
		return invokeSyncIntern(requestId, requestId + "|" + message);
	}

	private Object invokeSyncIntern(int requestId, String message) throws Exception {
		long beginTime = System.currentTimeMillis();
		ArrayBlockingQueue<String[]> responseQueue = new ArrayBlockingQueue<String[]>(1);
		responses.put(requestId, responseQueue);
		try {
			if (isDebugEnabled) {
				log.debug("client ready to send message,request id: " + requestId);
			}
			sendRequest(requestId, message, getConnectTimeout());
			if (isDebugEnabled) {
				log.debug("client write message to send buffer,wait for response,request id: " + requestId);
			}
		} catch (Exception e) {
			responses.remove(requestId);
			responseQueue = null;
			log.error("send request to os sendbuffer error", e);
			throw e;
		}
		Object result = null;
		try {
			result = responseQueue.poll(getConnectTimeout() - (System.currentTimeMillis() - beginTime), TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			responses.remove(requestId);
			log.error("Get response error", e);
			throw new Exception("Get response error", e);
		}
		responses.remove(requestId);

		if (result == null) {
			String errorMsg = "receive response timeout(" + getConnectTimeout() + " ms),server is: " + getServerIP() + ":" + getServerPort()
					+ " request id is:" + requestId;
			throw new Exception(errorMsg);
		}

		return result;
	}

	/**
	 * receive response
	 */
	public void putResponse(String message) throws Exception {
		String[] request = StringUtils.split(message, "|");
		if (request.length < 2) {
			throw new Exception("参数错误！切分的长度必须大于1" + message);
		}
		int requestId = Integer.parseInt(request[0]);
		if (!responses.containsKey(requestId)) {
			log.warn("give up the response,request id is:" + requestId + ",maybe because timeout!");
			return;
		}
		try {
			ArrayBlockingQueue<String[]> queue = responses.get(requestId);
			if (queue != null) {
				queue.put(request);
			} else {
				log.warn("give up the response,request id is:" + requestId + ",because queue is null");
			}
		} catch (InterruptedException e) {
			log.error("put response error,request id is:" + requestId, e);
		}
	}

	public abstract void sendRequest(int requestId, String message, int timeout) throws Exception;
}
