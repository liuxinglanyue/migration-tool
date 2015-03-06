package com.shata.migration.exception;

public class ConnectionException extends RuntimeException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7845348273527518227L;

	public ConnectionException() {
	}

	public ConnectionException(String message) {
		super(message);
	}

	public ConnectionException(Throwable cause) {
		super(cause);
	}

	public ConnectionException(String message, Throwable cause) {
		super(message, cause);
	}
}
