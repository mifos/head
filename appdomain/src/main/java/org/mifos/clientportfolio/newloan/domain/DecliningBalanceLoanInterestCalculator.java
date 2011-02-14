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

import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.framework.util.helpers.Money;

public class DecliningBalanceLoanInterestCalculator implements LoanInterestCalculator {


    @Override
    public Money calculate(LoanInterestCalculationDetails loanInterestCalculationDetails) {

        Money loanAmount = loanInterestCalculationDetails.getLoanAmount();
        Double interestRate = loanInterestCalculationDetails.getInterestRate();

        GraceType graceType = loanInterestCalculationDetails.getGraceType();
        Integer gracePeriodDuration = loanInterestCalculationDetails.getGracePeriodDuration();
        Integer numberOfInstallments = loanInterestCalculationDetails.getNumberOfInstallments();
        Double interestFractionalRatePerInstallment = loanInterestCalculationDetails.getInterestFractionalRatePerInstallment();

        return getDecliningInterestAmount_v2(graceType, gracePeriodDuration, numberOfInstallments, loanAmount, interestRate, interestFractionalRatePerInstallment);

    }

    /**
     * Compute the total interest due on a declining-interest loan. Interest during a principal-only grace period is
     * calculated differently from non-grace-periods.
     * <p>
     * The formula is as follows:
     * <p>
     * The total interest paid is I = Ig + In where Ig = interest paid during any principal-only grace periods In =
     * interest paid during regular payment periods In = A - P A = total amount paid across regular payment periods The
     * formula for computing A is A = p * n where A = total amount paid p = payment per installment n = number of
     * regular (non-grace) installments P = principal i = interest per period
     */
    private Money getDecliningInterestAmount_v2(GraceType graceType, Integer gracePeriodDuration, Integer numOfInstallments,
            Money loanAmount, Double interestRate, Double interestFractionalRatePerInstallment) {

        Money nonGraceInterestPayments = getDecliningInterestAmountNonGrace_v2(numOfInstallments, loanAmount, interestRate, interestFractionalRatePerInstallment);
        Money interest = nonGraceInterestPayments;
        if (graceType.equals(GraceType.PRINCIPALONLYGRACE)) {

            nonGraceInterestPayments = getDecliningInterestAmountNonGrace_v2(numOfInstallments-gracePeriodDuration, loanAmount, interestRate, interestFractionalRatePerInstallment);

            Money graceInterestPayments = getDecliningInterestAmountGrace_v2(loanAmount, gracePeriodDuration, interestFractionalRatePerInstallment);
            interest = graceInterestPayments.add(nonGraceInterestPayments);
        }
        return interest;
    }

    private Money getDecliningInterestAmountGrace_v2(Money loanAmount, Integer gracePeriodDuration, Double interestFractionalRatePerInstallment) {
        return loanAmount.multiply(interestFractionalRatePerInstallment).multiply(Double.valueOf(gracePeriodDuration.toString()));
    }

    private Money getDecliningInterestAmountNonGrace_v2(final int numNonGraceInstallments, Money loanAmount, Double interestRate, Double interestFractionalRatePerInstallment) {
        Money paymentPerPeriod = getPaymentPerPeriodForDecliningInterest_v2(numNonGraceInstallments, interestRate, loanAmount, interestFractionalRatePerInstallment);
        Money totalPayments = paymentPerPeriod.multiply((double) numNonGraceInstallments);
        return totalPayments.subtract(loanAmount);
    }

    /*
     * Calculates equal payments per period for fixed payment, declining-interest loan type. Uses formula from
     * http://confluence.mifos.org :9090/display/Main/Declining+Balance+Example+Calcs The formula is copied here: EMI =
     * P * i / [1- (1+i)^-n] where p = principal (amount of loan) i = rate of interest per installment period as a
     * decimal (not percent) n = no. of installments
     *
     * Translated into program variables and method calls:
     *
     * paymentPerPeriod = interestFractionalRatePerPeriod * getLoanAmount() / ( 1 - (1 +
     * interestFractionalRatePerPeriod) ^ (-getNoOfInstallments()))
     *
     * NOTE: Use double here, not BigDecimal, to calculate the factor that getLoanAmount() is multiplied by. Since
     * calculations all involve small quantities, 64-bit precision is sufficient. It is is more accurate to use
     * floating-point, for quantities of small magnitude (say for very small interest rates)
     *
     * NOTE: These calculations do not take into account EPI or grace period adjustments.
     */
    private Money getPaymentPerPeriodForDecliningInterest_v2(final int numInstallments, Double interestRate, Money loanAmount,
            Double interestFractionalRatePerInstallment) {

        double factor = 0.0;
        if (interestRate == 0.0) {
            Money paymentPerPeriod = loanAmount.divide(numInstallments);
            return paymentPerPeriod;
        }

        factor = interestFractionalRatePerInstallment / (1.0 - Math.pow(1.0 + interestFractionalRatePerInstallment, -numInstallments));

        Money paymentPerPeriod = loanAmount.multiply(factor);
        return paymentPerPeriod;
    }
}