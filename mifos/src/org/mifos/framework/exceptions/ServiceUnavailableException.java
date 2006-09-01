package org.mifos.framework.exceptions;

public class ServiceUnavailableException extends RuntimeException {

	protected String key = null;

	protected Object[] values = null;
	
	public ServiceUnavailableException() {
		super();
	}

	public ServiceUnavailableException(String key) {
		super(key);
		this.key = key;
	}

	public ServiceUnavailableException(String key, Throwable cause) {
		super(key, cause);
		this.key = key;
	}

	public ServiceUnavailableException(Throwable cause) {
		super(cause);
	}
	
	public String getKey() {
		return "exception.framework.ServiceUnavailableException";
	}

	public Object[] getValues() {
		return values;
	}


}
