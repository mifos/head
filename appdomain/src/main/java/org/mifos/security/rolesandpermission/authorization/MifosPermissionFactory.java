package org.mifos.security.rolesandpermission.authorization;

import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.security.rolesandpermission.authorization.permissions.MaxLoanAmountForApprovePermission;
import org.mifos.security.rolesandpermission.authorization.permissions.MifosPermission;
import org.mifos.security.rolesandpermission.authorization.permissions.CustomerStatusPermission;
import org.mifos.security.util.helpers.ActivityRestrictionType;

public class MifosPermissionFactory {

    public static MifosPermission getPermissionClass(Object permissionName) {
        if (permissionName.equals(ActivityRestrictionType.MAX_LOAN_AMOUNT_FOR_APPROVE.toString())) {
            return new MaxLoanAmountForApprovePermission();
        } else if (permissionName.equals(CustomerConstants.CLIENT_STATUS)) {
            return new CustomerStatusPermission();
        }
        return null;
    }

}
