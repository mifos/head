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

import org.mifos.clientportfolio.newloan.domain.IndividualLoan;
import org.mifos.clientportfolio.newloan.domain.LoanService;

public class IndividualLoanServiceFacadeWebTier implements IndividualLoanServiceFacade {

    private final LoanService loanService;
    private final LoanAssembler loanAssembler;

    public IndividualLoanServiceFacadeWebTier(LoanService loanService, LoanAssembler loanAssembler) {
        this.loanService = loanService;
        this.loanAssembler = loanAssembler;
    }

    @Override
    public void create(IndividualLoanRequest individualLoan) {

        IndividualLoan loan = loanAssembler.assembleFrom(individualLoan);

//        loanService.create(loan);
    }
}