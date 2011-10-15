/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.TestUtils.RUPEE;
import static org.mifos.framework.TestUtils.getDate;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.loan.persistance.LegacyLoanDao;
import org.mifos.accounts.loan.schedule.domain.Installment;
import org.mifos.accounts.loan.schedule.domain.InstallmentBuilder;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoanScheduleEntityTest {
    @Mock
    private LoanBO loanBO;
    @Mock
    private PersonnelBO personnel;
    @Mock
    private AccountPaymentEntity accountPayment;
    @Mock
    private LegacyLoanDao legacyLoanDao;
    @Mock
    private AccountPaymentEntity accountPaymentEntity;

    private LoanScheduleEntity loanScheduleEntity;
    private Date paymentDate;

    @Before
    public void setUp() {
        loanScheduleEntity = new LoanScheduleEntity();
        paymentDate = TestUtils.getDate(12, 1, 2010);
    }

    @Test
    public void shouldRecordPaymentOnPayOff() {
        loanScheduleEntity = new LoanScheduleEntity() {
            @Override
            public Money getTotalDueWithFees() {
                return Money.zero(RUPEE);
            }
        };
        loanScheduleEntity.recordPayment(paymentDate);
        assertEquals(loanScheduleEntity.getPaymentDate(), paymentDate);
        assertEquals(loanScheduleEntity.getPaymentStatus(), PaymentStatus.PAID.getValue());
    }

    @Test
    public void shouldRecordPaymentOnPartPay() {
        loanScheduleEntity = new LoanScheduleEntity() {
            @Override
            public Money getTotalDueWithFees() {
                return new Money(RUPEE, 10.0);
            }
        };
        loanScheduleEntity.recordPayment(paymentDate);
        assertEquals(loanScheduleEntity.getPaymentDate(), paymentDate);
        assertEquals(loanScheduleEntity.getPaymentStatus(), PaymentStatus.UNPAID.getValue());
    }

    // TODO: Use a AccountTrxnEntityMatcher to verify the allocation totals
    @Test
    public void shouldUpdateLoanSummaryAndPerformanceHistoryOnPayOff() {
        PaymentAllocation paymentAllocation = new PaymentAllocation(RUPEE);
        loanScheduleEntity = new LoanScheduleEntity() {
            @Override
            public boolean isPaid() {
                return true;
            }
        };
        loanScheduleEntity.setAccount(loanBO);
        loanScheduleEntity.setPaymentAllocation(paymentAllocation);
        Mockito.doNothing().when(accountPayment).addAccountTrxn(Mockito.<AccountTrxnEntity>any());
        when(loanBO.getlegacyLoanDao()).thenReturn(legacyLoanDao);
        when(accountPayment.getAccount()).thenReturn(loanBO);
        loanScheduleEntity.updateSummaryAndPerformanceHistory(accountPayment, personnel, paymentDate);
        verify(accountPayment, times(1)).addAccountTrxn(Mockito.<AccountTrxnEntity>any());
        verify(loanBO, times(1)).getlegacyLoanDao();
        verify(accountPayment, times(1)).getAccount();
        verify(loanBO, times(1)).recordSummaryAndPerfHistory(true, paymentAllocation);
    }

    @Test
    public void shouldUpdateLoanSummaryAndNotPerformanceHistoryOnPartPay() {
        PaymentAllocation paymentAllocation = new PaymentAllocation(RUPEE);
        loanScheduleEntity = new LoanScheduleEntity() {
            @Override
            public boolean isPaid() {
                return false;
            }
        };
        loanScheduleEntity.setAccount(loanBO);
        loanScheduleEntity.setPaymentAllocation(paymentAllocation);
        Mockito.doNothing().when(accountPayment).addAccountTrxn(Mockito.<AccountTrxnEntity>any());
        when(loanBO.getlegacyLoanDao()).thenReturn(legacyLoanDao);
        when(accountPayment.getAccount()).thenReturn(loanBO);
        loanScheduleEntity.updateSummaryAndPerformanceHistory(accountPayment, personnel, paymentDate);
        verify(accountPayment, times(1)).addAccountTrxn(Mockito.<AccountTrxnEntity>any());
        verify(loanBO, times(1)).getlegacyLoanDao();
        verify(accountPayment, times(1)).getAccount();
        verify(loanBO, times(1)).recordSummaryAndPerfHistory(false, paymentAllocation);
    }

    @Test
    public void shouldPayComponents() {
        loanScheduleEntity = new LoanScheduleEntity() {
            @Override
            public Money getTotalDueWithFees() {
                return Money.zero(RUPEE);
            }
        };
        when(loanBO.getCurrency()).thenReturn(RUPEE);
        loanScheduleEntity.setAccount(loanBO);
        loanScheduleEntity.setPrincipalPaid(Money.zero(RUPEE));
        loanScheduleEntity.setInterestPaid(Money.zero(RUPEE));
        loanScheduleEntity.setExtraInterestPaid(Money.zero(RUPEE));
        loanScheduleEntity.setPenaltyPaid(Money.zero(RUPEE));
        loanScheduleEntity.setMiscPenaltyPaid(Money.zero(RUPEE));
        loanScheduleEntity.setMiscFeePaid(Money.zero(RUPEE));
        AccountFeesActionDetailEntity feesActionDetailEntity = new LoanFeeScheduleEntity(loanScheduleEntity,
                mock(FeeBO.class), mock(AccountFeesEntity.class), new Money(RUPEE, 100d));
        feesActionDetailEntity.setAccountFeesActionDetailId(1);
        loanScheduleEntity.addAccountFeesAction(feesActionDetailEntity);
        loanScheduleEntity.payComponents(getInstallment(), RUPEE, paymentDate);
        PaymentAllocation paymentAllocation = loanScheduleEntity.getPaymentAllocation();
        assertEquals(new Money(RUPEE, 90.0), paymentAllocation.getPrincipalPaid());
        assertEquals(new Money(RUPEE, 80.0), paymentAllocation.getInterestPaid());
        assertEquals(new Money(RUPEE, 70.0), paymentAllocation.getExtraInterestPaid());
        assertEquals(new Money(RUPEE, 60.0), paymentAllocation.getTotalFeesPaid());
        assertEquals(new Money(RUPEE, 50.0), paymentAllocation.getMiscFeePaid());
        assertEquals(new Money(RUPEE, 40.0), paymentAllocation.getPenaltyPaid());
        assertEquals(new Money(RUPEE, 30.0), paymentAllocation.getMiscPenaltyPaid());
        assertEquals(new Money(RUPEE, 100.0), loanScheduleEntity.getExtraInterest());
        assertEquals(new Money(RUPEE, 100.0), loanScheduleEntity.getInterest());
    }

    private Installment getInstallment() {
        return new InstallmentBuilder("1").
                withDueDate(getDate(23, 10, 2010)).
                withPrincipal(100).withPrincipalPaid(90).
                withInterest(100).withInterestPaid(80).
                withExtraInterest(100).withExtraInterestPaid(70).
                withMiscFees(100).withMiscFeesPaid(50).
                withPenalty(100).withPenaltyPaid(40).
                withMiscPenalty(100).withMiscPenaltyPaid(30).
                withFees(100).withFeesPaid(60).
                build();
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

        loanScheduleEntity.setAccount(loanBO);
        when(loanBO.getCurrency()).thenReturn(RUPEE);

        Money paymentAmount = makeMoney(1000d);
        Money balance = loanScheduleEntity.payComponents(paymentAmount, paymentDate);
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
        verify(loanBO, atLeastOnce()).getCurrency();
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
        loanScheduleEntity.setAccount(loanBO);
        when(loanBO.getCurrency()).thenReturn(RUPEE);

        Money paymentAmount = makeMoney(15d);
        Money balance = loanScheduleEntity.payComponents(paymentAmount, paymentDate);
        PaymentAllocation paymentAllocation = loanScheduleEntity.getPaymentAllocation();

        assertThat(paymentAllocation.getPenaltyPaid().getAmount().doubleValue(), is(10d));
        assertThat(paymentAllocation.getInterestPaid().getAmount().doubleValue(), is(5d));
        assertThat(paymentAllocation.getPrincipalPaid().getAmount().doubleValue(), is(0d));
        assertThat(balance.getAmount().doubleValue(), is(0d));
        verify(loanBO, atLeastOnce()).getCurrency();
    }

    @Test
    public void shouldDetermineWhetherPaymentApplied() {
        when(loanBO.getCurrency()).thenReturn(RUPEE);
        loanScheduleEntity.setAccount(loanBO);
        loanScheduleEntity.setPrincipalPaid(makeMoney(0));
        loanScheduleEntity.setInterestPaid(makeMoney(0));
        loanScheduleEntity.setPenaltyPaid(makeMoney(0));
        loanScheduleEntity.setMiscFeePaid(makeMoney(0));
        loanScheduleEntity.setMiscPenaltyPaid(makeMoney(0));
        loanScheduleEntity.setExtraInterestPaid(makeMoney(10));
        assertThat(loanScheduleEntity.isPaymentApplied(), is(true));
        verify(loanBO).getCurrency();
    }

    @Test
    public void shouldPopulateComputedInterestForPAWDEPAdjustment() {
        loanScheduleEntity.setAccount(loanBO);
        when(loanBO.getCurrency()).thenReturn(RUPEE);
        when(loanBO.isDecliningBalanceInterestRecalculation()).thenReturn(true);
        when(accountPayment.getAccount()).thenReturn(loanBO);
        LoanTrxnDetailEntity loanReverseTrxn = new LoanTrxnDetailEntity(accountPayment,
                AccountActionTypes.LOAN_ADJUSTMENT, Short.valueOf("1"), paymentDate,
                personnel, paymentDate,
                makeMoney(280), "test for loan adjustment", null, makeMoney(-14.06),
                makeMoney(-3.3), makeMoney(0), makeMoney(0), makeMoney(0), null);
        CalculatedInterestOnPayment interestOnPayment = new CalculatedInterestOnPayment();
        interestOnPayment.setExtraInterestPaid(makeMoney(0.8d));
        interestOnPayment.setOriginalInterest(makeMoney(14.96d));
        interestOnPayment.setInterestDueTillPaid(makeMoney(2.5d));
        loanReverseTrxn.setCalculatedInterestOnPayment(interestOnPayment);
        loanScheduleEntity.setPrincipalPaid(makeMoney(14.06d));
        loanScheduleEntity.setInterestPaid(makeMoney(2.5d));
        loanScheduleEntity.setExtraInterestPaid(makeMoney(0.8d));
        loanScheduleEntity.setPenaltyPaid(makeMoney(0d));
        loanScheduleEntity.setMiscPenaltyPaid(makeMoney(0d));
        loanScheduleEntity.setMiscFeePaid(makeMoney(0d));
        loanScheduleEntity.updatePaymentDetailsForAdjustment(loanReverseTrxn);
        assertThat(loanScheduleEntity.getPrincipalPaidAsDouble(), is(0d));
        assertThat(loanScheduleEntity.getInterest().getAmount().doubleValue(), is(14.96d));
        assertThat(loanScheduleEntity.getInterestPaidAsDouble(), is(0d));
        assertThat(loanScheduleEntity.getExtraInterestPaidAsDouble(), is(0d));
    }

    @Test
    public void shouldNotPopulateComputedInterestForPAWDEPAdjustmentForFutureInstallment() {
        loanScheduleEntity.setAccount(loanBO);
        when(loanBO.getCurrency()).thenReturn(RUPEE);
        when(loanBO.isDecliningBalanceInterestRecalculation()).thenReturn(true);
        when(accountPayment.getAccount()).thenReturn(loanBO);
        LoanTrxnDetailEntity loanReverseTrxn = new LoanTrxnDetailEntity(accountPayment,
                AccountActionTypes.LOAN_ADJUSTMENT, Short.valueOf("1"), paymentDate,
                personnel, paymentDate,
                makeMoney(280), "test for loan adjustment", null, makeMoney(-14.06),
                makeMoney(-2.5), makeMoney(0), makeMoney(0), makeMoney(0), null);
        loanScheduleEntity.setInterest(makeMoney(14.96d));
        loanScheduleEntity.setPrincipalPaid(makeMoney(14.06d));
        loanScheduleEntity.setInterestPaid(makeMoney(2.5d));
        loanScheduleEntity.setExtraInterestPaid(makeMoney(0d));
        loanScheduleEntity.setPenaltyPaid(makeMoney(0d));
        loanScheduleEntity.setMiscPenaltyPaid(makeMoney(0d));
        loanScheduleEntity.setMiscFeePaid(makeMoney(0d));
        loanScheduleEntity.updatePaymentDetailsForAdjustment(loanReverseTrxn);
        assertThat(loanScheduleEntity.getPrincipalPaidAsDouble(), is(0d));
        assertThat(loanScheduleEntity.getInterest().getAmount().doubleValue(), is(14.96d));
        assertThat(loanScheduleEntity.getInterestPaidAsDouble(), is(0d));
        assertThat(loanScheduleEntity.getExtraInterestPaidAsDouble(), is(0d));
    }

    @Test
    public void shouldPopulateComputedInterestForNormalAdjustment() {
        loanScheduleEntity.setAccount(loanBO);
        when(loanBO.getCurrency()).thenReturn(RUPEE);
        when(loanBO.isDecliningBalanceInterestRecalculation()).thenReturn(false);
        when(accountPayment.getAccount()).thenReturn(loanBO);
        LoanTrxnDetailEntity loanReverseTrxn = new LoanTrxnDetailEntity(accountPayment,
                AccountActionTypes.LOAN_ADJUSTMENT, Short.valueOf("1"), paymentDate,
                personnel, paymentDate,
                makeMoney(280), "test for loan adjustment", null, makeMoney(-14.06),
                makeMoney(-2.5), makeMoney(0), makeMoney(0), makeMoney(0), null);
        loanScheduleEntity.setInterest(makeMoney(14.96d));
        loanScheduleEntity.setPrincipalPaid(makeMoney(14.06d));
        loanScheduleEntity.setInterestPaid(makeMoney(2.5d));
        loanScheduleEntity.setExtraInterestPaid(makeMoney(0d));
        loanScheduleEntity.setPenaltyPaid(makeMoney(0d));
        loanScheduleEntity.setMiscPenaltyPaid(makeMoney(0d));
        loanScheduleEntity.setMiscFeePaid(makeMoney(0d));
        loanScheduleEntity.updatePaymentDetailsForAdjustment(loanReverseTrxn);
        assertThat(loanScheduleEntity.getPrincipalPaidAsDouble(), is(0d));
        assertThat(loanScheduleEntity.getInterest().getAmount().doubleValue(), is(14.96d));
        assertThat(loanScheduleEntity.getInterestPaidAsDouble(), is(0d));
        assertThat(loanScheduleEntity.getExtraInterestPaidAsDouble(), is(0d));
    }

    @Test
    public void testRecordAdjustmentSinglePaymentExists() {
        loanScheduleEntity.setAccount(loanBO);
        when(loanBO.getLastPmntToBeAdjusted()).thenReturn(null);
        loanScheduleEntity.recordForAdjustment();
        Assert.assertEquals(PaymentStatus.UNPAID, loanScheduleEntity.getPaymentStatusAsEnum());
        Assert.assertNull(loanScheduleEntity.getPaymentDate());
        verify(loanBO, Mockito.times(1)).getLastPmntToBeAdjusted();
    }

    @Test
    public void testRecordAdjustmentWhenMultiplePaymentsExist() {
        loanScheduleEntity.setAccount(loanBO);
        when(loanBO.getLastPmntToBeAdjusted()).thenReturn(accountPaymentEntity);
        Date dateOfPaymentPriorToAdjustedOne = new Date();
        when(accountPaymentEntity.getPaymentDate()).thenReturn(dateOfPaymentPriorToAdjustedOne);
        loanScheduleEntity.recordForAdjustment();
        Assert.assertEquals(PaymentStatus.UNPAID, loanScheduleEntity.getPaymentStatusAsEnum());
        Assert.assertEquals(dateOfPaymentPriorToAdjustedOne, loanScheduleEntity.getPaymentDate());
        verify(accountPaymentEntity, Mockito.times(1)).getPaymentDate();
        verify(loanBO, Mockito.times(1)).getLastPmntToBeAdjusted();
    }


    private LoanFeeScheduleEntity getFee(int id, double amount, double amountPaid) {
        LoanFeeScheduleEntity loanFeeScheduleEntity = new LoanFeeScheduleEntity();
        loanFeeScheduleEntity.setAccountFeesActionDetailId(id);
        loanFeeScheduleEntity.setFeeAmount(makeMoney(amount));
        loanFeeScheduleEntity.setFeeAmountPaid(makeMoney(amountPaid));
        return loanFeeScheduleEntity;
    }

    private Money makeMoney(double amount) {
        return new Money(RUPEE, amount);
    }

}
