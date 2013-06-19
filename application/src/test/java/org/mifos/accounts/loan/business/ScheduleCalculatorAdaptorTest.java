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

import static java.math.BigDecimal.valueOf;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.TestUtils.getDate;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.loan.persistance.LegacyLoanDao;
import org.mifos.accounts.loan.schedule.calculation.ScheduleCalculator;
import org.mifos.accounts.loan.schedule.domain.Installment;
import org.mifos.accounts.loan.schedule.domain.InstallmentBuilder;
import org.mifos.accounts.loan.schedule.domain.Schedule;
import org.mifos.accounts.loan.schedule.domain.ScheduleMatcher;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.Transformer;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleCalculatorAdaptorTest {

	@Mock
	private ScheduleCalculatorAdaptor scheduleCalculatorAdaptor;
	
    @Mock
    private ScheduleMapper scheduleMapper;

    @Mock
    private LoanBO loanBO;

    @Mock
    private PersonnelBO personnel;

    @Mock
    private AccountPaymentEntity accountPaymentEntity;

    @Mock
    private LegacyLoanDao legacyLoanDao;

    @Mock
    private AccountActionEntity accountActionEntity;

    @Mock
    private LoanSummaryEntity loanSummary;

    @Mock
    private LoanPerformanceHistoryEntity performanceHistory;

    @Mock
    private ConfigurationBusinessService configurationBusinessService;
    
    @Mock
    private ScheduleCalculator scheduleCalculator;
    
    private MifosCurrency rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", valueOf(1), "INR");
    private static final Double DAILY_INTEREST_RATE = 0.000658;
    private static final Double ANNUAL_INTEREST_RATE = 24.0;
    private static BigDecimal LOAN_AMOUNT = new BigDecimal(1000);
    private static final Date DISBURSEMENT_DATE = getDate(23, 9, 2010);

    @Before
    public void setup() {
        scheduleCalculator = Mockito.spy(new ScheduleCalculator());
        scheduleMapper = Mockito.spy(new ScheduleMapper());
        scheduleCalculatorAdaptor = new ScheduleCalculatorAdaptor(scheduleCalculator, scheduleMapper, configurationBusinessService);
        LOAN_AMOUNT = LOAN_AMOUNT.setScale(13, RoundingMode.HALF_UP);
        when(loanBO.getCurrency()).thenReturn(rupee);
    }

    @Test
    public void shouldApplyPaymentZeroPayment() throws PersistenceException {
        Set<LoanScheduleEntity> loanScheduleEntities = getLoanScheduleEntities();
        when(loanBO.getLoanScheduleEntities()).thenReturn(loanScheduleEntities);
        when(loanBO.getDisbursementDate()).thenReturn(DISBURSEMENT_DATE);
        when(loanBO.getLoanAmount()).thenReturn(new Money(rupee, LOAN_AMOUNT));
        when(loanBO.getInterestRate()).thenReturn(ANNUAL_INTEREST_RATE);
        when(loanBO.getlegacyLoanDao()).thenReturn(legacyLoanDao);
        when(loanBO.getLoanSummary()).thenReturn(loanSummary);
        when(loanBO.getPerformanceHistory()).thenReturn(performanceHistory);
        when(accountPaymentEntity.getAccount()).thenReturn(loanBO);
        scheduleCalculatorAdaptor.applyPayment(loanBO, Money.zero(rupee), getDate(30, 10, 2010), personnel, accountPaymentEntity, false);
        verify(scheduleMapper, times(1)).mapToSchedule(Mockito.<Collection<LoanScheduleEntity>>any(), Mockito.<Date>any(), Mockito.<Double>any(), Mockito.<BigDecimal>any());
        verify(scheduleCalculator).applyPayment(Mockito.<Schedule>any(), Mockito.<BigDecimal>any(), Mockito.<Date>any(), Mockito.anyBoolean());
        verify(scheduleMapper).populatePaymentDetails(Mockito.<Schedule>any(), Mockito.<LoanBO>any(), Mockito.<Date>any(), Mockito.<PersonnelBO>any(), Mockito.<AccountPaymentEntity>any());
        verify(loanBO, times(2)).getLoanScheduleEntities();
        verify(loanBO, times(1)).getDisbursementDate();
        verify(loanBO, times(1)).getInterestRate();
        verify(loanBO, times(0)).getlegacyLoanDao();
        verify(loanBO, times(0)).getLoanSummary();
        verify(loanBO, times(0)).getPerformanceHistory();
        verify(accountPaymentEntity, times(0)).getAccount();
        verify(legacyLoanDao, times(0)).getPersistentObject(eq(AccountActionEntity.class), Mockito.<Serializable>any());
        verify(loanSummary, times(0)).updatePaymentDetails(Mockito.<PaymentAllocation>any());
        verify(performanceHistory, times(0)).incrementPayments();
    }

    @Test
    public void shouldApplyPayment() throws PersistenceException {
        Set<LoanScheduleEntity> loanScheduleEntities = getLoanScheduleEntities();
        when(loanBO.getLoanScheduleEntities()).thenReturn(loanScheduleEntities);
        when(loanBO.getDisbursementDate()).thenReturn(DISBURSEMENT_DATE);
        when(loanBO.getLoanAmount()).thenReturn(new Money(rupee, LOAN_AMOUNT));
        when(loanBO.getInterestRate()).thenReturn(ANNUAL_INTEREST_RATE);
        when(loanBO.getlegacyLoanDao()).thenReturn(legacyLoanDao);
        when(accountPaymentEntity.getAccount()).thenReturn(loanBO);
        scheduleCalculatorAdaptor.applyPayment(loanBO, new Money(rupee, 112.00), getDate(30, 10, 2010), personnel, accountPaymentEntity, false);
        verify(scheduleMapper, times(1)).mapToSchedule(Mockito.<Collection<LoanScheduleEntity>>any(), Mockito.<Date>any(), Mockito.<Double>any(), Mockito.<BigDecimal>any());
        verify(scheduleCalculator).applyPayment(Mockito.<Schedule>any(), Mockito.<BigDecimal>any(), Mockito.<Date>any(), Mockito.anyBoolean());
        verify(scheduleMapper).populatePaymentDetails(Mockito.<Schedule>any(), Mockito.<LoanBO>any(), Mockito.<Date>any(), Mockito.<PersonnelBO>any(), Mockito.<AccountPaymentEntity>any());
        verify(loanBO, times(2)).getLoanScheduleEntities();
        verify(loanBO, times(1)).getDisbursementDate();
        verify(loanBO, times(1)).getInterestRate();
        verify(loanBO, times(2)).getlegacyLoanDao();
        verify(loanBO, times(2)).recordSummaryAndPerfHistory(anyBoolean(), Matchers.<PaymentAllocation>any());
        verify(accountPaymentEntity, times(2)).getAccount();
    }

    @Test
    public void shouldComputeExtraInterestForDecliningPrincipalBalance() {
        Set<LoanScheduleEntity> loanScheduleEntities = getLoanScheduleEntities();
        when(loanBO.isDecliningBalanceInterestRecalculation()).thenReturn(true);
        when(loanBO.getLoanScheduleEntities()).thenReturn(loanScheduleEntities);
        when(loanBO.getDisbursementDate()).thenReturn(DISBURSEMENT_DATE);
        when(loanBO.getLoanAmount()).thenReturn(new Money(rupee, LOAN_AMOUNT));
        when(loanBO.getInterestRate()).thenReturn(ANNUAL_INTEREST_RATE);
     	when(loanBO.getLoanScheduleEntityMap()).thenReturn(getLoanScheduleEntityMap(loanScheduleEntities));
        
        scheduleCalculatorAdaptor.computeExtraInterest(loanBO, getDate(30, 10, 2010));
        
        Schedule expectedSchedule = getSchedule(DISBURSEMENT_DATE, LOAN_AMOUNT, getInstallments(0, .46, 0));
        verify(scheduleCalculator).computeExtraInterest(argThat(new ScheduleMatcher(expectedSchedule)), Mockito.eq(getDate(30, 10, 2010)));

        verify(loanBO, times(1)).isDecliningBalanceInterestRecalculation();
        verify(loanBO, times(1)).getLoanScheduleEntities();
        verify(loanBO, times(1)).getDisbursementDate();
        verify(loanBO, times(1)).getLoanAmount();
        verify(loanBO, times(1)).getInterestRate();
        verify(loanBO, times(1)).getLoanScheduleEntityMap();

        ArrayList<LoanScheduleEntity> loanScheduleEntitiesWithExtraInterest = new ArrayList<LoanScheduleEntity>(loanBO.getLoanScheduleEntities());
        assertExtraInterest(loanScheduleEntitiesWithExtraInterest.get(0), 0.0);
        assertExtraInterest(loanScheduleEntitiesWithExtraInterest.get(1), 0.46);
        assertExtraInterest(loanScheduleEntitiesWithExtraInterest.get(2), 0.0);
    }

    @Test
    public void shouldNotComputeExtraInterestForNonPrincipalBalanceInterestTypes() {
        Set<LoanScheduleEntity> loanScheduleEntities = getLoanScheduleEntities();
        when(loanBO.isDecliningBalanceInterestRecalculation()).thenReturn(false);
        when(loanBO.getLoanScheduleEntities()).thenReturn(loanScheduleEntities);
        when(loanBO.getDisbursementDate()).thenReturn(DISBURSEMENT_DATE);
        when(loanBO.getLoanAmount()).thenReturn(new Money(rupee, LOAN_AMOUNT));
        when(loanBO.getInterestRate()).thenReturn(ANNUAL_INTEREST_RATE);
        when(loanBO.getLoanScheduleEntityMap()).thenReturn(getLoanScheduleEntityMap(loanScheduleEntities));

        scheduleCalculatorAdaptor.computeExtraInterest(loanBO, getDate(30, 10, 2010));

        verify(scheduleCalculator, times(0)).computeExtraInterest(Mockito.<Schedule>any(), Mockito.<Date>any());
        verify(loanBO, times(0)).getInterestRate();
        verify(loanBO, times(0)).getLoanScheduleEntities();
        verify(loanBO, times(0)).getDisbursementDate();
        verify(loanBO, times(0)).getLoanAmount();
        verify(loanBO, times(0)).getInterestRate();
        verify(loanBO, times(0)).getLoanScheduleEntityMap();

        ArrayList<LoanScheduleEntity> loanScheduleEntitiesWithExtraInterest = new ArrayList<LoanScheduleEntity>(loanBO.getLoanScheduleEntities());
        assertExtraInterest(loanScheduleEntitiesWithExtraInterest.get(0), 0.0);
        assertExtraInterest(loanScheduleEntitiesWithExtraInterest.get(1), 0.0);
        assertExtraInterest(loanScheduleEntitiesWithExtraInterest.get(2), 0.0);
    }

    @Test
    public void computeExtraInterestAndPopulateInLoanScheduleEntities() {
        List<Installment> installments = getInstallments(0, 0, 0);
        Schedule schedule = new Schedule(DISBURSEMENT_DATE, DAILY_INTEREST_RATE, LOAN_AMOUNT, installments);
        new ScheduleCalculator().computeExtraInterest(schedule, getDate(30, 10, 2010));
        Set<LoanScheduleEntity> loanScheduleEntities = getLoanScheduleEntities();
        Map<Integer, LoanScheduleEntity> loanScheduleEntityMap = getLoanScheduleEntityMap(loanScheduleEntities);
        assertThat(schedule.getInstallments().get(2).getExtraInterest().doubleValue(), is(0.46));
        scheduleCalculatorAdaptor.populateExtraInterestInLoanScheduleEntities(schedule, loanScheduleEntityMap);
        for (Installment installment : installments) {
            LoanScheduleEntity loanScheduleEntity = loanScheduleEntityMap.get(installment.getId());
            assertExtraInterest(loanScheduleEntity, installment.getExtraInterest().doubleValue());
        }
    }

    @Test
    public void shouldComputeRepaymentAmount() {
        Set<LoanScheduleEntity> loanScheduleEntities = getLoanScheduleEntities();
        when(loanBO.getLoanScheduleEntities()).thenReturn(loanScheduleEntities);
        when(loanBO.getDisbursementDate()).thenReturn(DISBURSEMENT_DATE);
        when(loanBO.getLoanAmount()).thenReturn(new Money(rupee, LOAN_AMOUNT));
        when(loanBO.getInterestRate()).thenReturn(ANNUAL_INTEREST_RATE);
        Date asOfDate = getDate(30, 10, 2010);
        scheduleCalculatorAdaptor.computeRepaymentAmount(loanBO, asOfDate);
        verify(scheduleMapper, times(1)).mapToSchedule(Mockito.<Collection<LoanScheduleEntity>>any(), Mockito.<Date>any(), Mockito.<Double>any(), Mockito.<BigDecimal>any());
        verify(scheduleCalculator).computeRepaymentAmount(Mockito.<Schedule>any(), eq(asOfDate));
        verify(loanBO).getLoanScheduleEntities();
        verify(loanBO).getDisbursementDate();
        verify(loanBO).getInterestRate();
    }

    private Schedule getSchedule(Date disbursementDate, BigDecimal loanAmount, List<Installment> installments) {
        return new Schedule(disbursementDate, DAILY_INTEREST_RATE, loanAmount, installments);
    }

    private void assertExtraInterest(LoanScheduleEntity loanScheduleEntity, double extraInterest) {
        assertThat(loanScheduleEntity.getExtraInterest().getAmount().doubleValue(), is(extraInterest));
    }

    private Map<Integer, LoanScheduleEntity> getLoanScheduleEntityMap(Collection<LoanScheduleEntity> loanScheduleEntities) {
        return CollectionUtils.asValueMap(loanScheduleEntities, new Transformer<LoanScheduleEntity, Integer>() {
            @Override
            public Integer transform(LoanScheduleEntity input) {
                return Integer.valueOf(input.getInstallmentId());
            }
        });
    }

    private List<Installment> getInstallments(double... extraInterest) {
        return asList(
                getInstallment(1, getDate(23, 10, 2010), getDate(23, 10, 2010), new double[]{100, 10, extraInterest[0], 0.0, 0.0, 0.0, 0.0}, new double[]{0, 0, 0.0, 0.0, 0.0, 0.0, 0.0}),
                getInstallment(2, getDate(23, 11, 2010), getDate(23, 11, 2010), new double[]{100, 10, extraInterest[1], 0.0, 0.0, 0.0, 0.0}, new double[]{0, 0, 0.0, 0.0, 0.0, 0.0, 0.0}),
                getInstallment(3, getDate(23, 12, 2010), getDate(23, 12, 2010), new double[]{100, 10, extraInterest[2], 0.0, 0.0, 0.0, 0.0}, new double[]{0, 0, 0.0, 0.0, 0.0, 0.0, 0.0})
        );
    }

    private Installment getInstallment(int id, Date dueDate, Date paidDate, double[] actualAmounts, double[] paidAmounts) {
        return new InstallmentBuilder(String.valueOf(id)).
                withDueDate(dueDate).
                withPaymentDate(paidDate).
                withPrincipal(actualAmounts[0]).withPrincipalPaid(paidAmounts[0]).
                withInterest(actualAmounts[1]).withInterestPaid(paidAmounts[1]).
                withExtraInterest(actualAmounts[2]).withExtraInterestPaid(paidAmounts[2]).
                withFees(actualAmounts[3]).withFeesPaid(paidAmounts[3]).
                withMiscFees(actualAmounts[4]).withMiscFeesPaid(paidAmounts[4]).
                withPenalty(actualAmounts[5]).withPenaltyPaid(paidAmounts[5]).
                withMiscPenalty(actualAmounts[6]).withMiscPenaltyPaid(paidAmounts[6]).
                build();
    }

    private Set<LoanScheduleEntity> getLoanScheduleEntities() {
        LinkedHashSet<LoanScheduleEntity> loanScheduleEntities = new LinkedHashSet<LoanScheduleEntity>();
        loanScheduleEntities.add(new LoanScheduleBuilder("1", loanBO).withDueDate(getDate(23, 10, 2010)).
                withPaymentStatus(PaymentStatus.UNPAID).withPrincipal(100).withInterest(10).build());
        loanScheduleEntities.add(new LoanScheduleBuilder("2", loanBO).withDueDate(getDate(23, 11, 2010)).
                withPaymentStatus(PaymentStatus.UNPAID).withPrincipal(100).withInterest(10).build());
        loanScheduleEntities.add(new LoanScheduleBuilder("3", loanBO).withDueDate(getDate(23, 12, 2010)).
                withPaymentStatus(PaymentStatus.UNPAID).withPrincipal(100).withInterest(10).build());
        return loanScheduleEntities;
    }

}
