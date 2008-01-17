package org.mifos.application.reports.business.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.mifos.application.master.MessageLookup;

public class Errors {
	List<ErrorEntry> errors;
	private final Locale locale;

	public Errors(Locale locale) {
		this.locale = locale;
		errors = new ArrayList<ErrorEntry>();
	}

	public void rejectValue(String fieldName, String errorCode) {
		errors.add(new ErrorEntry(fieldName, errorCode));
	}

	public List<String> getAllErrorMessages() {
		List<String> errorMessages = new ArrayList<String>();
		for (ErrorEntry entry : errors) {
			errorMessages.add(MessageLookup.getInstance().lookup(
					entry.errorCode, locale));

		}
		return errorMessages;
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public List<ErrorEntry> getErrors() {
		return errors;
	}

	public static class ErrorEntry {
		final String fieldName;
		final String errorCode;

		public ErrorEntry(String fieldName, String errorCode) {
			super();
			this.fieldName = fieldName;
			this.errorCode = errorCode;
		}
	}

	public ErrorEntry getFieldError(String fieldName) {
		for (ErrorEntry errorEntry : errors) {
			if(errorEntry.fieldName.equals(fieldName))
				return errorEntry;
		}
		return null;
	}
}
