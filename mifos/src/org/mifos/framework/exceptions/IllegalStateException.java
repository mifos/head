package org.mifos.framework.exceptions;

public class IllegalStateException extends SystemException {

	public IllegalStateException() {
	}

	public IllegalStateException(String key) {
		super(key);
	}

	public IllegalStateException(String key, Throwable throwable) {
		super(key, throwable);
	}

	public IllegalStateException(Throwable throwable) {
		super(throwable);
	}

}
