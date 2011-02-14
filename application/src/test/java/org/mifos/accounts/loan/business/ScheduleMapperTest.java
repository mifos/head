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

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.TestUtils.getDate;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.loan.business.matchers.LoanScheduleEntityMatcher;
import org.mifos.accounts.loan.persistance.LegacyLoanDao;
import org.mifos.accounts.loan.schedule.domain.Installment;
import org.mifos.accounts.loan.schedule.domain.InstallmentBuilder;
import org.mifos.accounts.loan.schedule.domain.Schedule;
import org.mifos.accounts.loan.schedule.domain.ScheduleMatcher;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.Transformer;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

// TODO: Test data instantiation inside this class needs improvement - johnvic/buddy
@RunWith(MockitoJUnitRunner.class)
public class ScheduleMapperTest {
    private MifosCurrency rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
    private static final Double DAILY_INTEREST_RATE = 0.000658;
    private static BigDecimal LOAN_AMOUNT = new BigDecimal(1000);
    private static final Date DISBURSEMENT_DATE = getDate(23, 9, 2010);
    private ScheduleMapper scheduleMapper;

    @Mock
    private LoanBO loanBO;
    @Mock
    private PersonnelBO personnelBO;
    @Mock
    private AccountPaymentEntity accountPaymentEntity;
    @Mock
    private LegacyLoanDao legacyLoanDao;
    @Mock
    private AccountActionEntity accountActionEntity;

    @Before
    public void setUp() {
        scheduleMapper = new ScheduleMapper();
        when(loanBO.getCurrency()).thenReturn(rupee);
    }

    @Test
    public void shouldMapLoanScheduleEntityToSchedule() {
        Collection<LoanScheduleEntity> loanScheduleEntities = getLoanScheduleEntities(getDate(24, 11, 2010));
        Schedule schedule = scheduleMapper.mapToSchedule(loanScheduleEntities, DISBURSEMENT_DATE, DAILY_INTEREST_RATE, LOAN_AMOUNT);
        assertThat(schedule, new ScheduleMatcher(getSchedule()));
    }

    @Test
    public void shouldMapScheduleToLoanScheduleEntity() throws PersistenceException {
        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(loanBO, null, null, null, null, null);
        LoanScheduleEntity scheduleEntityForPopulateTestInput = getLoanScheduleEntityForPopulateTestInput();
        Set<LoanScheduleEntity> loanScheduleEntities = new LinkedHashSet<LoanScheduleEntity>();
        loanScheduleEntities.add(scheduleEntityForPopulateTestInput);
        when(loanBO.getLoanScheduleEntities()).thenReturn(loanScheduleEntities);
        when(loanBO.getlegacyLoanDao()).thenReturn(legacyLoanDao);
        Date paymentDate = getDate(24, 11, 2010);
        Schedule schedule = getScheduleWithSingleInstallment();
        scheduleMapper.populatePaymentDetails(schedule, loanBO, paymentDate, personnelBO, accountPaymentEntity);
        assertCalculatedInterestOnPayment(accountPaymentEntity);
        assertThat(getLoanScheduleEntity(paymentDate), new LoanScheduleEntityMatcher(scheduleEntityForPopulateTestInput));
        verify(loanBO, times(1)).getLoanScheduleEntities();
        verify(loanBO, times(1)).getlegacyLoanDao();
        verify(loanBO, times(1)).recordSummaryAndPerfHistory(anyBoolean(), Matchers.<PaymentAllocation>any());
    }

    private void assertCalculatedInterestOnPayment(AccountPaymentEntity accountPaymentEntity) {
        List<AccountTrxnEntity> accountTrxns = new ArrayList<AccountTrxnEntity>(accountPaymentEntity.getAccountTrxns());
        assertThat(accountTrxns.size(), is(1));
        LoanTrxnDetailEntity loanTrxnDetailEntity = (LoanTrxnDetailEntity) accountTrxns.get(0);
        CalculatedInterestOnPayment calculatedInterestOnPayment = loanTrxnDetailEntity.getCalculatedInterestOnPayment();
        assertThat(calculatedInterestOnPayment, is(not(nullValue())));
        assertThat(calculatedInterestOnPayment.getOriginalInterest().getAmount().doubleValue(), is(10d));
        assertThat(calculatedInterestOnPayment.getExtraInterestPaid().getAmount().doubleValue(), is(4.5d));
        assertThat(calculatedInterestOnPayment.getInterestDueTillPaid().getAmount().doubleValue(), is(5d));
    }

    @Test
    public void shouldPopulateExtraInterestInLoanScheduleEntities() {
        List<Installment> installments = getInstallments(0, 0, 0);
        Schedule schedule = new Schedule(DISBURSEMENT_DATE, DAILY_INTEREST_RATE, LOAN_AMOUNT, installments);
        for (int i = 0; i < installments.size(); i++) {
            installments.get(i).setExtraInterest(new BigDecimal(i));
        }
        Collection<LoanScheduleEntity> loanScheduleEntities = getLoanScheduleEntities(getDate(24, 11, 2010));
        Map<Integer, LoanScheduleEntity> loanScheduleEntityMap = getLoanScheduleEntityMap(loanScheduleEntities);
        scheduleMapper.populateExtraInterestInLoanScheduleEntities(schedule, loanScheduleEntityMap);
        assertExtraInterest(loanScheduleEntityMap.get(1), 0d);
        assertExtraInterest(loanScheduleEntityMap.get(2), 1d);
        assertExtraInterest(loanScheduleEntityMap.get(3), 2d);
    }

    private Collection<LoanScheduleEntity> getLoanScheduleEntities(Date paymentDate) {
        LoanScheduleEntity loanScheduleEntity1 = new LoanScheduleBuilder("1", loanBO).
                withDueDate(getDate(23, 10, 2010)).withPaymentStatus(PaymentStatus.PAID).
                withPaymentDate(paymentDate).
                withPrincipal(100).withPrincipalPaid(100).
                withInterest(10).withInterestPaid(10).
                withExtraInterest(9).withExtraInterestPaid(9).
                withMiscFees(8).withMiscFeesPaid(8).
                withPenalty(7).withPenaltyPaid(7).
                withMiscPenalty(6).withMiscPenaltyPaid(6).
                addFees(1, 5, 5).build();
        LoanScheduleEntity loanScheduleEntity2 = new LoanScheduleBuilder("2", loanBO).
                withDueDate(getDate(23, 11, 2010)).withPaymentStatus(PaymentStatus.UNPAID).
                withPaymentDate(paymentDate).
                withPrincipal(1000).withPrincipalPaid(100).
                withInterest(100).withInterestPaid(10).
                withExtraInterest(90).withExtraInterestPaid(9).
                withMiscFees(80).withMiscFeesPaid(8).
                withPenalty(70).withPenaltyPaid(7).
                withMiscPenalty(60).withMiscPenaltyPaid(6).
                addFees(1, 50, 5).build();
        LoanScheduleEntity loanScheduleEntity3 = new LoanScheduleBuilder("3", loanBO).
                withDueDate(getDate(23, 12, 2010)).withPaymentStatus(PaymentStatus.UNPAID).
                withPrincipal(1000).withPrincipalPaid(0).
                withInterest(100).withInterestPaid(0).
                withExtraInterest(90).withExtraInterestPaid(0).
                withMiscFees(80).withMiscFeesPaid(0).
                withPenalty(70).withPenaltyPaid(0).
                withMiscPenalty(60).withMiscPenaltyPaid(0).
                addFees(1, 50, 0).build();
        return Arrays.asList(loanScheduleEntity1, loanScheduleEntity2, loanScheduleEntity3);
    }

    private LoanScheduleEntity getLoanScheduleEntity(Date paymentDate) {
        return new LoanScheduleBuilder("1", loanBO).
                withDueDate(getDate(23, 10, 2010)).withPaymentStatus(PaymentStatus.PAID).
                withPaymentDate(paymentDate).
                withPrincipal(100).withPrincipalPaid(100).
                withInterest(10).withInterestPaid(10).
                withExtraInterest(9).withExtraInterestPaid(9).
                withMiscFees(8).withMiscFeesPaid(8).
                withPenalty(7).withPenaltyPaid(7).
                withMiscPenalty(6).withMiscPenaltyPaid(6).
                addFees(1, 5, 5).build();
    }

    private LoanScheduleEntity getLoanScheduleEntityForPopulateTestInput() {
        return new LoanScheduleBuilder("1", loanBO).
                withDueDate(getDate(23, 10, 2010)).
                withPaymentDate(getDate(10, 11, 2010)).
                withPaymentStatus(PaymentStatus.UNPAID).
                withPrincipal(100).withPrincipalPaid(50).
                withInterest(10).withInterestPaid(5).
                withExtraInterest(9).withExtraInterestPaid(4.5).
                withMiscFees(8).withMiscFeesPaid(4).
                withPenalty(7).withPenaltyPaid(3.5).
                withMiscPenalty(6).withMiscPenaltyPaid(3).
                addFees(1, 5, 4).build();
    }

    public Schedule getSchedule() {
        Installment installment1 = new InstallmentBuilder("1").
                withDueDate(getDate(23, 10, 2010)).
                withPrincipal(100).withPrincipalPaid(100).
                withInterest(10).withInterestPaid(10).
                withExtraInterest(9).withExtraInterestPaid(9).
                withMiscFees(8).withMiscFeesPaid(8).
                withPenalty(7).withPenaltyPaid(7).
                withMiscPenalty(6).withMiscPenaltyPaid(6).
                withFees(5).withFeesPaid(5).
                build();
        Installment installment2 = new InstallmentBuilder("2").
                withDueDate(getDate(23, 11, 2010)).
                withPrincipal(1000).withPrincipalPaid(100).
                withInterest(100).withInterestPaid(10).
                withExtraInterest(90).withExtraInterestPaid(9).
                withMiscFees(80).withMiscFeesPaid(8).
                withPenalty(70).withPenaltyPaid(7).
                withMiscPenalty(60).withMiscPenaltyPaid(6).
                withFees(50).withFeesPaid(5).
                build();
        Installment installment3 = new InstallmentBuilder("3").
                withDueDate(getDate(23, 12, 2010)).
                withPrincipal(1000).withPrincipalPaid(0).
                withInterest(100).withInterestPaid(0).
                withExtraInterest(90).withExtraInterestPaid(0).
                withMiscFees(80).withMiscFeesPaid(0).
                withPenalty(70).withPenaltyPaid(0).
                withMiscPenalty(60).withMiscPenaltyPaid(0).
                withFees(50).withFeesPaid(0).
                build();
        List<Installment> installments = Arrays.asList(installment1, installment2, installment3);
        return new Schedule(DISBURSEMENT_DATE, DAILY_INTEREST_RATE, LOAN_AMOUNT, installments);
    }

    public Schedule getScheduleWithSingleInstallment() {
        Installment installment1 = new InstallmentBuilder("1").
                withDueDate(getDate(23, 10, 2010)).
                withPrincipal(100).withPrincipalPaid(50).
                withInterest(10).withInterestPaid(5).
                withExtraInterest(9).withExtraInterestPaid(4.5).
                withMiscFees(8).withMiscFeesPaid(4).
                withPenalty(7).withPenaltyPaid(3.5).
                withMiscPenalty(6).withMiscPenaltyPaid(3).
                withFees(5).withFeesPaid(2.5).
                build();
        return new Schedule(DISBURSEMENT_DATE, DAILY_INTEREST_RATE, LOAN_AMOUNT, Arrays.asList(installment1));
    }

    private void assertExtraInterest(LoanScheduleEntity loanScheduleEntity, double extraInterest) {
        assertThat(loanScheduleEntity.getExtraInterest().getAmount().doubleValue(), is(extraInterest));
    }

    private List<Installment> getInstallments(double... extraInterest) {
        Installment installment1 = new InstallmentBuilder("1").withDueDate(getDate(23, 10, 2010)).withPrincipal(100)
                .withInterest(10).withExtraInterest(extraInterest[0]).build();
        Installment installment2 = new InstallmentBuilder("2").withDueDate(getDate(23, 10, 2010)).withPrincipal(100)
                .withInterest(10).withExtraInterest(extraInterest[1]).build();
        Installment installment3 = new InstallmentBuilder("3").withDueDate(getDate(23, 12, 2010)).withPrincipal(100)
                .withInterest(10).withExtraInterest(extraInterest[2]).build();
        return asList(installment1, installment2, installment3);
    }


    private Map<Integer, LoanScheduleEntity> getLoanScheduleEntityMap(Collection<LoanScheduleEntity> loanScheduleEntities) {
        return CollectionUtils.asValueMap(loanScheduleEntities, new Transformer<LoanScheduleEntity, Integer>() {
            @Override
            public Integer transform(LoanScheduleEntity input) {
                return Integer.valueOf(input.getInstallmentId());
            }
        });
    }

    // TODO: This matcher currently verifies only the CalculatedInterestOnPayment details. Needs extension to cover other fields !!!
    private class LoanTrxnDetailEntityMatcher extends TypeSafeMatcher<LoanTrxnDetailEntity> {
        private double originalInterest;
        private double extraInterestPaid;

        public LoanTrxnDetailEntityMatcher(double originalInterest, double extraInterestPaid) {
            this.originalInterest = originalInterest;
            this.extraInterestPaid = extraInterestPaid;
        }

        @Override
        public boolean matchesSafely(LoanTrxnDetailEntity loanTrxnDetailEntity) {
            CalculatedInterestOnPayment calculatedInterestOnPayment = loanTrxnDetailEntity.getCalculatedInterestOnPayment();
            return calculatedInterestOnPayment != null
                    && calculatedInterestOnPayment.getOriginalInterest().getAmount().doubleValue() == originalInterest
                    && calculatedInterestOnPayment.getExtraInterestPaid().getAmount().doubleValue() == originalInterest;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("LoanTrxnDetailEntity did not match");
        }
    }
}


