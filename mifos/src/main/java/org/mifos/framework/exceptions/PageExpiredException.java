package org.mifos.framework.exceptions;

import org.mifos.framework.util.helpers.ExceptionConstants;

public class PageExpiredException extends ApplicationException {

	public PageExpiredException() {
		this(null);
	}

	public PageExpiredException(String internalMessage) {
		super(ExceptionConstants.PAGEEXPIREDEXCEPTION, null, internalMessage);
	}

}
