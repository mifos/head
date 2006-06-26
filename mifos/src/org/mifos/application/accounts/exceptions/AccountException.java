package org.mifos.application.accounts.exceptions;

import org.mifos.framework.exceptions.ApplicationException;

public class AccountException extends ApplicationException {

	public AccountException(String key, Object[] values) {
		super(key, values);
	}

	public AccountException(String key, Throwable cause) {
		super(key, cause);
	}
}