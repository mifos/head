package org.mifos.application.rolesandpermission.exceptions;

import org.mifos.framework.exceptions.ApplicationException;

public class RolesPermissionException extends ApplicationException {

	public RolesPermissionException(Throwable cause) {
		super(cause);
	}

	public RolesPermissionException(String key) {
		super(key);
	}

}
