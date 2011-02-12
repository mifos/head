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

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;

/**
 * I am responsible for implementing interest compounding for savings.
 *
 * This occurs when posting interest to savings account.
 *
 * This calculator handles this 'posting' inline so it may calculate posting period that occur after it.
 */
public class InterestPostingPeriodCalculator implements CompoundingInterestCalculator {

    private final MifosCurrency currency;
    private final NonCompoundingInterestCalculator interestCalculationPeriodCalculator;

    public InterestPostingPeriodCalculator(NonCompoundingInterestCalculator interestCalculationPeriodCalculator, MifosCurrency currency) {
        this.interestCalculationPeriodCalculator = interestCalculationPeriodCalculator;
        this.currency = currency;
    }

    @Override
    public List<InterestPostingPeriodResult> calculateAllPostingPeriodDetails(List<EndOfDayDetail> endOfDayDetailsForPeriods, List<CalendarPeriod> postingPeriods) {

        List<InterestPostingPeriodResult> allInterestPostings = new ArrayList<InterestPostingPeriodResult>();

        Money zero = Money.zero(currency);
        Money periodAccountBalance = zero;

        for (CalendarPeriod postingPeriod : postingPeriods) {
            InterestPostingPeriodResult postingPeriodResult = calculateAllCalculationPeriodsForPostingPeriod(postingPeriod, endOfDayDetailsForPeriods, periodAccountBalance);
            allInterestPostings.add(postingPeriodResult);
            periodAccountBalance = postingPeriodResult.getPeriodBalance();

            if (postingPeriodResult.isTotalCalculatedInterestIsDifferent()) {
                periodAccountBalance = periodAccountBalance.add(postingPeriodResult.getDifferenceInInterest());
            }
        }

        return allInterestPostings;

    }

    private InterestPostingPeriodResult calculateAllCalculationPeriodsForPostingPeriod(CalendarPeriod postingPeriod, List<EndOfDayDetail> endOfDayDetailsForPostingPeriod, Money periodAccountBalace) {

        InterestPostingPeriodResult interestPostingPeriodResult = new InterestPostingPeriodResult(postingPeriod);

        Money interestCalculationRunningBalance = periodAccountBalace;

        List<InterestCalculationPeriodResult> calculationPeriodResults = this.interestCalculationPeriodCalculator.calculateDetails(postingPeriod, interestCalculationRunningBalance, endOfDayDetailsForPostingPeriod);
        for (InterestCalculationPeriodResult interestCalculationPeriodResult : calculationPeriodResults) {
            interestPostingPeriodResult.add(interestCalculationPeriodResult);
            interestCalculationRunningBalance = interestCalculationRunningBalance.add(interestCalculationPeriodResult.getTotalPrincipal());
        }

        interestPostingPeriodResult.setPeriodBalance(interestCalculationRunningBalance);

        return interestPostingPeriodResult;
    }
}