package org.mifos.framework.exceptions;

public class ConstantsNotLoadedException extends SystemException {

	public ConstantsNotLoadedException(Throwable cause) {
		super(cause);
	}

	public ConstantsNotLoadedException(String key) {
		super(key);
		this.key = key;
	}

	public ConstantsNotLoadedException(String key, Throwable cause,
			Object[] values) {
		super(key,cause);
		this.values = values;
	}
}
