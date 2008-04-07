package org.mifos.framework.exceptions;

public class ConnectionNotFoundException extends SystemException {

	protected String key = null;

	public ConnectionNotFoundException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getKey() {
		return "exception.framework.ConnectionNotFoundException";
	}

}
