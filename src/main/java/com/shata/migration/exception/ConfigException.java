package com.shata.migration.exception;

public class ConfigException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6173681415915912214L;

	public ConfigException() {
	}

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(Throwable cause) {
		super(cause);
	}

	public ConfigException(String message, Throwable cause) {
		super(message, cause);
	}
}
