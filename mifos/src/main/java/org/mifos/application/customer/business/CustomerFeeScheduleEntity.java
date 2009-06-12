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

package org.mifos.application.customer.business;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.framework.util.helpers.Money;

public class CustomerFeeScheduleEntity extends AccountFeesActionDetailEntity {

    public CustomerFeeScheduleEntity(AccountActionDateEntity accountActionDate, FeeBO fee,
            AccountFeesEntity accountFee, Money feeAmount) {
        super(accountActionDate, fee, accountFee, feeAmount);
    }

    protected CustomerFeeScheduleEntity() {
        super(null, null, null, null);
    }

    @Override
    protected void setFeeAmount(Money feeAmount) {
        super.setFeeAmount(feeAmount);
    }

    @Override
    protected void setFeeAmountPaid(Money feeAmountPaid) {
        super.setFeeAmountPaid(feeAmountPaid);
    }

    @Override
    protected void makePayment(Money feePaid) {
        super.makePayment(feePaid);
    }

    @Override
    protected void makeRepaymentEnteries(String payFullOrPartial) {
        super.makeRepaymentEnteries(payFullOrPartial);
    }

    @Override
    protected Money waiveCharges() {
        return super.waiveCharges();
    }
}
