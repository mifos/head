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

package org.mifos.accounts.loan.business;

import java.util.Set;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

public class LoanTestUtils {

    /**
     * Check amount of the account fee in the installment. Note that this only
     * works correctly if the installment has just one account fee.
     */
    public static void assertOneInstallmentFee(Money expected, LoanScheduleEntity installment) {
        Set<AccountFeesActionDetailEntity> actionDetails = installment.getAccountFeesActionDetails();
        Assert.assertFalse("expected fee > 0.0 but loan has no account fees", expected.getAmountDoubleValue() > 0.0
                & actionDetails.size() == 0);
        for (AccountFeesActionDetailEntity detail : actionDetails) {
            Assert.assertEquals(expected, detail.getFeeDue());
        }
    }

    public static void assertInstallmentDetails(LoanScheduleEntity installment, Double principal, Double interest,
            Double accountFee, Double miscFee, Double miscPenalty) {
        Assert.assertEquals(new Money(TestUtils.getCurrency(), principal.toString()), installment.getPrincipalDue());
        Assert.assertEquals(new Money(TestUtils.getCurrency(), interest.toString()), installment.getInterestDue());
        Assert.assertEquals(new Money(TestUtils.getCurrency(), miscFee.toString()), installment.getMiscFeeDue());
        Assert.assertEquals(new Money(TestUtils.getCurrency(), miscPenalty.toString()), installment.getMiscPenaltyDue());
        assertOneInstallmentFee(new Money(TestUtils.getCurrency(), accountFee.toString()), installment);
    }

    public static void assertInstallmentDetails(LoanBO loan, int installmentId, Double total, Double principal,
            Double interest, Double accountFee, Double miscFee, Double miscPenalty) {

        LoanScheduleEntity installment = (LoanScheduleEntity) loan.getAccountActionDate((short) installmentId);
        Assert.assertEquals(new Money(TestUtils.getCurrency(), total.toString()), installment.getTotalPaymentDue());
        Assert.assertEquals(new Money(TestUtils.getCurrency(), principal.toString()), installment.getPrincipalDue());
        Assert.assertEquals(new Money(TestUtils.getCurrency(), interest.toString()), installment.getInterestDue());
        Assert.assertEquals(new Money(TestUtils.getCurrency(), miscFee.toString()), installment.getMiscFeeDue());
        Assert.assertEquals(new Money(TestUtils.getCurrency(), miscPenalty.toString()), installment.getMiscPenaltyDue());
        assertOneInstallmentFee(new Money(TestUtils.getCurrency(), accountFee.toString()), installment);
    }

    public static void assertInstallmentDetails(LoanBO loan, int installmentId, Double principal, Double interest,
            Double accountFee, Double miscFee, Double miscPenalty) {

        LoanScheduleEntity installment = (LoanScheduleEntity) loan.getAccountActionDate((short) installmentId);
        Assert.assertEquals(new Money(TestUtils.getCurrency(), principal.toString()), installment.getPrincipalDue());
        Assert.assertEquals(new Money(TestUtils.getCurrency(), interest.toString()), installment.getInterestDue());
        Assert.assertEquals(new Money(TestUtils.getCurrency(), miscFee.toString()), installment.getMiscFeeDue());
        Assert.assertEquals(new Money(TestUtils.getCurrency(), miscPenalty.toString()), installment.getMiscPenaltyDue());
        assertOneInstallmentFee(new Money(TestUtils.getCurrency(), accountFee.toString()), installment);
    }

    public static LoanScheduleEntity[] getSortedAccountActionDateEntity(
            Set<AccountActionDateEntity> actionDateCollection) {

        LoanScheduleEntity[] sortedList = new LoanScheduleEntity[actionDateCollection.size()];

        // Don't know whether it will always be 6 for future tests, but
        // right now it is...
        Assert.assertEquals(6, actionDateCollection.size());

        for (AccountActionDateEntity actionDateEntity : actionDateCollection) {
            sortedList[actionDateEntity.getInstallmentId().intValue() - 1] = (LoanScheduleEntity) actionDateEntity;
        }

        return sortedList;
    }

}
