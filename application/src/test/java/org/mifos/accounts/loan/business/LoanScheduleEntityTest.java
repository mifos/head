/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.accounts.loan.business;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoanScheduleEntityTest {
    private LoanScheduleEntity loanScheduleEntity;
    private MifosCurrency rupeeCurrency;

    @Mock
    private AccountBO accountBO;

    @Before
    public void setUp() {
        loanScheduleEntity = new LoanScheduleEntity();
        rupeeCurrency = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
    }

    @Test
    public void shouldPayComponentsAndAllocatePayments() {
        LoanFeeScheduleEntity fee1, fee2, fee3, fee4, fee5, fee6;
        loanScheduleEntity.setPrincipal(makeMoney(300));
        loanScheduleEntity.setPrincipalPaid(makeMoney(150));
        loanScheduleEntity.setInterest(makeMoney(20));
        loanScheduleEntity.setInterestPaid(makeMoney(10));
        loanScheduleEntity.addAccountFeesAction(fee1 = getFee(11, 10, 5));
        loanScheduleEntity.addAccountFeesAction(fee2 = getFee(22, 8, 4));
        loanScheduleEntity.addAccountFeesAction(fee3 = getFee(33, 6, 3));
        loanScheduleEntity.addAccountFeesAction(fee4 = getFee(44, 4, 2));
        loanScheduleEntity.addAccountFeesAction(fee5 = getFee(55, 2, 1));
        loanScheduleEntity.addAccountFeesAction(fee6 = getFee(66, 1, 0));
        loanScheduleEntity.setMiscFee(makeMoney(0));
        loanScheduleEntity.setMiscFeePaid(makeMoney(0));
        loanScheduleEntity.setPenalty(makeMoney(1));
        loanScheduleEntity.setPenaltyPaid(makeMoney(1));
        loanScheduleEntity.setMiscPenalty(makeMoney(10));
        loanScheduleEntity.setMiscPenaltyPaid(makeMoney(5));

        loanScheduleEntity.setAccount(accountBO);
        when(accountBO.getCurrency()).thenReturn(rupeeCurrency);

        Money paymentAmount = makeMoney(1000d);
        Money balance = loanScheduleEntity.payComponents(paymentAmount);
        PaymentAllocation paymentAllocation = loanScheduleEntity.getPaymentAllocation();

        assertThat(paymentAllocation.getPenaltyPaid().getAmount().doubleValue(), is(0d));
        assertThat(paymentAllocation.getMiscFeePaid().getAmount().doubleValue(), is(0d));
        assertThat(paymentAllocation.getMiscPenaltyPaid().getAmount().doubleValue(), is(5d));
        assertThat(paymentAllocation.getInterestPaid().getAmount().doubleValue(), is(10d));
        assertThat(paymentAllocation.getPrincipalPaid().getAmount().doubleValue(), is(150d));
        assertThat(paymentAllocation.getFeePaid(fee1.getAccountFeesActionDetailId()).getAmount().doubleValue(), is(5d));
        assertThat(paymentAllocation.getFeePaid(fee2.getAccountFeesActionDetailId()).getAmount().doubleValue(), is(4d));
        assertThat(paymentAllocation.getFeePaid(fee3.getAccountFeesActionDetailId()).getAmount().doubleValue(), is(3d));
        assertThat(paymentAllocation.getFeePaid(fee4.getAccountFeesActionDetailId()).getAmount().doubleValue(), is(2d));
        assertThat(paymentAllocation.getFeePaid(fee5.getAccountFeesActionDetailId()).getAmount().doubleValue(), is(1d));
        assertThat(paymentAllocation.getFeePaid(fee6.getAccountFeesActionDetailId()).getAmount().doubleValue(), is(1d));
        assertThat(balance.getAmount().doubleValue(), is(819d));
        verify(accountBO).getCurrency();
    }

    @Test
    public void shouldPartiallyPayComponentsAndAllocatePayments() {
        loanScheduleEntity.setPrincipal(makeMoney(30));
        loanScheduleEntity.setPrincipalPaid(makeMoney(0));
        loanScheduleEntity.setInterest(makeMoney(20));
        loanScheduleEntity.setInterestPaid(makeMoney(0));
        loanScheduleEntity.setPenalty(makeMoney(10));
        loanScheduleEntity.setPenaltyPaid(makeMoney(0));
        loanScheduleEntity.setMiscFee(makeMoney(0));
        loanScheduleEntity.setMiscFeePaid(makeMoney(0));
        loanScheduleEntity.setMiscPenalty(makeMoney(0));
        loanScheduleEntity.setMiscPenaltyPaid(makeMoney(0));


        loanScheduleEntity.setAccount(accountBO);
        when(accountBO.getCurrency()).thenReturn(rupeeCurrency);

        Money paymentAmount = makeMoney(15d);
        Money balance = loanScheduleEntity.payComponents(paymentAmount);
        PaymentAllocation paymentAllocation = loanScheduleEntity.getPaymentAllocation();

        assertThat(paymentAllocation.getPenaltyPaid().getAmount().doubleValue(), is(10d));
        assertThat(paymentAllocation.getInterestPaid().getAmount().doubleValue(), is(5d));
        assertThat(paymentAllocation.getPrincipalPaid().getAmount().doubleValue(), is(0d));
        assertThat(balance.getAmount().doubleValue(), is(0d));
        verify(accountBO).getCurrency();
    }

    private LoanFeeScheduleEntity getFee(int id, double amount, double amountPaid) {
        LoanFeeScheduleEntity loanFeeScheduleEntity = new LoanFeeScheduleEntity();
        loanFeeScheduleEntity.setAccountFeesActionDetailId(id);
        loanFeeScheduleEntity.setFeeAmount(makeMoney(amount));
        loanFeeScheduleEntity.setFeeAmountPaid(makeMoney(amountPaid));
        return loanFeeScheduleEntity;
    }

    private Money makeMoney(double amount) {
        return new Money(rupeeCurrency, amount);
    }

}
