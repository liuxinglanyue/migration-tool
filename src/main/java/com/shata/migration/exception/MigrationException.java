package com.shata.migration.exception;

public class MigrationException extends RuntimeException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2456950208380584786L;

	public MigrationException() {
	}

	public MigrationException(String message) {
		super(message);
	}

	public MigrationException(Throwable cause) {
		super(cause);
	}

	public MigrationException(String message, Throwable cause) {
		super(message, cause);
	}
}
