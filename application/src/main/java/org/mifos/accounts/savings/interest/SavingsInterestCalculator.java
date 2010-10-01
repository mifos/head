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

package org.mifos.accounts.savings.interest;

import org.mifos.framework.util.helpers.Money;

public class SavingsInterestCalculator implements InterestCalculator {

    private final PrincipalCalculationStrategy principalCalculationStrategy;
    private CompoundInterestCalculationStrategy compoundInterestCaluclation = new CompoundInterestCalculationStrategy();
    private PrincipalCalculationStrategy totalPrincipalCalculationStrategy = new TotalBalanceCalculationStrategy();

    public SavingsInterestCalculator(PrincipalCalculationStrategy principalCalculationStrategy) {
        this.principalCalculationStrategy = principalCalculationStrategy;
    }

    @Override
    public InterestCalculationPeriodResult calculateSavingsDetailsForPeriod(InterestCalculationPeriodDetail interestCalculationPeriodDetail) {

        Money calculatedInterest = interestCalculationPeriodDetail.zeroAmount();

        Money totalBalanceForPeriod = totalPrincipalCalculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);
        Money principal = principalCalculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);
        if (interestCalculationPeriodDetail.isMinimumBalanceReached(principal)) {
            calculatedInterest = compoundInterestCaluclation.calculateInterest(principal, interestCalculationPeriodDetail.getInterestRate(), interestCalculationPeriodDetail.getDuration());
        }

        return new InterestCalculationPeriodResult(calculatedInterest, principal, totalBalanceForPeriod);
    }

    public void setCompoundInterestCaluclation(CompoundInterestCalculationStrategy compoundInterestCaluclation) {
        this.compoundInterestCaluclation = compoundInterestCaluclation;
    }

    public void setTotalPrincipalCalculationStrategy(PrincipalCalculationStrategy totalPrincipalCalculationStrategy) {
        this.totalPrincipalCalculationStrategy = totalPrincipalCalculationStrategy;
    }
}