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

package org.mifos.accounts.business;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class AccountTestUtils {

    public static void addAccountActionDate(AccountActionDateEntity accountAction, AccountBO account) {
        account.addAccountActionDate(accountAction);
    }


    public static void addToAccountStatusChangeHistory(LoanBO loan,
            AccountStatusChangeHistoryEntity accountStatusChangeHistoryEntity) {
        loan.addAccountStatusChangeHistory(accountStatusChangeHistoryEntity);
    }

    public static void addAccountFees(AccountFeesEntity fees, AccountBO account) {
        account.addAccountFees(fees);
    }

    public static void addAccountFlag(AccountStateFlagEntity flagDetail, AccountBO account) {
        account.addAccountFlag(flagDetail);
    }

    public static void addAccountPayment(AccountPaymentEntity payment, AccountBO account) {
        account.addAccountPayment(payment);
    }

    public static List<AccountTrxnEntity> reversalAdjustment(String adjustmentComment, AccountPaymentEntity lastPayment)
            throws Exception {
        return lastPayment.reversalAdjustment(new PersonnelPersistence().getPersonnel(TestObjectFactory.getContext()
                .getId()), adjustmentComment);

    }

    /**
     * Changes <em>all</em> installment dates to yesterday. In production,
     * multiple installments should never have the same ACTION_DATE.
     */
    public static void changeInstallmentDatesToPreviousDate(AccountBO accountBO) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - 1);
        for (AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()) {
            accountActionDateEntity.setActionDate(new java.sql.Date(currentDateCalendar.getTimeInMillis()));
        }
    }

    public static void changeInstallmentDatesToPreviousDateExceptLastInstallment(AccountBO accountBO,
            int noOfInstallmentsToBeChanged) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - 1);
        for (int i = 1; i <= noOfInstallmentsToBeChanged; i++) {
            AccountActionDateEntity accountActionDateEntity = accountBO.getAccountActionDate(Integer.valueOf(i)
                    .shortValue());
            accountActionDateEntity.setActionDate(new java.sql.Date(currentDateCalendar.getTimeInMillis()));
        }
    }
}
