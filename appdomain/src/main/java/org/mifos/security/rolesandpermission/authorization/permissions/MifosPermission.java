package org.mifos.security.rolesandpermission.authorization.permissions;

import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.springframework.security.core.Authentication;

public interface MifosPermission {
    boolean isAllowed(Authentication authentication, Object targetDomainObject) throws SystemException, ApplicationException;
}
