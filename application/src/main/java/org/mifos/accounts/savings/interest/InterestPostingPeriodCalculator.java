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

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;

public class InterestPostingPeriodCalculator implements CompoundingInterestCalculator {

    private final MifosCurrency currency;
    private final NonCompoundingInterestCalculator interestCalculationPeriodCalculator;

    public InterestPostingPeriodCalculator(NonCompoundingInterestCalculator interestCalculationPeriodCalculator, MifosCurrency currency) {
        this.interestCalculationPeriodCalculator = interestCalculationPeriodCalculator;
        this.currency = currency;
    }

    @Override
    public List<InterestPostingPeriodResult> calculatePostingPeriodDetails(List<EndOfDayDetail> endOfDayDetailsForPeriods, List<InterestCalculationInterval> postingPeriods) {

        List<InterestPostingPeriodResult> allInterestPostings = new ArrayList<InterestPostingPeriodResult>();

        Money zero = Money.zero(currency);
        Money periodAccountBalance = zero;

        for (InterestCalculationInterval postingPeriod : postingPeriods) {
            InterestPostingPeriodResult postingPeriodResult = calculateInterestForPosting(postingPeriod, endOfDayDetailsForPeriods, periodAccountBalance);
            allInterestPostings.add(postingPeriodResult);
            periodAccountBalance = postingPeriodResult.getPeriodBalance();

            // NOTE - here we are checking if current interest calculation for period differs from the sum of previous interest posted for period.
            // so inline we add it to the balance (i.e. take it that the correct interest is posted) and continue to check all other posting periods.
            // NOTE - The interest can differ due to back-date transactions, adjustments, previous incorrect interest postings etc.
            if (postingPeriodResult.isTotalCalculatedInterestIsDifferent()) {
                periodAccountBalance = periodAccountBalance.add(postingPeriodResult.getDifferenceInInterest());
            }
        }

        return allInterestPostings;

    }

    private InterestPostingPeriodResult calculateInterestForPosting(InterestCalculationInterval postingPeriod,
            List<EndOfDayDetail> endOfDayDetailsForPostingPeriod, Money periodAccountBalace) {

        InterestPostingPeriodResult interestPostingPeriodResult = new InterestPostingPeriodResult(postingPeriod);

        Money totalBalanceBeforeInterestCalculation = periodAccountBalace;

        List<InterestCalculationPeriodResult> calculationPeriodResults = this.interestCalculationPeriodCalculator.calculateDetails(postingPeriod, totalBalanceBeforeInterestCalculation, endOfDayDetailsForPostingPeriod);
        for (InterestCalculationPeriodResult interestCalculationPeriodResult : calculationPeriodResults) {
            interestPostingPeriodResult.add(interestCalculationPeriodResult);
            totalBalanceBeforeInterestCalculation = totalBalanceBeforeInterestCalculation.add(interestCalculationPeriodResult.getTotalPrincipal());
        }

        interestPostingPeriodResult.setPeriodBalance(totalBalanceBeforeInterestCalculation);

        return interestPostingPeriodResult;
    }
}