package org.mifos.framework.components.batchjobs.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.mifos.framework.exceptions.ApplicationException;

public class BatchJobException extends ApplicationException {

	protected final String key;

	protected final List<String> values;

	public BatchJobException(Throwable cause) {
		super(cause);
		this.key = null;
		values = new ArrayList<String>();
	}

	public BatchJobException(String key, List<String> values) {
		super(key);
		this.key = key;
		this.values = values;
	}

	@Override
	public String getKey() {
		if (null == key) {
			return "exception.framework.BatchJobException";
		} else {
			return this.key;
		}
	}

	public String getErrorMessage() {
		String errorMessage = null;
		if (values.size() > 0) {
			StringBuilder builder = new StringBuilder();
			for (String string : values) {
				builder.append(string);
				builder.append(",");
			}
			errorMessage = builder.substring(0,builder.length()-1);
		} else {
			errorMessage = this.getMessage();
		}
		if (errorMessage.length() > 500) {
			return errorMessage.substring(0, 499);
		}
		return errorMessage;
	}
}
