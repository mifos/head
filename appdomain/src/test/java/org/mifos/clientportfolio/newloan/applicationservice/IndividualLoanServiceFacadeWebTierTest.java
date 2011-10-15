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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.clientportfolio.newloan.domain.IndividualLoan;
import org.mifos.clientportfolio.newloan.domain.LoanService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IndividualLoanServiceFacadeWebTierTest {

    private IndividualLoanServiceFacade individualLoanServiceFacade;

    @Mock private LoanService loanService;
    @Mock private LoanAssembler loanAssembler;
    @Mock private IndividualLoan individualLoan;

    @Before
    public void setup() {
        individualLoanServiceFacade = new IndividualLoanServiceFacadeWebTier(loanService, loanAssembler);
    }

    @Test
    public void shouldAssembleIndividualLoanWhenCreatingLoan() {

        // setup
        IndividualLoanRequest individualLoanRequest = new IndividualLoanRequestBuilder().build();

        // exercise test
        individualLoanServiceFacade.create(individualLoanRequest);

        // verification
        verify(loanAssembler).assembleFrom(individualLoanRequest);
    }

    @Test
    public void shouldCreateLoanAssembledFromIndividualLoanRequest() {

        // setup
        IndividualLoanRequest individualLoanRequest = new IndividualLoanRequestBuilder().build();

        // stubbing
        when(loanAssembler.assembleFrom(individualLoanRequest)).thenReturn(individualLoan);

        // exercise test
        individualLoanServiceFacade.create(individualLoanRequest);

        // verification
//        verify(loanService).create(individualLoan);
    }
}