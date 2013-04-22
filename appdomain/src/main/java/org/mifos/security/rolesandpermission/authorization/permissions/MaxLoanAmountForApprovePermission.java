package org.mifos.security.rolesandpermission.authorization.permissions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.persistance.LoanDaoHibernate;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.persistence.LoanProductDaoHibernate;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.dto.domain.AccountUpdateStatus;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.mifos.security.rolesandpermission.business.ActivityRestrictionTypeEntity;
import org.mifos.security.rolesandpermission.business.RoleActivityRestrictionBO;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.security.rolesandpermission.persistence.LegacyRolesPermissionsDao;
import org.mifos.security.util.helpers.ActivityRestrictionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

/**
 * If new loan account status is "Approved" then checks if user role has max loan amount
 * for this activity;
 * {@link http://mifosforge.jira.com/browse/MIFOS-5408}
 */
public class MaxLoanAmountForApprovePermission implements MifosPermission {

    @Autowired
    private LoanDao loanDao;

    private RolesPermissionsBusinessService rolesPermissionsBusinessService;

    public MaxLoanAmountForApprovePermission() {
        // FIXME : somehow autowiring is not working
        if (loanDao == null) {
            loanDao = ApplicationContextProvider.getBean(LoanDaoHibernate.class);
        }
        rolesPermissionsBusinessService = new RolesPermissionsBusinessService();
    }

    @Override
    public boolean isAllowed(Authentication authentication, Object targetDomainObject) throws ServiceException {
        if (targetDomainObject instanceof AccountUpdateStatus) {

            AccountUpdateStatus accountUpdateStatus = (AccountUpdateStatus) targetDomainObject;
            MifosUser user = (MifosUser) authentication.getPrincipal();
            return checkPermissionByAccountUpdateStatus(user, accountUpdateStatus);

        } else if (targetDomainObject instanceof List) {

            boolean isAllowed = true;
            List<AccountUpdateStatus> accountsUpdateStatus = (List<AccountUpdateStatus>) targetDomainObject;
            MifosUser user = (MifosUser) authentication.getPrincipal();
            for (AccountUpdateStatus accountUpdateStatus : accountsUpdateStatus) {
                isAllowed = checkPermissionByAccountUpdateStatus(user, accountUpdateStatus);
                if (!isAllowed) {
                    return false;
                }
            }
            return isAllowed;

        }

        return true;
    }

    private boolean checkPermissionByAccountUpdateStatus(MifosUser user, AccountUpdateStatus accountUpdateStatus)
            throws ServiceException {
        boolean isAllowed = false;

        if (!accountUpdateStatus.getNewStatusId().equals(AccountState.LOAN_APPROVED.getValue())) {
            return true;
        }
        LoanBO loanBO = loanDao.findById(accountUpdateStatus.getSavingsId().intValue());
        Money totalPrincipalAmount = loanBO.getTotalPrincipalAmount();
        BigDecimal maxAmountRestriction = BigDecimal.ZERO;

        List<Short> roleIds = user.getRoleIds();
        for (Short roleId : roleIds) {
            BigDecimal amountRestriction = rolesPermissionsBusinessService
                    .getRoleActivityRestrictionAmountValueByRestrictionTypeId(roleId,
                            ActivityRestrictionType.MAX_LOAN_AMOUNT_FOR_APPROVE.getValue());
            
            if (amountRestriction != null && amountRestriction.compareTo(maxAmountRestriction) == 1) {
                maxAmountRestriction = amountRestriction;
            }
            
            if ((amountRestriction == null && maxAmountRestriction.compareTo(BigDecimal.ZERO) == 0)
                    || (maxAmountRestriction.compareTo(BigDecimal.ZERO) != 0 && totalPrincipalAmount
                            .isLessThan(new Money(loanBO.getCurrency(), maxAmountRestriction)))) {
                isAllowed = true;
            }
        }

        return isAllowed;
    }

}
