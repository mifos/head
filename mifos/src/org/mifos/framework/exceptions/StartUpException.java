package org.mifos.framework.exceptions;

import org.mifos.framework.util.helpers.ExceptionConstants;

public class StartUpException extends SystemException {
	protected String key = null;

	public StartUpException(Throwable cause) {
		super(cause);
		super.initCause(cause);
	}

	@Override
	public String getKey() {
		if (null == key) {
			return ExceptionConstants.STARTUP_EXCEPTION;
		} else {
			return this.key;
		}
	}

}
