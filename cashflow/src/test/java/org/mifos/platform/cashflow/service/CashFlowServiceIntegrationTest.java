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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.cashflow.CashFlowService;
import org.mifos.platform.cashflow.builder.CashFlowDetailsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-cashflow-dbContext.xml", "/test-cashflow-persistenceContext.xml", "/META-INF/spring/CashFlowContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
public class CashFlowServiceIntegrationTest {

    @Autowired
    private CashFlowService cashFlowService;
    private BigDecimal revenue = new BigDecimal(100.21d);
    private BigDecimal expense = new BigDecimal(50.37d);

    @Test
    @Transactional
    public void shouldSaveCashFlow() {
        Integer cashFlowId = cashFlowService.save(getCashFlowDetails());
        Assert.assertNotNull(cashFlowId);
    }

    private CashFlowDetail getCashFlowDetails() {
        DateTime dateTime = new DateTime(2010, 10, 11, 12, 13, 14, 15);
        return new CashFlowDetailsBuilder().
                withMonthlyCashFlow(new MonthlyCashFlowDetail(dateTime, revenue, expense, "my notes")).
                withMonthlyCashFlow(new MonthlyCashFlowDetail(dateTime.plusMonths(1), revenue.add(new BigDecimal(20.01)), expense.add(new BigDecimal(10.22)), "my other notes")).
                build();
    }
}
