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

package org.mifos.clientportfolio.newloan.domain;

import java.math.BigDecimal;

import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.framework.util.helpers.Money;

public class DecliningBalanceWithEqualPrincipalInstallmentsLoanInterestCalculator implements LoanInterestCalculator {

    @Override
    public Money calculate(LoanInterestCalculationDetails loanInterestCalculationDetails) {

        Money loanAmount = loanInterestCalculationDetails.getLoanAmount();
        GraceType graceType = loanInterestCalculationDetails.getGraceType();
        Integer gracePeriodDuration = loanInterestCalculationDetails.getGracePeriodDuration();
        Integer numberOfInstallments = loanInterestCalculationDetails.getNumberOfInstallments();
        Double interestFractionalRatePerInstallment = loanInterestCalculationDetails.getInterestFractionalRatePerInstallment();

        return getDecliningEPIAmount_v2(graceType, gracePeriodDuration, numberOfInstallments, loanAmount, interestFractionalRatePerInstallment);
    }

    private Money getDecliningEPIAmount_v2(GraceType graceType, Integer gracePeriodDuration, Integer numberOfInstallments, Money loanAmount, Double interestFractionalRatePerInstallment) {

        Money interest = new Money(loanAmount.getCurrency(), "0");
        if (graceType.equals(GraceType.PRINCIPALONLYGRACE)) {
            Money graceInterestPayments = getDecliningInterestAmountGrace_v2(loanAmount, gracePeriodDuration, interestFractionalRatePerInstallment);
            Money nonGraceInterestPayments = getDecliningEPIAmountNonGrace_v2(numberOfInstallments - gracePeriodDuration, loanAmount, interestFractionalRatePerInstallment);
            interest = graceInterestPayments.add(nonGraceInterestPayments);
        } else {
            interest = getDecliningEPIAmountNonGrace_v2(numberOfInstallments, loanAmount, interestFractionalRatePerInstallment);
        }
        return interest;
    }

    private Money getDecliningInterestAmountGrace_v2(Money loanAmount, Integer gracePeriodDuration, Double interestFractionalRatePerInstallment) {
        return loanAmount.multiply(interestFractionalRatePerInstallment).multiply(gracePeriodDuration.doubleValue());
    }

    // the decliningEPI amount = sum of interests for all installments
    private Money getDecliningEPIAmountNonGrace_v2(final int numNonGraceInstallments, Money loanAmount, Double interestFractionalRatePerInstallment) {
        Money principalBalance = loanAmount;
        Money principalPerPeriod = principalBalance.divide(new BigDecimal(numNonGraceInstallments));
        Money totalInterest = new Money(loanAmount.getCurrency(), "0");
        for (int i = 0; i < numNonGraceInstallments; i++) {
            Money interestThisPeriod = principalBalance.multiply(interestFractionalRatePerInstallment);
            totalInterest = totalInterest.add(interestThisPeriod);
            principalBalance = principalBalance.subtract(principalPerPeriod);
        }

        return totalInterest;
    }
}