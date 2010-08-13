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

import java.util.HashSet;
import java.util.Set;

import org.mifos.accounts.productdefinition.business.LoanAmountFromLastLoanAmountBO;
import org.mifos.accounts.productdefinition.business.LoanAmountFromLoanCycleBO;
import org.mifos.accounts.productdefinition.business.LoanAmountSameForAllLoanBO;

public class LoanAmountCaluclationFactory {

    public static LoanAmountCalculation create(LoanProductCalculationType loanCalculation, Double min, Double max, Double theDefault) {

        LoanAmountSameForAllLoanBO sameForAll = null;
        Set<LoanAmountFromLastLoanAmountBO> loanAmountFromLastLoan = new HashSet<LoanAmountFromLastLoanAmountBO>();
        Set<LoanAmountFromLoanCycleBO> loanAmountFromLoanCycle = new HashSet<LoanAmountFromLoanCycleBO>();
        switch (loanCalculation) {
        case SAME_FOR_ALL_LOANS:
            sameForAll = new LoanAmountSameForAllLoanBO(min, max, theDefault, null);
            loanAmountFromLastLoan = new HashSet<LoanAmountFromLastLoanAmountBO>();
            loanAmountFromLoanCycle = new HashSet<LoanAmountFromLoanCycleBO>();
            break;
        default:
            break;
        }
        return new LoanAmountCalculation(sameForAll, loanAmountFromLastLoan, loanAmountFromLoanCycle);
    }

}
