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

package org.mifos.accounts.savings.business;

import java.util.Date;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.framework.util.helpers.Money;

public class SavingBOTestUtils {

    public static void setBalance(SavingsBO savings, Money balanceAmount) {
        savings.setSavingsBalance(balanceAmount);
    }

    public static void setNextIntCalcDate(SavingsBO savings, Date nextDate) {
        savings.setNextIntCalcDate(nextDate);
    }

    public static void setActivationDate(SavingsBO savings, Date nextDate) {
        savings.setActivationDate(nextDate);
    }

    public static void setNextIntPostDate(SavingsBO savings, Date nextDate) {
        savings.setNextIntPostDate(nextDate);
    }

    public static void setInterestToBePosted(SavingsBO savings, Money interest) {
        savings.setInterestToBePosted(interest);
    }

    public static void setDepositPaid(SavingsScheduleEntity actionDate, Money depositPaid) {
        actionDate.setDepositPaid(depositPaid);
    }

    public static void setActionDate(AccountActionDateEntity accountActionDateEntity, java.sql.Date actionDate) {
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(actionDate);
    }

    public static void setPaymentDate(AccountActionDateEntity accountActionDateEntity, java.sql.Date paymentDate) {
        ((SavingsScheduleEntity) accountActionDateEntity).setPaymentDate(paymentDate);
    }

}
