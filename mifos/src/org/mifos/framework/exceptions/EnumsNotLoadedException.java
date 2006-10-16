package org.mifos.framework.exceptions;

public class EnumsNotLoadedException extends SystemException {

	public EnumsNotLoadedException(String key, Throwable cause, Object[] values) {
		super(key,cause);
		this.key = key;
		super.initCause(cause);
		this.values = values;
	}
}
