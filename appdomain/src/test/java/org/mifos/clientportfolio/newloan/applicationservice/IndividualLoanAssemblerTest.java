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

package org.mifos.clientportfolio.newloan.applicationservice;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.clientportfolio.newloan.domain.ClientId;
import org.mifos.clientportfolio.newloan.domain.IndividualLoan;
import org.mifos.clientportfolio.newloan.domain.LoanProductId;
import org.mifos.clientportfolio.newloan.domain.LoanScheduleFactory;
import org.mifos.clientportfolio.newloan.domain.RecurringScheduledEventFactory;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IndividualLoanAssemblerTest {

    private LoanAssembler loanAssembler;

    @Mock private LoanProductDao loanProductDao;
    @Mock private CustomerDao customerDao;
    @Mock private LoanScheduleFactory loanScheduleFactory;
    @Mock private ScheduledDateGeneration scheduledDateGeneration;
    @Mock private RecurringScheduledEventFactory scheduledEventFactory;

    // stubs
    private LoanProductId loanProductId = new LoanProductId("LPLPLP-001");
    private ClientId clientId = new ClientId("CCC-001");
    @Mock private LoanOfferingBO loanProduct;

    @Before
    public void setup() {
        loanAssembler = new IndividualLoanAssembler(loanProductDao, customerDao, loanScheduleFactory,
                scheduledDateGeneration, scheduledEventFactory);
    }

    @Test
    public void shouldRetrieveLoanProductWhenAssembling() {

        // setup
        IndividualLoanRequest individualLoanRequest = new IndividualLoanRequestBuilder().with(loanProductId).build();

        // stubbing
        when(loanProductDao.findBySystemId(loanProductId.globalIdentity())).thenReturn(loanProduct);

        // exercise test
        IndividualLoan individualLoan = loanAssembler.assembleFrom(individualLoanRequest);

        // verification
        verify(loanProductDao).findBySystemId(loanProductId.globalIdentity());
        assertThat(individualLoan, is(notNullValue()));
    }

    @Test
    public void shouldRetrieveClientWhenAssembling() {

        // setup
        IndividualLoanRequest individualLoanRequest = new IndividualLoanRequestBuilder().with(loanProductId)
                                                                                        .with(clientId)
                                                                                        .build();

        // stubbing
        when(loanProductDao.findBySystemId(loanProductId.globalIdentity())).thenReturn(loanProduct);

        // exercise test
        loanAssembler.assembleFrom(individualLoanRequest);

        // verification
        verify(customerDao).findClientBySystemId(clientId.globalIdentity());
    }

}