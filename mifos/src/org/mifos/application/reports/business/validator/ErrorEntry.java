package org.mifos.application.reports.business.validator;

public class ErrorEntry {
	final String fieldName;
	final String errorCode;

	public ErrorEntry(String fieldName, String errorCode) {
		super();
		this.fieldName = fieldName;
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getFieldName() {
		return fieldName;
	}
}
