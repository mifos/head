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
package org.mifos.platform.cashflow.service;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.cashflow.CashFlowService;
import org.mifos.platform.cashflow.builder.CashFlowDetailsBuilder;
import org.mifos.platform.cashflow.builder.CashFlowEntityBuilder;
import org.mifos.platform.cashflow.domain.CashFlow;
import org.mifos.platform.cashflow.domain.MonthlyCashFlow;
import org.mifos.platform.cashflow.matchers.CashFlowMatcher;
import org.mifos.platform.cashflow.persistence.CashFlowDao;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class CashFlowServiceTest {

    private CashFlowService cashFlowService;
    private BigDecimal revenue = new BigDecimal(100.21d);
    private BigDecimal expense = new BigDecimal(50.37d);

    @Mock
    CashFlowDao cashFlowDao;

    @Before
    public void setUp() {
        cashFlowService = new CashFlowServiceImpl(cashFlowDao);
    }

    @Test
    public void shouldSaveCashFlow() {
        Mockito.when(cashFlowDao.create(Mockito.<CashFlow>any())).thenReturn(new Integer(10));
        Integer cashFlowId = cashFlowService.save(getCashFlowDetails());
        Mockito.verify(cashFlowDao, Mockito.times(1)).create(
                Mockito.argThat(new CashFlowMatcher(getCashFlowEntity()))
        );
        Assert.assertNotNull(cashFlowId);
        Assert.assertEquals(cashFlowId, new Integer(10));
    }

    @Test
    public void cashFlowBoundariesWithSameStartAndEndDates() {
        CashFlowBoundary cashFlowBoundary = cashFlowService.getCashFlowBoundary(new DateTime(2009, 12, 31, 1, 1,
                1, 1), new DateTime(2009, 12, 31, 1, 1, 1, 1));
        Assert.assertEquals(cashFlowBoundary.getStartMonth(), 11);
        Assert.assertEquals(cashFlowBoundary.getStartYear(), 2009);
        Assert.assertEquals(cashFlowBoundary.getNumberOfMonths(), 3);
    }

    @Test
    public void cashFlowBoundariesWithExtremeDaysOfMonths() {
        CashFlowBoundary cashFlowBoundary = cashFlowService.getCashFlowBoundary(new DateTime(2009, 12, 1, 1, 1,
                1, 1), new DateTime(2009, 12, 31, 1, 1, 1, 1));
        Assert.assertEquals(cashFlowBoundary.getStartMonth(), 11);
        Assert.assertEquals(cashFlowBoundary.getStartYear(), 2009);
        Assert.assertEquals(cashFlowBoundary.getNumberOfMonths(), 3);
    }

    @Test
    public void cashFlowBoundariesForDatesSpanningMultipleYears() {
        CashFlowBoundary cashFlowBoundary = cashFlowService.getCashFlowBoundary(new DateTime(2009, 12, 1, 1, 1,
                1, 1), new DateTime(2010, 1, 1, 1, 1, 1, 1));
        Assert.assertEquals(cashFlowBoundary.getStartMonth(), 11);
        Assert.assertEquals(cashFlowBoundary.getStartYear(), 2009);
        Assert.assertEquals(cashFlowBoundary.getNumberOfMonths(), 4);
    }

    @Test
    public void cashFlowBoundariesForDatesSpanningMultipleYearsAndExtremeDaysOfMonths() {
        CashFlowBoundary cashFlowBoundary = cashFlowService.getCashFlowBoundary(new DateTime(2009, 12, 1, 1, 1,
                1, 1), new DateTime(2010, 1, 31, 1, 1, 1, 1));
        Assert.assertEquals(cashFlowBoundary.getStartMonth(), 11);
        Assert.assertEquals(cashFlowBoundary.getStartYear(), 2009);
        Assert.assertEquals(cashFlowBoundary.getNumberOfMonths(), 4);
    }

    @Test
    public void cashFlowBoundariesForDaysInMiddle() {
        CashFlowBoundary cashFlowBoundary = cashFlowService.getCashFlowBoundary(new DateTime(2009, 12, 31, 1, 1,
                1, 1), new DateTime(2010, 1, 7, 1, 1, 1, 1));
        Assert.assertEquals(cashFlowBoundary.getStartMonth(), 11);
        Assert.assertEquals(cashFlowBoundary.getStartYear(), 2009);
        Assert.assertEquals(cashFlowBoundary.getNumberOfMonths(), 4);
    }

    private CashFlowDetail getCashFlowDetails() {
        DateTime dateTime = new DateTime(2010, 10, 11, 12, 13, 14, 15);
        return new CashFlowDetailsBuilder().
                withMonthlyCashFlow(new MonthlyCashFlowDetail(dateTime, revenue, expense, "my notes")).
                withMonthlyCashFlow(new MonthlyCashFlowDetail(dateTime.plusMonths(1), revenue.add(new BigDecimal(20.01)), expense.add(new BigDecimal(10.22)), "my other notes")).
                build();
    }

    private CashFlow getCashFlowEntity() {
        DateTime dateTime = new DateTime(2010, 10, 1, 2, 3, 4, 5);
        return new CashFlowEntityBuilder().
                withMonthlyCashFlow(new MonthlyCashFlow(dateTime.plusMonths(1), revenue.add(new BigDecimal(20.01)), expense.add(new BigDecimal(10.22)), "my other notes")).
                withMonthlyCashFlow(new MonthlyCashFlow(dateTime, revenue, expense, "my notes")).
                build();
    }
}