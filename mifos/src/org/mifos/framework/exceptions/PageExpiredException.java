package org.mifos.framework.exceptions;

public class PageExpiredException extends ApplicationException {

	public PageExpiredException() {
		super();
	}

	public PageExpiredException(Object[] values) {
		super(values);
	}

	public PageExpiredException(Throwable cause) {
		super(cause);
	}

	public PageExpiredException(String key) {
		super(key);
	}

	public PageExpiredException(String key, Object[] values) {
		super(key, values);
	}

	public PageExpiredException(String key, Throwable cause) {
		super(key, cause);
	}

	public PageExpiredException(String key, Throwable cause, Object[] values) {
		super(key, cause, values);
	}

}
