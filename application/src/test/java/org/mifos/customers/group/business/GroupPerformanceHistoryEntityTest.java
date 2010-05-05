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

package org.mifos.customers.group.business;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GroupPerformanceHistoryEntityTest {

    private GroupPerformanceHistoryEntity groupPerformanceHistoryEntity;

    @Mock
    private LoanBO loan;

    @Mock
    private GroupBO group;

    @Ignore
    @Test
    public void shouldUpdateLastLoanAmountOnFullRepayment() throws Exception {

        // setup
        groupPerformanceHistoryEntity = new GroupPerformanceHistoryEntity(group);
        Money totalAmount = null;

        // exercise test
        groupPerformanceHistoryEntity.updateOnRepayment(loan, totalAmount);

        // verification
        assertThat(groupPerformanceHistoryEntity.getLastGroupLoanAmount(), is(notNullValue()));
        assertThat(groupPerformanceHistoryEntity.getLastGroupLoanAmount().getAmountDoubleValue(), is(Double.valueOf("")));
    }
}