package org.mifos.application.rolesandpermission.exceptions;

import org.mifos.framework.exceptions.ApplicationException;

public class RolesPermissionException extends ApplicationException {

	public RolesPermissionException() {
		super();
	}

	public RolesPermissionException(Object[] values) {
		super(values);
	}

	public RolesPermissionException(String key, Object[] values) {
		super(key, values);
	}

	public RolesPermissionException(String key, Throwable cause) {
		super(key, cause);
	}

	public RolesPermissionException(Throwable cause) {
		super(cause);
	}

	public RolesPermissionException(String key) {
		super.setKey(key);
	}

}
