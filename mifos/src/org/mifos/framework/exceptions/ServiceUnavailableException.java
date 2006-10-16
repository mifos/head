package org.mifos.framework.exceptions;

public class ServiceUnavailableException extends SystemException {

	protected String key = null;

	public ServiceUnavailableException(String key, Throwable cause) {
		super(key, cause);
		this.key = key;
	}

	@Override
	public String getKey() {
		return "exception.framework.ServiceUnavailableException";
	}

}
