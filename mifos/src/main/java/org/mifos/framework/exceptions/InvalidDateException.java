package org.mifos.framework.exceptions;

import org.mifos.framework.util.helpers.ExceptionConstants;

public class InvalidDateException extends SystemException {

	private String dateString;
	
	public InvalidDateException(String dateStr) {
		super(ExceptionConstants.INVALIDDATEEXCEPTION);
		this.dateString = dateStr;
	}
	
	public String getDateString() {
		return dateString;
	}

}
