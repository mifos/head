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

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.savings.business.SavingsActivityEntity;
import org.mifos.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.config.AccountingRules;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;

public abstract class AbstractInterestCalculator implements InterestCalculator {

    @Override
    public List<EndOfDayBalance> getEndOfDayBalanceDetails(LocalDate sDate, LocalDate endDate,
            List<AccountTrxnEntity> trxns) {
        LocalDate startDate = sDate;

        List<EndOfDayBalance> balanceRecords = new ArrayList<EndOfDayBalance>();

        boolean hasFoundInitialBalance = false;
        SavingsTrxnDetailEntity initialTrxn = null;
        // scroll over the activities and fill end of day balance list
        int count = 0;
        for (AccountTrxnEntity accountTrxn : trxns) {
            count++;
            SavingsTrxnDetailEntity currentTrxn = (SavingsTrxnDetailEntity) accountTrxn;

            if (isAdjustment(currentTrxn)) {
                // ignore adjustment as it doesn't count in end of day balance
                continue;
            }

            // initial Activity is the activity which has starting(initial) balance
            if (!hasFoundInitialBalance) {
                if (date(currentTrxn).compareTo(startDate) <= 0) {
                    initialTrxn = currentTrxn;
                    if(trxns.size() == count) {
                        balanceRecords.add(new EndOfDayBalance(startDate, initialTrxn.getBalance()));
                        hasFoundInitialBalance = true;
                    }
                } else {
                    // we have crossed fromDate, if we are still in this condition then
                    if (initialTrxn == null) {
                        initialTrxn = currentTrxn;
                        startDate = date(currentTrxn);
                        if(trxns.size() == count) {
                            balanceRecords.add(new EndOfDayBalance(startDate, initialTrxn.getBalance()));
                            hasFoundInitialBalance = true;
                        }
                    } else {
                        balanceRecords.add(new EndOfDayBalance(startDate, initialTrxn.getBalance()));
                        hasFoundInitialBalance = true;
                    }
                }
            }

            if (hasFoundInitialBalance && date(currentTrxn).compareTo(startDate) > 0
                    && date(currentTrxn).compareTo(endDate) <= 0) {
                if (balanceRecords.get(balanceRecords.size() - 1).getDate().compareTo(date(currentTrxn)) == 0) {
                    balanceRecords.set(balanceRecords.size() - 1,
                            new EndOfDayBalance(date(currentTrxn), currentTrxn.getBalance()));
                } else {
                    balanceRecords.add(new EndOfDayBalance(date(currentTrxn), currentTrxn.getBalance()));
                }
            }
        }
        return balanceRecords;
    }

    protected LocalDate date(SavingsActivityEntity s) {
        return new LocalDate(s.getTrxnCreatedDate());
    }

    public boolean isAdjustment(SavingsActivityEntity activity) {
        return (activity.getActivity().asEnum() == AccountActionTypes.SAVINGS_ADJUSTMENT);
    }

    private LocalDate date(SavingsTrxnDetailEntity trxn) {
        return new LocalDate(trxn.getActionDate());
    }

    private boolean isAdjustment(SavingsTrxnDetailEntity trxn) {
        // TODO Auto-generated method stub
        return (trxn.getAccountAction() == AccountActionTypes.SAVINGS_ADJUSTMENT);
    }

    @Override
    public Money calculateInterest(final Money principal, final Double interestRate,
            final LocalDate startDate, final LocalDate endDate) {

        validateData(startDate, "startDate");
        validateData(principal, "principal");
        validateData(endDate, "endDate");
        validateData(interestRate, "interestRate");

        int days = Days.daysBetween(startDate, endDate).getDays();

        if (days < 0) {
            throw new IllegalArgumentException("Invalid invalid period, start date should alway be smaller/before than end date");
        }

        return getInterest(principal, interestRate, days);
    }

    protected void validateData(Object data, String msg) {
        if (data == null) {
            throw new IllegalArgumentException("null value encountered for " + msg);
        }

    }

    public Money getInterest(final Money principal, final double interestRate, final int duration) {
        double intRate = interestRate;
        intRate = intRate / AccountingRules.getNumberOfInterestDays() * duration;
        Money interestAmount = principal.multiply(new Double(1 + intRate / 100.0)).subtract(principal);

        interestAmount = MoneyUtils.currencyRound(interestAmount);
        return interestAmount;
    }
}
