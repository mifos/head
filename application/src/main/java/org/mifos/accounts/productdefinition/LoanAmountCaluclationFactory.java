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
import java.util.List;
import java.util.Set;

import org.mifos.accounts.productdefinition.business.LoanAmountFromLastLoanAmountBO;
import org.mifos.accounts.productdefinition.business.LoanAmountFromLoanCycleBO;
import org.mifos.accounts.productdefinition.business.LoanAmountSameForAllLoanBO;
import org.mifos.dto.domain.LowerUpperMinMaxDefaultDto;
import org.mifos.dto.domain.MinMaxDefaultDto;
import org.mifos.dto.screen.LoanAmountDetails;

public class LoanAmountCaluclationFactory {

    public static LoanAmountCalculation assembleFromDto(LoanAmountDetails loanAmountDetails) {

        LoanProductCalculationType loanCalculation = LoanProductCalculationType.fromInt(loanAmountDetails.getCalculationType());

        LoanAmountSameForAllLoanBO sameForAll = null;
        Set<LoanAmountFromLastLoanAmountBO> loanAmountFromLastLoan = new HashSet<LoanAmountFromLastLoanAmountBO>();
        Set<LoanAmountFromLoanCycleBO> loanAmountFromLoanCycle = new HashSet<LoanAmountFromLoanCycleBO>();
        switch (loanCalculation) {
        case SAME_FOR_ALL_LOANS:
            MinMaxDefaultDto<Double> sameForAllLoan = loanAmountDetails.getSameForAllLoanRange();
            sameForAll = new LoanAmountSameForAllLoanBO(sameForAllLoan.getMin(), sameForAllLoan.getMax(), sameForAllLoan.getTheDefault(), null);
            break;
        case BY_LAST_LOAN:
            List<LowerUpperMinMaxDefaultDto<Double>> byLastAmount = loanAmountDetails.getByLastLoanAmountList();
            for (LowerUpperMinMaxDefaultDto<Double> bean : byLastAmount) {
                loanAmountFromLastLoan.add(new LoanAmountFromLastLoanAmountBO(bean.getMin(), bean.getMax(), bean.getTheDefault(), bean.getLower(), bean.getUpper(), null));
            }
        break;
        case BY_LOAN_CYCLE:
            List<MinMaxDefaultDto<Double>> byCycle = loanAmountDetails.getByLoanCycleList();
            Short rangeIndex = Short.valueOf("0");
            for (MinMaxDefaultDto<Double> bean : byCycle) {
                loanAmountFromLoanCycle.add(new LoanAmountFromLoanCycleBO(bean.getMin(), bean.getMax(), bean.getTheDefault(), rangeIndex, null));
                rangeIndex++;
            }
            break;
        default:
            break;
        }
        return new LoanAmountCalculation(sameForAll, loanAmountFromLastLoan, loanAmountFromLoanCycle);
    }
}