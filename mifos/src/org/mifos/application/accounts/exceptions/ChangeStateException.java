package org.mifos.application.accounts.exceptions;

import org.mifos.framework.exceptions.ApplicationException;

public class ChangeStateException extends ApplicationException {

	public ChangeStateException() {
		super();
	}

	public ChangeStateException(Object[] values) {
		super(values);
	}

	public ChangeStateException(Throwable cause) {
		super(cause);
	}

	public ChangeStateException(String key) {
		super(key);
	}

	public ChangeStateException(String key, Object[] values) {
		super(key, values);
	}

	public ChangeStateException(String key, Throwable cause) {
		super(key, cause);
	}

	public ChangeStateException(String key, Throwable cause, Object[] values) {
		super(key, cause, values);
	}

}
