/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.api.AccountReferenceDto;
import org.mifos.accounts.api.UserReferenceDto;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.servicefacade.ListItem;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;

/**
 * Concrete implementation of service to manipulate accounts
 * from the presentation layer.
 *
 */
public class WebTierAccountServiceFacade implements AccountServiceFacade {

    @Override
    public AccountPaymentDto getAccountPaymentInformation(AccountReferenceDto accountReferenceDto,
            String paymentType, Short localeId, UserReferenceDto userReferenceDto) throws Exception {
        AccountBO account = new AccountBusinessService().getAccount(Integer.valueOf(accountReferenceDto.getAccountId()));

        UserReferenceDto accountUser = null;
        if (account.getPersonnel() != null) {
            accountUser = new UserReferenceDto(account.getPersonnel().getPersonnelId());
        } else {
            accountUser = userReferenceDto;
        }
        AccountPaymentDto accountPaymentDto = new AccountPaymentDto(
                AccountTypeDto.getAccountType(account.getAccountType().getAccountTypeId()),
                account.getVersionNo(), constructPaymentTypeList(paymentType, localeId),
                account.getTotalPaymentDue(), accountUser);

        return accountPaymentDto;
    }

    private List<ListItem<Short>> constructPaymentTypeList(String paymentType, Short localeId) throws Exception {
        List<PaymentTypeEntity> paymentTypeList = null;
        if (paymentType != null && paymentType.trim() != Constants.EMPTY_STRING) {
            if (paymentType.equals(Constants.LOAN)) {
                paymentTypeList = new AcceptedPaymentTypePersistence().
                    getAcceptedPaymentTypesForATransaction(localeId, TrxnTypes.loan_repayment.getValue());
            } else {
                paymentTypeList = new AcceptedPaymentTypePersistence().
                    getAcceptedPaymentTypesForATransaction(localeId, TrxnTypes.fee.getValue());
            }
        }

        List<ListItem<Short>> listItems = new ArrayList<ListItem<Short>>();
        for (PaymentTypeEntity paymentTypeEntity : paymentTypeList) {
            listItems.add(new ListItem<Short>(paymentTypeEntity.getId(), paymentTypeEntity.getName()));
        }
        return listItems;
    }

    @Override
    public boolean isPaymentPermitted(final AccountReferenceDto accountReferenceDto, final UserContext userContext) throws ServiceException {
        AccountBO account = new AccountBusinessService().getAccount(Integer.valueOf(accountReferenceDto.getAccountId()));
        CustomerLevel customerLevel = null;
        if (account.getType().equals(AccountTypes.CUSTOMER_ACCOUNT)) {
            customerLevel = account.getCustomer().getLevel();
        }

        Short personnelId = null;
        if (account.getPersonnel() != null) {
            personnelId = account.getPersonnel().getPersonnelId();
        } else {
            personnelId = userContext.getId();
        }

        return ActivityMapper.getInstance().isPaymentPermittedForAccounts(account.getType(), customerLevel, userContext,
                account.getOffice().getOfficeId(), personnelId);
    }



}
