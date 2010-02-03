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

package org.mifos.application.accounts.loan.business;

import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.application.accounts.AccountIntegrationTestCase;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.util.helpers.OverDueAmounts;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

public class LoanScheduleEntityIntegrationTest extends AccountIntegrationTestCase {

    public LoanScheduleEntityIntegrationTest() throws Exception {
        super();
    }

    public void testGetPrincipalDue() {
        LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO.getAccountActionDates().toArray()[0];
        accountActionDate.setPrincipalPaid(new Money(getCurrency(), "10.0"));
       Assert.assertEquals(TestUtils.makeMoney(90.0), accountActionDate.getPrincipalDue());

    }

    public void testGetInterestDue() {
        LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO.getAccountActionDates().toArray()[0];
        accountActionDate.setInterestPaid(new Money(getCurrency(), "2.0"));
       Assert.assertEquals(TestUtils.makeMoney(10.0), accountActionDate.getInterestDue());

    }

    public void testGetPenaltyDue() {
        LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO.getAccountActionDates().toArray()[0];
        accountActionDate.setPenalty(new Money(getCurrency(), "20.0"));
        accountActionDate.setPenaltyPaid(new Money(getCurrency(), "5.0"));
        accountActionDate.setMiscPenalty(new Money(getCurrency(), "10.0"));
        accountActionDate.setMiscPenaltyPaid(new Money(getCurrency(), "5.0"));
       Assert.assertEquals(TestUtils.makeMoney(20.0), accountActionDate.getPenaltyDue());

    }

    public void testGetTotalDue() {
        LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO.getAccountActionDates().toArray()[0];
        accountActionDate.setPrincipalPaid(new Money(getCurrency(), "10.0"));
        accountActionDate.setInterestPaid(new Money(getCurrency(), "2.0"));
        accountActionDate.setPenalty(new Money(getCurrency(), "20.0"));
        accountActionDate.setPenaltyPaid(new Money(getCurrency(), "5.0"));
        accountActionDate.setMiscPenalty(new Money(getCurrency(), "10.0"));
        accountActionDate.setMiscFee(new Money(getCurrency(), "20.0"));
        accountActionDate.setMiscFeePaid(new Money(getCurrency(), "5.0"));
       Assert.assertEquals(TestUtils.makeMoney(140.0), accountActionDate.getTotalDue());

    }

    public void testGetTotalDueWithFees() {
        LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO.getAccountActionDates().toArray()[0];
        accountActionDate.setPrincipalPaid(new Money(getCurrency(), "10.0"));
        accountActionDate.setInterestPaid(new Money(getCurrency(), "2.0"));
        accountActionDate.setPenalty(new Money(getCurrency(), "20.0"));
        accountActionDate.setPenaltyPaid(new Money(getCurrency(), "5.0"));
        accountActionDate.setMiscPenalty(new Money(getCurrency(), "10.0"));
        accountActionDate.setMiscFee(new Money(getCurrency(), "20.0"));
        accountActionDate.setMiscFeePaid(new Money(getCurrency(), "5.0"));
       Assert.assertEquals(TestUtils.makeMoney(240.0), accountActionDate.getTotalDueWithFees());

    }

    public void testGetDueAmounts() {
        LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO.getAccountActionDates().toArray()[0];
        accountActionDate.setPrincipalPaid(new Money(getCurrency(), "10.0"));
        accountActionDate.setInterestPaid(new Money(getCurrency(), "2.0"));
        accountActionDate.setPenalty(new Money(getCurrency(), "20.0"));
        accountActionDate.setPenaltyPaid(new Money(getCurrency(), "5.0"));
        accountActionDate.setMiscPenalty(new Money(getCurrency(), "10.0"));
        accountActionDate.setMiscFee(new Money(getCurrency(), "20.0"));
        accountActionDate.setMiscFeePaid(new Money(getCurrency(), "5.0"));
       Assert.assertEquals(TestUtils.makeMoney(115.0), accountActionDate.getDueAmnts().getFeesOverdue());

    }

    public void testGetTotalDueAmounts() {
        LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO.getAccountActionDates().toArray()[0];
        accountActionDate.setPrincipalPaid(new Money(getCurrency(), "10.0"));
        accountActionDate.setInterestPaid(new Money(getCurrency(), "2.0"));
        accountActionDate.setPenalty(new Money(getCurrency(), "20.0"));
        accountActionDate.setPenaltyPaid(new Money(getCurrency(), "5.0"));
        accountActionDate.setMiscPenalty(new Money(getCurrency(), "10.0"));
        accountActionDate.setMiscFee(new Money(getCurrency(), "20.0"));
        accountActionDate.setMiscFeePaid(new Money(getCurrency(), "5.0"));
        OverDueAmounts totalDue = accountActionDate.getDueAmnts();
       Assert.assertEquals(TestUtils.makeMoney(115.0), totalDue.getFeesOverdue());
       Assert.assertEquals(TestUtils.makeMoney(90.0), totalDue.getPrincipalOverDue());
       Assert.assertEquals(TestUtils.makeMoney(10.0), totalDue.getInterestOverdue());
       Assert.assertEquals(TestUtils.makeMoney(25.0), totalDue.getPenaltyOverdue());

    }

    public void testGetTotalScheduleAmountWithFees() {
        LoanScheduleEntity accountActionDate = new LoanScheduleEntity(accountBO, accountBO.getCustomer(), Short
                .valueOf("1"), new java.sql.Date(System.currentTimeMillis()), PaymentStatus.UNPAID, new Money(getCurrency(), "100"),
                new Money(getCurrency(), "10"));
        accountActionDate.setPenalty(new Money(getCurrency(), "10.0"));
        accountActionDate.setMiscPenalty(new Money(getCurrency(), "10.0"));
        accountActionDate.setMiscFee(new Money(getCurrency(), "20.0"));

        accountActionDate.setPrincipalPaid(new Money(getCurrency()));
        accountActionDate.setInterestPaid(new Money(getCurrency()));
        accountActionDate.setPenaltyPaid(new Money(getCurrency()));
        accountActionDate.setMiscPenaltyPaid(new Money(getCurrency()));
        accountActionDate.setMiscFeePaid(new Money(getCurrency()));

        LoanFeeScheduleEntity loanFeeSchedule = new LoanFeeScheduleEntity(accountActionDate, null, null,
                new Money(getCurrency(), "10"));
        loanFeeSchedule.setFeeAmountPaid(new Money(getCurrency()));
        LoanFeeScheduleEntity loanFeeSchedule1 = new LoanFeeScheduleEntity(accountActionDate, null, null, new Money(
                getCurrency(), "10"));
        loanFeeSchedule1.setFeeAmountPaid(new Money(getCurrency()));
        accountActionDate.addAccountFeesAction(loanFeeSchedule);
        accountActionDate.addAccountFeesAction(loanFeeSchedule1);

       Assert.assertEquals(new Money(getCurrency(), "170"), accountActionDate.getTotalScheduleAmountWithFees());
    }

    public void testIsPricipalZero() {
        for (AccountActionDateEntity accountAction : accountBO.getAccountActionDates()) {
            LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
            if (accountActionDateEntity.getInstallmentId() == 1) {
                accountActionDateEntity.setPrincipal(new Money(getCurrency()));
               Assert.assertTrue(accountActionDateEntity.isPrincipalZero());
            } else
                Assert.assertFalse(accountActionDateEntity.isPrincipalZero());
        }
    }

    public void testMakeEarlyRepaymentEnteriesForFeePayment() {
        for (AccountActionDateEntity accountAction : accountBO.getAccountActionDates()) {
            LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
            accountActionDateEntity.makeEarlyRepaymentEnteries(LoanConstants.PAY_FEES_PENALTY_INTEREST);
           Assert.assertEquals(accountActionDateEntity.getPrincipal(), accountActionDateEntity.getPrincipalPaid());
           Assert.assertEquals(accountActionDateEntity.getInterest(), accountActionDateEntity.getInterestPaid());
           Assert.assertEquals(accountActionDateEntity.getPenalty(), accountActionDateEntity.getPenaltyPaid());
           Assert.assertEquals(accountActionDateEntity.getMiscFee(), accountActionDateEntity.getMiscFeePaid());
           Assert.assertTrue(accountActionDateEntity.isPaid());
        }
    }

    public void testMakeEarlyRepaymentEnteriesForNotPayingFee() {
        for (AccountActionDateEntity accountAction : accountBO.getAccountActionDates()) {
            LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
            accountActionDateEntity.makeEarlyRepaymentEnteries(LoanConstants.DONOT_PAY_FEES_PENALTY_INTEREST);
           Assert.assertEquals(accountActionDateEntity.getPrincipal(), accountActionDateEntity.getPrincipalPaid());
           Assert.assertEquals(accountActionDateEntity.getInterest(), accountActionDateEntity.getInterestPaid());
           Assert.assertEquals(accountActionDateEntity.getPenalty(), accountActionDateEntity.getPenaltyPaid());
           Assert.assertEquals(accountActionDateEntity.getMiscFee(), accountActionDateEntity.getMiscFeePaid());
           Assert.assertTrue(accountActionDateEntity.isPaid());
        }
    }

    public void testSuccessRemoveFees() {
        Short feeId = null;
        Set<AccountFeesEntity> accountFeesSet = accountBO.getAccountFees();
        for (AccountFeesEntity accountFeesEntity : accountFeesSet) {
            feeId = accountFeesEntity.getFees().getFeeId();
            break;
        }
        Set<AccountActionDateEntity> accountActionDateEntitySet = accountBO.getAccountActionDates();
        Iterator<AccountActionDateEntity> itr = accountActionDateEntitySet.iterator();
        while (itr.hasNext()) {
            LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) itr.next();
            accountActionDateEntity.removeFees(feeId);
           Assert.assertTrue(true);
        }
    }

}
