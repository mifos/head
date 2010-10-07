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

public class SavingsInterestCalculatorFactory {

    public static InterestCalculator create(SavingsInterestDetail interestDetail) {

        SimpleInterestCalculationStrategy principalBasedInterestCalculation = new SimpleInterestCalculationStrategy(interestDetail.getInterestRate(), interestDetail.getAccountingNumberOfDaysInYear());
        InterestCalucationRule minimumBalanceForInterestCalculation = new MinimumBalanceInterestCalculationRule(interestDetail.getMinimumBalanceRequired());
        PrincipalCalculationStrategy principalCalculationStrategy = new AverageBalanceCaluclationStrategy();
        switch (interestDetail.getInterestCalcType()) {
        case AVERAGE_BALANCE:
            principalCalculationStrategy = new AverageBalanceCaluclationStrategy();
            break;
        case MINIMUM_BALANCE:
            principalCalculationStrategy = new MinimumBalanceCalculationStrategy();
            break;
        default:
            break;
        }

        return new SavingsInterestCalculator(principalCalculationStrategy, principalBasedInterestCalculation, minimumBalanceForInterestCalculation);
    }
}