/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.cashflow.CashFlowService;
import org.mifos.platform.cashflow.builder.CashFlowDetailsBuilder;
import org.mifos.platform.cashflow.domain.CashFlow;
import org.mifos.platform.cashflow.persistence.CashFlowDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-cashflow-dbContext.xml", "/test-cashflow-persistenceContext.xml", "/META-INF/spring/CashFlowContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
public class CashFlowServiceIntegrationTest {

    @Autowired
    private CashFlowService cashFlowService;

    @Autowired
    private CashFlowDao cashFlowDao;

    private BigDecimal revenue = new BigDecimal(100.21d);
    private BigDecimal expense = new BigDecimal(50.37d);

    @Test
    @Transactional
    public void shouldSaveCashFlowWithTotalCapitalAndTotalLiability() {
        Integer cashFlowId = cashFlowService.save(getCashFlowDetails(123d, 456d));
        CashFlow cashFlow = cashFlowDao.getDetails(cashFlowId);
        BigDecimal totalLiability = cashFlow.getTotalLiability();
        BigDecimal totalCapital = cashFlow.getTotalCapital();
        assertThat(totalCapital.doubleValue(), is(123d));
        assertThat(totalLiability.doubleValue(), is(456d));
    }

    private CashFlowDetail getCashFlowDetails(double totalCapital, double totalLiability) {
        DateTime dateTime = new DateTime(2010, 10, 11, 12, 13, 14, 15);
        return new CashFlowDetailsBuilder().
                withMonthlyCashFlow(new MonthlyCashFlowDetail(dateTime, revenue, expense, "my notes")).
                withMonthlyCashFlow(new MonthlyCashFlowDetail(dateTime.plusMonths(1), revenue.add(new BigDecimal(20.01)), expense.add(new BigDecimal(10.22)), "my other notes")).
                withTotalCapital(totalCapital).
                withTotalLiability(totalLiability).
                build();
    }
}
