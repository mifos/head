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

import java.util.ArrayList;
import java.util.List;

import org.mifos.framework.util.helpers.Money;

public class InterestPostingPeriodResult {

    private Money periodBalance;
    private List<InterestCalculationPeriodResult> interestCalulcationResults = new ArrayList<InterestCalculationPeriodResult>();
    private final InterestCalculationInterval postingPeriod;

    public InterestPostingPeriodResult(InterestCalculationInterval postingPeriod) {
        this.postingPeriod = postingPeriod;
    }

    public void add(InterestCalculationPeriodResult interestCalculationPeriodResult) {
        this.interestCalulcationResults.add(interestCalculationPeriodResult);
    }

    public Money getPeriodBalance() {
        return this.periodBalance;
    }

    public void setPeriodBalance(Money periodBalance) {
        this.periodBalance = periodBalance;
    }

    public InterestCalculationInterval getPostingPeriod() {
        return this.postingPeriod;
    }

    public Money getPeriodInterest() {
        Money balance = Money.zero();
        for (InterestCalculationPeriodResult result : interestCalulcationResults) {
            balance = balance.add(result.getInterest());
        }

        return balance;
    }

    public boolean isTotalCalculatedInterestIsDifferent() {
        return getPeriodInterest().isLessThan(getPreviousPeriodInterest()) || getPeriodInterest().isGreaterThan(getPreviousPeriodInterest());
    }

    public Money getDifferenceInInterest() {
        return getPeriodInterest().subtract(getPreviousPeriodInterest());
    }

    private Money getPreviousPeriodInterest() {
        Money balance = Money.zero();
        for (InterestCalculationPeriodResult result : interestCalulcationResults) {
            balance = balance.add(result.getPreviousTotalInterestForPeriod());
        }

        return balance;

    }

    @Override
    public String toString() {
        return new StringBuilder().append(postingPeriod)
                                  .append(" end balance: ").append(periodBalance)
                                  .append(" calculated interest: ").append(getPeriodInterest())
                                  .append(" previous interest: ").append(getPreviousPeriodInterest())
                                  .toString();
    }
}