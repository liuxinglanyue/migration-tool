package com.shata.migration.server;

import org.apache.commons.lang.StringUtils;

import com.shata.migration.constants.Commands;
import com.shata.migration.constants.DeviceConstants;

public class ServerHandler {

	public static String handleRequest(Object message) {
		if(!(message instanceof String)) {
			return Commands.ERROR;
		}
		String request = (String) message;
		String response = Commands.ERROR;
		
		if(request.startsWith(Commands.REG_DEVICE)) {
			response = DeviceConstants.reg_device(request.substring(11));
		} else if(request.startsWith(Commands.LOGOUT_DEVICE)) {
			response = DeviceConstants.logout_device(request.substring(14));
		} else if (request.startsWith(Commands.GET_SEGMENT)) {
			
		} else if (request.startsWith(Commands.UPDATE_STATUS)) {
			response = SegementManager.update_status(request.substring(14));
		}
		return response;
	}
	
	public static String[] splitCommand(String request, int len) {
		String[] body = StringUtils.split(request, "|");
		if(null == body || body.length != len) {
			return null;
		}
		return body;
	}
}
