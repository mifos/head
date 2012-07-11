package org.mifos.security.rolesandpermission.authorization;

import java.io.Serializable;

import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.security.rolesandpermission.authorization.permissions.MaxLoanAmountForApprovePermission;
import org.mifos.security.rolesandpermission.authorization.permissions.MifosPermission;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom Mifos PermissionEvaluator.
 * See http://static.springsource.org/spring-security/site/docs/3.1.x/reference/el-access.html#el-permission-evaluator
 */
public class MifosPermissionEvaluator implements PermissionEvaluator {

    public MifosPermissionEvaluator() {
        super();
    }

    @Override
    @Transactional
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        try {
            return checkPermission(authentication, targetDomainObject, permission);
        } catch (SystemException e) {
            throw new MifosRuntimeException(e);
        } catch (ApplicationException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    @Transactional
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        return false;
    }

    public boolean checkPermission(Authentication authentication, Object targetDomainObject, Object permissionName) throws SystemException, ApplicationException{
        MifosPermission permission = MifosPermissionFactory.getPermissionClass(permissionName);
        return permission.isAllowed(authentication, targetDomainObject);
    }
    
}
