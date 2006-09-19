package org.mifos.framework.exceptions;

public class LoginException extends ApplicationException {

	public LoginException() {
		super();
	}

	public LoginException(Object[] values) {
		super(values);
	}

	public LoginException(Throwable cause) {
		super(cause);
	}

	public LoginException(String key) {
		super(key);
	}

	public LoginException(String key, Object[] values) {
		super(key, values);
	}

	public LoginException(String key, Throwable cause) {
		super(key, cause);
	}

	public LoginException(String key, Throwable cause, Object[] values) {
		super(key, cause, values);
	}

}
