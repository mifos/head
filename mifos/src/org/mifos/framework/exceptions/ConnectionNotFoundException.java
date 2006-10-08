package org.mifos.framework.exceptions;

public class ConnectionNotFoundException extends SystemException {
	
	protected String key = null;

	protected Object[] values = null;
	
	public ConnectionNotFoundException() {
		super();
	}

	public ConnectionNotFoundException(String key) {
		super(key);
		this.key = key;
	}

	public ConnectionNotFoundException(String key, Throwable cause) {
		super(key, cause);
		this.key = key;
	}

	public ConnectionNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public String getKey() {
		return "exception.framework.ConnectionNotFoundException";
	}

	public Object[] getValues() {
		return values;
	}

}
