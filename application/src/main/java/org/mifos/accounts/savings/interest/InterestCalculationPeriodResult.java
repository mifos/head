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

public class InterestCalculationPeriodResult {

    private final InterestCalculationInterval interestCalculationInterval;
    private final Money interest;
    private final Money averagePrincipal;
    private final Money totalPrincipal;
    private final Money previousTotalInterestForPeriod;

    public InterestCalculationPeriodResult(InterestCalculationInterval interestCalculationInterval, Money calculatedInterest, Money averagePrincipal, Money totalPrincipal, Money previousTotalInterestForPeriod) {
        this.interestCalculationInterval = interestCalculationInterval;
        this.interest = calculatedInterest;
        this.averagePrincipal = averagePrincipal;
        this.totalPrincipal = totalPrincipal;
        this.previousTotalInterestForPeriod = previousTotalInterestForPeriod;
    }

    public Money getInterest() {
        return this.interest;
    }

    public Money getAveragePrincipal() {
        return this.averagePrincipal;
    }

    public Money getTotalPrincipal() {
        return this.totalPrincipal;
    }

    public Money getPreviousTotalInterestForPeriod() {
        return this.previousTotalInterestForPeriod;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(this.interestCalculationInterval).append(" >> total principal for period: ").append(totalPrincipal)
                                  .append(" interest calculated: ").append(interest)
                                  .append(" previous interest: ").append(previousTotalInterestForPeriod)
                                  .append(" avg principal: ").append(averagePrincipal).toString();
    }
}