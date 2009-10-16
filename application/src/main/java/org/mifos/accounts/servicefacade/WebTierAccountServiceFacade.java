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

import org.mifos.api.accounts.AccountReferenceDto;
import org.mifos.api.accounts.UserReferenceDto;
import org.mifos.application.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.servicefacade.ListItem;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
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
        
        UserReferenceDto accountUser = userReferenceDto;
        if (account.getPersonnel() != null) {
            accountUser.setUserId(account.getPersonnel().getPersonnelId());
        }
        AccountPaymentDto accountPaymentDto = new AccountPaymentDto(
                AccountTypeDto.getAccountType(account.getAccountType().getAccountTypeId()),
                account.getVersionNo(), constructPaymentTypeList(paymentType, localeId),
                account.getTotalAmountDue(), accountUser);
        
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
