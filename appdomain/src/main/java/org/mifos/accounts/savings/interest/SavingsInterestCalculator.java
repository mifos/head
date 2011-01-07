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
    private final SimpleInterestCalculationStrategy principalBasedInterestCalculation;
    private final InterestCalucationRule minimumBalanceRule;

    public SavingsInterestCalculator(PrincipalCalculationStrategy principalCalculationStrategy, SimpleInterestCalculationStrategy principalBasedInterestCalculation, InterestCalucationRule minimumBalanceRule) {
        this.principalCalculationStrategy = principalCalculationStrategy;
        this.principalBasedInterestCalculation = principalBasedInterestCalculation;
        this.minimumBalanceRule = minimumBalanceRule;
    }

    @Override
    public InterestCalculationPeriodResult calculateSavingsDetailsForPeriod(InterestCalculationPeriodDetail interestCalculationPeriodDetail) {

        Money calculatedInterest = interestCalculationPeriodDetail.zeroAmount();

        Money totalBalanceForPeriod = interestCalculationPeriodDetail.sumOfPrincipal();
        Money totalInterestForPeriod = interestCalculationPeriodDetail.sumOfInterest();
        Money principal = principalCalculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);
        if (minimumBalanceRule.isCalculationAllowed(principal)) {
            calculatedInterest = principalBasedInterestCalculation.calculateInterest(principal, interestCalculationPeriodDetail.getDuration());
        }

        return new InterestCalculationPeriodResult(interestCalculationPeriodDetail.getInterval(), calculatedInterest, principal, totalBalanceForPeriod, totalInterestForPeriod);
    }
}