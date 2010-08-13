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

package org.mifos.accounts.productdefinition;

import java.util.Set;

import org.mifos.accounts.productdefinition.business.NoOfInstallFromLastLoanAmountBO;
import org.mifos.accounts.productdefinition.business.NoOfInstallFromLoanCycleBO;
import org.mifos.accounts.productdefinition.business.NoOfInstallSameForAllLoanBO;

public class LoanInstallmentCalculation {

    private final NoOfInstallSameForAllLoanBO sameForAll;
    private final Set<NoOfInstallFromLastLoanAmountBO> fromLastLoan;
    private final Set<NoOfInstallFromLoanCycleBO> fromLoanCycle;

    public LoanInstallmentCalculation(NoOfInstallSameForAllLoanBO sameForAll,
            Set<NoOfInstallFromLastLoanAmountBO> fromLastLoan, Set<NoOfInstallFromLoanCycleBO> fromLoanCycle) {
        this.sameForAll = sameForAll;
        this.fromLastLoan = fromLastLoan;
        this.fromLoanCycle = fromLoanCycle;
    }

    public NoOfInstallSameForAllLoanBO getSameForAll() {
        return this.sameForAll;
    }

    public Set<NoOfInstallFromLastLoanAmountBO> getFromLastLoan() {
        return this.fromLastLoan;
    }

    public Set<NoOfInstallFromLoanCycleBO> getFromLoanCycle() {
        return this.fromLoanCycle;
    }
}