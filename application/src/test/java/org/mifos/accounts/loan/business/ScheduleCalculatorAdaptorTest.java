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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.schedule.calculation.ScheduleCalculator;
import org.mifos.accounts.loan.schedule.domain.Installment;
import org.mifos.accounts.loan.schedule.domain.Schedule;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.Transformer;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.mifos.framework.TestUtils.getDate;

@RunWith(MockitoJUnitRunner.class)

public class ScheduleCalculatorAdaptorTest {
    @Mock
    LoanBO loanBO;

    private MifosCurrency rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
    private ScheduleCalculatorAdaptor scheduleCalculatorAdaptor;
    private static final Double DAILY_INTEREST_RATE = 0.000658;
    private static final Double ANNUAL_INTEREST_RATE = 24.0;
    private static final BigDecimal LOAN_AMOUNT = new BigDecimal(1000);

    @Before
    public void setup() {
        scheduleCalculatorAdaptor = new ScheduleCalculatorAdaptor();
    }

    @Test
    public void shouldComputeExtraInterestForDecliningPrincipalBalance() {
        ArrayList<LoanScheduleEntity> loanScheduleEntities = getLoanScheduleEntities();
        Mockito.when(loanBO.isDecliningPrincipalBalance()).thenReturn(true);
        Mockito.when(loanBO.getLoanScheduleEntities()).thenReturn(loanScheduleEntities);
        Mockito.when(loanBO.getDisbursementDate()).thenReturn(getDate(23, 9, 2010));
        Mockito.when(loanBO.getLoanAmount()).thenReturn(new Money(rupee,LOAN_AMOUNT));
        Mockito.when(loanBO.getInterestRate()).thenReturn(ANNUAL_INTEREST_RATE);
        Mockito.when(loanBO.getLoanScheduleEntityMap()).thenReturn(getLoanScheduleEntityMap(loanScheduleEntities));

        scheduleCalculatorAdaptor.computeExtraInterest(loanBO, getDate(30, 10, 2010));

        Mockito.verify(loanBO, Mockito.times(1)).isDecliningPrincipalBalance();
        Mockito.verify(loanBO, Mockito.times(1)).getLoanScheduleEntities();
        Mockito.verify(loanBO, Mockito.times(1)).getDisbursementDate();
        Mockito.verify(loanBO, Mockito.times(1)).getLoanAmount();
        Mockito.verify(loanBO, Mockito.times(1)).getInterestRate();
        Mockito.verify(loanBO, Mockito.times(1)).getLoanScheduleEntityMap();

        ArrayList<LoanScheduleEntity> loanScheduleEntitiesWithExtraInterest = new ArrayList<LoanScheduleEntity>(loanBO.getLoanScheduleEntities());
        assertExtraInterest(loanScheduleEntitiesWithExtraInterest.get(0), 0.0);
        assertExtraInterest(loanScheduleEntitiesWithExtraInterest.get(1), 0.46);
        assertExtraInterest(loanScheduleEntitiesWithExtraInterest.get(2), 0.0);
    }
    
    @Test
    public void shouldNotComputeExtraInterestForNonPrincipalBalanceInterestTypes() {
        ArrayList<LoanScheduleEntity> loanScheduleEntities = getLoanScheduleEntities();
        Mockito.when(loanBO.isDecliningPrincipalBalance()).thenReturn(false);
        Mockito.when(loanBO.getLoanScheduleEntities()).thenReturn(loanScheduleEntities);
        Mockito.when(loanBO.getDisbursementDate()).thenReturn(getDate(23, 9, 2010));
        Mockito.when(loanBO.getLoanAmount()).thenReturn(new Money(rupee,LOAN_AMOUNT));
        Mockito.when(loanBO.getInterestRate()).thenReturn(ANNUAL_INTEREST_RATE);
        Mockito.when(loanBO.getLoanScheduleEntityMap()).thenReturn(getLoanScheduleEntityMap(loanScheduleEntities));

        scheduleCalculatorAdaptor.computeExtraInterest(loanBO, getDate(30, 10, 2010));

        Mockito.verify(loanBO, Mockito.times(0)).getInterestRate();
        Mockito.verify(loanBO, Mockito.times(0)).getLoanScheduleEntities();
        Mockito.verify(loanBO, Mockito.times(0)).getDisbursementDate();
        Mockito.verify(loanBO, Mockito.times(0)).getLoanAmount();
        Mockito.verify(loanBO, Mockito.times(0)).getInterestRate();
        Mockito.verify(loanBO, Mockito.times(0)).getLoanScheduleEntityMap();

        ArrayList<LoanScheduleEntity> loanScheduleEntitiesWithExtraInterest = new ArrayList<LoanScheduleEntity>(loanBO.getLoanScheduleEntities());
        assertExtraInterest(loanScheduleEntitiesWithExtraInterest.get(0), 0.0);
        assertExtraInterest(loanScheduleEntitiesWithExtraInterest.get(1), 0.0);
        assertExtraInterest(loanScheduleEntitiesWithExtraInterest.get(2), 0.0);
    }

    private void assertExtraInterest(LoanScheduleEntity loanScheduleEntity, double extraInterest) {
        Assert.assertThat(loanScheduleEntity.getExtraInterest().getAmount().doubleValue(), is(extraInterest));
    }

    @Test
    public void shouldMapLoanScheduleEntityToSchedule() {
        ArrayList<LoanScheduleEntity> loanScheduleEntities = getLoanScheduleEntities();
        Date disbursementDate = getDate(20, 10, 2010);
        Schedule schedule = scheduleCalculatorAdaptor.mapToSchedule(loanScheduleEntities, disbursementDate, DAILY_INTEREST_RATE, LOAN_AMOUNT);
        Assert.assertEquals(schedule.getDailyInterestRate(), DAILY_INTEREST_RATE);
        Assert.assertEquals(schedule.getDisbursementDate(), disbursementDate);
        Assert.assertEquals(schedule.getLoanAmount(), LOAN_AMOUNT);
        Map<Integer, Installment> installments = schedule.getInstallments();
        for (LoanScheduleEntity loanScheduleEntity : loanScheduleEntities) {
            assertInstallment(installments.get(new Integer(loanScheduleEntity.getInstallmentId())), loanScheduleEntity);
        }
    }

    @Test
    public void shouldPopulateExtraInterestInLoanScheduleEntities() {
        List<Installment> installments = getInstallments();
        Schedule schedule = new Schedule(getDate(23, 9, 2010), DAILY_INTEREST_RATE, LOAN_AMOUNT, installments);
        for (int i = 0; i < installments.size(); i++) {
            installments.get(i).setExtraInterest(new BigDecimal(i));
        }
        ArrayList<LoanScheduleEntity> loanScheduleEntities = getLoanScheduleEntities();
        Map<Integer, LoanScheduleEntity> loanScheduleEntityMap = getLoanScheduleEntityMap(loanScheduleEntities);
        scheduleCalculatorAdaptor.populateExtraInterestInLoanScheduleEntities(schedule, loanScheduleEntityMap);
        assertExtraInterest(loanScheduleEntityMap.get(1),0d);
        assertExtraInterest(loanScheduleEntityMap.get(2),1d);
        assertExtraInterest(loanScheduleEntityMap.get(3),2d);
    }

    @Test
    public void shouldComputeAndExtraInterestToLoanScheduleEntities() {
        List<Installment> installments = getInstallments();
        Schedule schedule = new Schedule(getDate(23, 9, 2010), DAILY_INTEREST_RATE, LOAN_AMOUNT, installments);
        new ScheduleCalculator().computeExtraInterest(schedule, getDate(30, 10, 2010));
        ArrayList<LoanScheduleEntity> loanScheduleEntities = getLoanScheduleEntities();
        Map<Integer, LoanScheduleEntity> loanScheduleEntityMap = getLoanScheduleEntityMap(loanScheduleEntities);
        Assert.assertThat(schedule.getInstallments().get(2).getExtraInterest().doubleValue(), is(0.46));

        scheduleCalculatorAdaptor.populateExtraInterestInLoanScheduleEntities(schedule, loanScheduleEntityMap);
        for (Installment installment : installments) {
            LoanScheduleEntity loanScheduleEntity = loanScheduleEntityMap.get(installment.getId());
            assertExtraInterest(loanScheduleEntity,installment.getExtraInterest().doubleValue());
        }
    }

    private Map<Integer, LoanScheduleEntity> getLoanScheduleEntityMap(List<LoanScheduleEntity> loanScheduleEntities) {
        return CollectionUtils.asValueMap(loanScheduleEntities, new Transformer<LoanScheduleEntity, Integer>() {
            @Override
            public Integer transform(LoanScheduleEntity input) {
                return Integer.valueOf(input.getInstallmentId());
            }
        });
    }

    private void assertInstallment(Installment installment, LoanScheduleEntity loanScheduleEntity) {
        Assert.assertEquals(installment.getDueDate(), loanScheduleEntity.getActionDate());
        Assert.assertEquals(installment.getPrincipal(), loanScheduleEntity.getPrincipal().getAmount());
        Assert.assertEquals(installment.getInterestDue(), loanScheduleEntity.getInterest().getAmount());
    }

    private List<Installment> getInstallments() {
        Installment installment1 = getInstallment(1, getDate(23, 10, 2010), 100, 10, 0);
        Installment installment2 = getInstallment(2, getDate(23, 11, 2010), 100, 10, 0);
        Installment installment3 = getInstallment(3, getDate(23, 12, 2010), 100, 10, 0);
        List<Installment> installments = asList(installment1, installment2, installment3);
        return installments;
    }

    private Installment getInstallment(int id, Date dueDate, double principal, double interest, double extraInterest) {
        Installment installment = new Installment(id, dueDate, BigDecimal.valueOf(principal), BigDecimal.valueOf(interest), BigDecimal.ZERO);
        installment.setExtraInterest(BigDecimal.valueOf(extraInterest));
        return installment;
    }

    private ArrayList<LoanScheduleEntity> getLoanScheduleEntities() {
        ArrayList<LoanScheduleEntity> loanScheduleEntities = new ArrayList<LoanScheduleEntity>();
        loanScheduleEntities.add(getLoanScheduleEntity(rupee, getDate(23, 10, 2010), "100", "10", "1"));
        loanScheduleEntities.add(getLoanScheduleEntity(rupee, getDate(23, 11, 2010), "100", "10", "2"));
        loanScheduleEntities.add(getLoanScheduleEntity(rupee, getDate(23, 12, 2010), "100", "10", "3"));
        return loanScheduleEntities;
    }

    private LoanScheduleEntity getLoanScheduleEntity(MifosCurrency currency, Date date, String principal, String interest, String installmentId) {
        LoanBO loan = new LoanBO() {
            public MifosCurrency getCurrency() {
                return rupee;
            }
        };
        return new LoanScheduleEntity(loan, null, Short
                .valueOf(installmentId), new java.sql.Date(date.getTime()), PaymentStatus.UNPAID, new Money(currency, principal),
                new Money(currency, interest));

    }
}
