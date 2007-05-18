package org.mifos.framework.exceptions;

import org.mifos.framework.util.helpers.ExceptionConstants;

public class PermissionFailureException extends ApplicationException {
	
	public PermissionFailureException() {
		super(ExceptionConstants.PERMISSIONFAILUREEXCEPTION);
	}

}
