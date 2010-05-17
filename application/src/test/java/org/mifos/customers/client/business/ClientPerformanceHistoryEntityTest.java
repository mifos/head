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

package org.mifos.customers.client.business;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClientPerformanceHistoryEntityTest {

    private ClientPerformanceHistoryEntity clientgroupPerformanceHistoryEntity;

    @Mock
    private LoanBO loan;

    @Mock
    private ClientBO client;

    private static MifosCurrency oldDefaultCurrency;

    @BeforeClass
    public static void initialiseHibernateUtil() {

        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }


    @Test
    public void shouldUpdateLastLoanAmountWhenLoanIsFullyPaid() throws Exception {

        Money loanAmount = new Money(Money.getDefaultCurrency(), "55.6");
        // setup
        clientgroupPerformanceHistoryEntity = new ClientPerformanceHistoryEntity(client);

        when(loan.getLoanAmount()).thenReturn(loanAmount);

        // exercise test
        clientgroupPerformanceHistoryEntity.updateOnFullRepayment(loanAmount);

        // verification
        assertThat(clientgroupPerformanceHistoryEntity.getLastLoanAmount(), is(notNullValue()));
        assertThat(clientgroupPerformanceHistoryEntity.getLastLoanAmount().getAmountDoubleValue(), is(loanAmount.getAmountDoubleValue()));
    }
}