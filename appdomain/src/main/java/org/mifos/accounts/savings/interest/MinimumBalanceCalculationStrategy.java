/*
 * Copyright Grameen Foundation USA
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

import org.apache.commons.collections.CollectionUtils;
import org.mifos.framework.util.helpers.Money;

public class MinimumBalanceCalculationStrategy implements PrincipalCalculationStrategy {

    @Override
    public Money calculatePrincipal(InterestCalculationPeriodDetail interestCalculationPeriodDetail) {

        validateInterestCalculationPeriodDetail(interestCalculationPeriodDetail);

        Money minimumBalance = interestCalculationPeriodDetail.getBalanceBeforeInterval();

        if(!interestCalculationPeriodDetail.isFirstActivityBeforeInterval() && CollectionUtils.isNotEmpty(interestCalculationPeriodDetail.getDailyDetails())){
            minimumBalance = interestCalculationPeriodDetail.getDailyDetails().get(0).getResultantAmountForDay();
        }

        Money runningBalance = interestCalculationPeriodDetail.getBalanceBeforeInterval();

        for (EndOfDayDetail daily : interestCalculationPeriodDetail.getDailyDetails()) {
                runningBalance = runningBalance.add(daily.getResultantAmountForDay());

            if (minimumBalance.isGreaterThan(runningBalance)) {
                minimumBalance = runningBalance;
            }

        }

        return minimumBalance;
    }

    private void validateInterestCalculationPeriodDetail(InterestCalculationPeriodDetail interestCalculationPeriodDetail) {
        if(interestCalculationPeriodDetail == null) {
            throw new IllegalArgumentException("interestCalculationPeriodDetail should not be null");
        }

        if(interestCalculationPeriodDetail.getDailyDetails() == null) {
            throw new IllegalArgumentException("dailyDetail list should not be null");
        }

        if(interestCalculationPeriodDetail.getBalanceBeforeInterval() == null) {
            throw new IllegalArgumentException("balanceBeforeInterval list should not be null");
        }
    }
}
