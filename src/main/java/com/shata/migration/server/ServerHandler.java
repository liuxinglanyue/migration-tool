package com.shata.migration.server;

import com.shata.migration.constants.Commands;

public class ServerHandler {

	public static String handleRequest(Object message) {
		if(!(message instanceof String)) {
			return Commands.ERROR;
		}
		String request = (String) message;
		String response = Commands.ERROR;
		
		if(request.startsWith(Commands.REG_DEVICE)) {
			response = reg_device(request.substring(11));
		} else if (request.startsWith(Commands.GET_SEGMENT)) {
			
		} else if (request.startsWith(Commands.UPDATE_STATUS)) {
			
		}
		return response;
	}
	
	public static String reg_device(String request) {
		
		return null;
	}
	
}
