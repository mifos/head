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

package org.mifos.accounts.savings.util.helpers;

import java.util.Date;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.util.helpers.Money;

public class SavingsHelper {

    public SavingsHelper() {
    }

    @Deprecated
    public AccountActionDateEntity createActionDateObject(AccountBO account, CustomerBO customer, Short installmentId,
            Date date, Short userId, Money amount) {
        AccountActionDateEntity actionDate = new SavingsScheduleEntity(account, customer, installmentId,
                new java.sql.Date(date.getTime()), PaymentStatus.UNPAID, amount);
        return actionDate;
    }

    public Short getPaymentActionType(AccountPaymentEntity payment) {
        for (AccountTrxnEntity accntTrxn : payment.getAccountTrxns()) {
            if (!accntTrxn.getAccountActionEntity().getId().equals(AccountActionTypes.SAVINGS_ADJUSTMENT.getValue())) {
                return accntTrxn.getAccountActionEntity().getId();
            }
        }
        return null;
    }
}
