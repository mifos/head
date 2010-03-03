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

import org.mifos.accounts.savings.interest.schedule.InterestScheduledEvent;
import org.mifos.framework.util.helpers.Money;

/**
 * I am responsible for working out how much interest should be applied to a given calculation period.
 */
public class InterestCalculationPeriodCalculator implements NonCompoundingInterestCalculator {

    private final InterestCalculator interestCalculator;
    private final InterestScheduledEvent interestCalculationSchedule;
    private final InterestCalculationIntervalHelper interestCalculationIntervalHelper;

    public InterestCalculationPeriodCalculator(InterestCalculator interestCalculator,
            InterestScheduledEvent interestCalculationSchedule,
            InterestCalculationIntervalHelper interestCalculationIntervalHelper) {
        this.interestCalculator = interestCalculator;
        this.interestCalculationSchedule = interestCalculationSchedule;
        this.interestCalculationIntervalHelper = interestCalculationIntervalHelper;
    }

    /**
     * I do this by determining all legal 'calculation periods' within a given {@link InterestCalculationInterval} time period.
     *
     * For each 'interest calculation period' derived, I create a {@link InterestCalculationPeriodDetail} which will hold all the information
     * necessary for the {@link InterestCalculator} to return a {@link InterestCalculationPeriodResult}.
     */
    @Override
    public List<InterestCalculationPeriodResult> calculateDetails(InterestCalculationInterval calculationPeriod,
            Money totalBalanceBeforeCalculationPeriod, List<EndOfDayDetail> endOfDayDetailsForCalculationPeriod) {

        List<InterestCalculationPeriodResult> calculationPeriodResults = new ArrayList<InterestCalculationPeriodResult>();

        List<InterestCalculationInterval> allPossible = interestCalculationIntervalHelper.determineAllPossiblePeriods(
                calculationPeriod.getStartDate(), this.interestCalculationSchedule, calculationPeriod.getEndDate());

        Money runningBalance = totalBalanceBeforeCalculationPeriod;
        for (InterestCalculationInterval interestCalculationPeriod : allPossible) {

            InterestCalculationPeriodDetail interestCalculationPeriodDetail = InterestCalculationPeriodDetail
                    .populatePeriodDetailBasedOnInterestCalculationInterval(interestCalculationPeriod,
                            endOfDayDetailsForCalculationPeriod, runningBalance);

            InterestCalculationPeriodResult calculationPeriodResult = interestCalculator.calculateSavingsDetailsForPeriod(interestCalculationPeriodDetail);

            calculationPeriodResults.add(calculationPeriodResult);
            runningBalance = runningBalance.add(calculationPeriodResult.getTotalPrincipal());
        }

        return calculationPeriodResults;
    }
}