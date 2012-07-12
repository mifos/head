package org.mifos.security.rolesandpermission.authorization.permissions;

import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.MifosUser;
import org.springframework.security.core.Authentication;

public class CustomerStatusPermission implements MifosPermission {

    @Override
    public boolean isAllowed(Authentication authentication, Object targetDomainObject) throws ServiceException {

        MifosUser user = (MifosUser) authentication.getPrincipal();
        return checkPermissionToEditCustomerInformation(user, targetDomainObject);
    }

    private boolean checkPermissionToEditCustomerInformation(MifosUser user, Object status)
            throws ServiceException {
        boolean isAllowed = false;
        if(status instanceof String){
        	if (status.equals(CustomerConstants.CLIENT_STATUS_PARTIAL)) {
        		isAllowed = true;
        	} 
        }
        else {
        	if (((Short)status).intValue() == user.getUserId()) {
        		isAllowed = true;
        	}
        }
        return isAllowed;
    }

}
