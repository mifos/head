package org.mifos.security.rolesandpermission.authorization.permissions;

import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.MifosUser;
import org.springframework.security.core.Authentication;

public class CustomerStatusPermission implements MifosPermission {

    @Override
    public boolean isAllowed(Authentication authentication, Object targetDomainObject) throws ServiceException {

        String customerStatus = (String) targetDomainObject;
        MifosUser user = (MifosUser) authentication.getPrincipal();
        return checkPermissionToEditCustomerInformation(user, customerStatus);
    }

    private boolean checkPermissionToEditCustomerInformation(MifosUser user, String customerStatus)
            throws ServiceException {
        boolean isAllowed = false;
        if (customerStatus.equals(CustomerConstants.CLIENT_STATUS_PARTIAL)) {
            isAllowed = true;
        }
        return isAllowed;
    }

}
