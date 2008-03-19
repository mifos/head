package org.mifos.framework.exceptions;

public class ConfigServiceInitializationException extends RuntimeException {

	public ConfigServiceInitializationException(String message) {
		super(message);
	}

	public ConfigServiceInitializationException(String message, Throwable e) {
		super(message,e);
	}

}
