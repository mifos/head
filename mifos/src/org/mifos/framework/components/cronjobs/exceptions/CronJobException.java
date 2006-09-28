package org.mifos.framework.components.cronjobs.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.mifos.framework.exceptions.ApplicationException;

public class CronJobException extends ApplicationException {

	protected final String key;

	protected final List<String> values;

	public CronJobException(Throwable cause) {
		super(cause);
		this.key = null;
		values = new ArrayList<String>();
	}

	public CronJobException(String key, List<String> values) {
		this.key = key;
		this.values = values;
	}

	@Override
	public String getKey() {
		if (null == key) {
			return "exception.framework.CronJobException";
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
			errorMessage = builder.toString();
		} else {
			errorMessage = this.getMessage();
		}
		if (errorMessage.length() > 500) {
			return errorMessage.substring(0, 499);
		}
		return errorMessage;
	}
}
