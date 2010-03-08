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

package org.mifos.customers.business;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.framework.util.helpers.Money;

public class CustomerAccountBOTestUtils {

    public static void setMiscFee(CustomerScheduleEntity customerSchedule, Money miscFee) {
        customerSchedule.setMiscFee(miscFee);
    }

    public static void setMiscFeePaid(CustomerScheduleEntity customerSchedule, Money miscFeePaid) {
        customerSchedule.setMiscFeePaid(miscFeePaid);
    }

    public static void setMiscPenaltyPaid(CustomerScheduleEntity customerSchedule, Money miscPenaltyPaid) {
        customerSchedule.setMiscPenaltyPaid(miscPenaltyPaid);
    }

    public static void applyPeriodicFees(CustomerScheduleEntity customerSchedule, Short feeId, Money totalAmount) {
        customerSchedule.applyPeriodicFees(feeId, totalAmount);
    }

    public static Money waiveCharges(CustomerScheduleEntity customerSchedule) {
        return customerSchedule.waiveCharges();
    }

    public static void setFeeAmount(CustomerFeeScheduleEntity customerFeeScheduleEntity, Money feeAmount) {
        customerFeeScheduleEntity.setFeeAmount(feeAmount);
    }

    public static void setFeeAmountPaid(CustomerFeeScheduleEntity customerFeeScheduleEntity, Money feeAmountPaid) {
        customerFeeScheduleEntity.setFeeAmountPaid(feeAmountPaid);
    }

    public static void setActionDate(AccountActionDateEntity accountActionDateEntity, java.sql.Date actionDate) {
        ((CustomerScheduleEntity) accountActionDateEntity).setActionDate(actionDate);
    }

    public static void setPaymentDate(AccountActionDateEntity accountActionDateEntity, java.sql.Date paymentDate) {
        accountActionDateEntity.setPaymentDate(paymentDate);
    }

}
