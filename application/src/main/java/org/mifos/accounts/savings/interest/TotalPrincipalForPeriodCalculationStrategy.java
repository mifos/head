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

import java.util.List;

import org.mifos.framework.util.helpers.Money;

/**
 * I am responsible for returning the total principal for a given {@link InterestCalculationPeriodDetail}.
 */
public class TotalPrincipalForPeriodCalculationStrategy implements PrincipalCalculationStrategy {

    @Override
    public Money calculatePrincipal(InterestCalculationPeriodDetail interestCalculationPeriodDetail) {

        Money totalPrincipal = Money.zero(interestCalculationPeriodDetail.getBalanceBeforeInterval().getCurrency());
        // FIXME - keithw - this class is only supposed to return principal for a given period.
        // implement a different implementation class if you need running principal behaviour at this level.
//        Money totalPrincipal = interestCalculationPeriodDetail.getBalanceBeforeInterval();

        List<EndOfDayDetail> dailyDetails = interestCalculationPeriodDetail.getDailyDetails();
        for (EndOfDayDetail endOfDayDetail : dailyDetails) {
            totalPrincipal = totalPrincipal.add(endOfDayDetail.getResultantAmountForDay());
        }

        return totalPrincipal;
    }

}
