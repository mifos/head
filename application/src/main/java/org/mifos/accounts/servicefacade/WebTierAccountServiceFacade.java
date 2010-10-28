/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.accounts.servicefacade;

import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.api.UserReferenceDto;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.ApplicableCharge;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.servicefacade.ListItem;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of service to manipulate accounts from the presentation layer.
 *
 */
public class WebTierAccountServiceFacade implements AccountServiceFacade {

    @Override
    public AccountPaymentDto getAccountPaymentInformation(Integer accountId, String paymentType, Short localeId, UserReferenceDto userReferenceDto) throws Exception {
        AccountBO account = new AccountBusinessService().getAccount(accountId);

        UserReferenceDto accountUser = userReferenceDto;
        if (account.getPersonnel() != null) {
            accountUser = new UserReferenceDto(account.getPersonnel().getPersonnelId());
        }

        List<ListItem<Short>> paymentTypeList = constructPaymentTypeList(paymentType, localeId);
        AccountTypeDto accountType = AccountTypeDto.getAccountType(account.getAccountType().getAccountTypeId());
        return new AccountPaymentDto(accountType, account.getVersionNo(), paymentTypeList, account.getTotalPaymentDue(), accountUser);
    }

    private List<ListItem<Short>> constructPaymentTypeList(String paymentType, Short localeId) throws Exception {
        List<PaymentTypeEntity> paymentTypeList = null;
        if (paymentType != null && paymentType.trim() != Constants.EMPTY_STRING) {
            if (paymentType.equals(Constants.LOAN)) {
                paymentTypeList = new AcceptedPaymentTypePersistence().getAcceptedPaymentTypesForATransaction(localeId,
                        TrxnTypes.loan_repayment.getValue());
            } else {
                paymentTypeList = new AcceptedPaymentTypePersistence().getAcceptedPaymentTypesForATransaction(localeId,
                        TrxnTypes.fee.getValue());
            }
        }

        List<ListItem<Short>> listItems = new ArrayList<ListItem<Short>>();
        for (PaymentTypeEntity paymentTypeEntity : paymentTypeList) {
            listItems.add(new ListItem<Short>(paymentTypeEntity.getId(), paymentTypeEntity.getName()));
        }
        return listItems;
    }

    @Override
    public boolean isPaymentPermitted(final UserContext userContext, Integer accountId)
            throws ServiceException {
        AccountBO account = new AccountBusinessService().getAccount(accountId);
        CustomerLevel customerLevel = null;
        if (account.getType().equals(AccountTypes.CUSTOMER_ACCOUNT)) {
            customerLevel = account.getCustomer().getLevel();
        }

        Short personnelId = userContext.getId();
        if (account.getPersonnel() != null) {
            personnelId = account.getPersonnel().getPersonnelId();
        }

        return ActivityMapper.getInstance().isPaymentPermittedForAccounts(account.getType(), customerLevel,
                userContext, account.getOffice().getOfficeId(), personnelId);
    }

    @Override
    public List<ApplicableCharge> getApplicableFees(Integer accountId, UserContext userContext) throws ServiceException {
        return new AccountBusinessService().getAppllicableFees(accountId, userContext);
    }

    @Override
    public void applyCharge(Integer accountId, UserContext userContext, Short feeId, Double chargeAmount)
            throws ApplicationException {

        AccountBO account = new AccountBusinessService().getAccount(accountId);

        account.setUserContext(userContext);

        CustomerLevel customerLevel = null;
        if (account.getType().equals(AccountTypes.CUSTOMER_ACCOUNT)) {
            customerLevel = account.getCustomer().getLevel();
        }
        if (account.getPersonnel() != null) {
            checkPermissionForApplyCharges(account.getType(), customerLevel, userContext, account.getOffice()
                    .getOfficeId(), account.getPersonnel().getPersonnelId());
        } else {
            checkPermissionForApplyCharges(account.getType(), customerLevel, userContext, account.getOffice()
                    .getOfficeId(), userContext.getId());
        }

        account.applyCharge(feeId, chargeAmount);
        account.update();

    }

    private void checkPermissionForApplyCharges(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) throws ApplicationException {
        if (!isPermissionAllowed(accountTypes, customerLevel, userContext, recordOfficeId, recordLoanOfficerId)) {
            throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowed(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return ActivityMapper.getInstance().isApplyChargesPermittedForAccounts(accountTypes, customerLevel,
                userContext, recordOfficeId, recordLoanOfficerId);
    }

    @Override
    public AccountTypeCustomerLevelDto getAccountTypeCustomerLevelDto(Integer accountId) throws ServiceException {

        AccountBO account = new AccountBusinessService().getAccount(accountId);
        return new AccountTypeCustomerLevelDto(account.getType().getValue(), account.getCustomer().getCustomerLevel()
                .getId());
    }
}
