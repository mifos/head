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
import org.mifos.accounts.productdefinition.business.NoOfInstallFromLastLoanAmountBO;
import org.mifos.accounts.productdefinition.business.NoOfInstallFromLoanCycleBO;
import org.mifos.accounts.productdefinition.business.NoOfInstallSameForAllLoanBO;
import org.mifos.dto.domain.LowerUpperMinMaxDefaultDto;
import org.mifos.dto.domain.MinMaxDefaultDto;
import org.mifos.dto.screen.LoanAmountDetailsDto;

public class LoanProductCaluclationTypeAssembler {

    public LoanAmountCalculation assembleLoanAmountCalculationFromDto(LoanAmountDetailsDto loanAmountDetails) {

        LoanProductCalculationType loanCalculation = LoanProductCalculationType.fromInt(loanAmountDetails.getCalculationType());

        LoanAmountSameForAllLoanBO sameForAll = null;
        Set<LoanAmountFromLastLoanAmountBO> loanAmountFromLastLoan = new HashSet<LoanAmountFromLastLoanAmountBO>();
        Set<LoanAmountFromLoanCycleBO> loanAmountFromLoanCycle = new HashSet<LoanAmountFromLoanCycleBO>();
        switch (loanCalculation) {
        case SAME_FOR_ALL_LOANS:
            MinMaxDefaultDto sameForAllLoan = loanAmountDetails.getSameForAllLoanRange();
            sameForAll = new LoanAmountSameForAllLoanBO(sameForAllLoan.getMin().doubleValue(), sameForAllLoan.getMax().doubleValue(), sameForAllLoan.getTheDefault().doubleValue(), null);
            break;
        case BY_LAST_LOAN:
            List<LowerUpperMinMaxDefaultDto> byLastAmount = loanAmountDetails.getByLastLoanAmountList();
            for (LowerUpperMinMaxDefaultDto bean : byLastAmount) {
                loanAmountFromLastLoan.add(new LoanAmountFromLastLoanAmountBO(bean.getMin().doubleValue(), bean.getMax().doubleValue(), bean.getTheDefault().doubleValue(), bean.getLower().doubleValue(), bean.getUpper().doubleValue(), null));
            }
        break;
        case BY_LOAN_CYCLE:
            List<MinMaxDefaultDto> byCycle = loanAmountDetails.getByLoanCycleList();
            Short rangeIndex = Short.valueOf("0");
            for (MinMaxDefaultDto bean : byCycle) {
                loanAmountFromLoanCycle.add(new LoanAmountFromLoanCycleBO(bean.getMin().doubleValue(), bean.getMax().doubleValue(), bean.getTheDefault().doubleValue(), rangeIndex, null));
                rangeIndex++;
            }
            break;
        default:
            break;
        }
        return new LoanAmountCalculation(sameForAll, loanAmountFromLastLoan, loanAmountFromLoanCycle);
    }

    public LoanInstallmentCalculation assembleLoanInstallmentCalculationFromDto(LoanAmountDetailsDto installmentCalculationDetails) {
        NoOfInstallSameForAllLoanBO sameForAll = null;
        Set<NoOfInstallFromLastLoanAmountBO> fromLastLoan = new HashSet<NoOfInstallFromLastLoanAmountBO>();
        Set<NoOfInstallFromLoanCycleBO> fromLoanCycle = new HashSet<NoOfInstallFromLoanCycleBO>();

        LoanProductCalculationType installmentCalculation = LoanProductCalculationType.fromInt(installmentCalculationDetails.getCalculationType());
        switch (installmentCalculation) {
        case SAME_FOR_ALL_LOANS:
            sameForAll = new NoOfInstallSameForAllLoanBO(installmentCalculationDetails.getSameForAllLoanRange().getMin().shortValue(), installmentCalculationDetails.getSameForAllLoanRange().getMax().shortValue(), installmentCalculationDetails.getSameForAllLoanRange().getTheDefault().shortValue(), null);
            break;
        case BY_LAST_LOAN:
            List<LowerUpperMinMaxDefaultDto> byLastAmount = installmentCalculationDetails.getByLastLoanAmountList();
            for (LowerUpperMinMaxDefaultDto bean : byLastAmount) {
                fromLastLoan.add(new NoOfInstallFromLastLoanAmountBO(bean.getMin().shortValue(), bean.getMax().shortValue(), bean.getTheDefault().shortValue(), bean.getLower().doubleValue(), bean.getUpper().doubleValue(), null));
            }
        break;
        case BY_LOAN_CYCLE:
            List<MinMaxDefaultDto> byCycle = installmentCalculationDetails.getByLoanCycleList();
            Short rangeIndex = Short.valueOf("0");
            for (MinMaxDefaultDto bean : byCycle) {
                fromLoanCycle.add(new NoOfInstallFromLoanCycleBO(bean.getMin().shortValue(), bean.getMax().shortValue(), bean.getTheDefault().shortValue(), rangeIndex, null));
                rangeIndex++;
            }
            break;
        default:
            break;
        }
        return new LoanInstallmentCalculation(sameForAll, fromLastLoan, fromLoanCycle);
    }
}