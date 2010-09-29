package org.mifos.accounts.savings.interest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.mifos.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.framework.util.helpers.Money;

public class MinimumBalanceInterestCalculator extends AbstractInterestCalculator {

    @Override
    public Money getPrincipal(List<EndOfDayBalance> balanceRecords, final LocalDate calculationStartDate, final LocalDate calculationEndDate) {
        Money minimumBalance = null;

        validateData(balanceRecords,"EndOfDayBalance list");
        validateData(calculationStartDate,"EndOfDayBalance list");
        validateData(calculationEndDate,"EndOfDayBalance list");

        for (EndOfDayBalance balance : balanceRecords) {
            if (minimumBalance == null) {
                minimumBalance = balance.getBalance();
            } else {
                if (minimumBalance.isGreaterThan(balance.getBalance())) {
                    minimumBalance = balance.getBalance();
                }
            }
        }

        return minimumBalance;
    }

    @Override
    public BigDecimal calcInterest(InterestCalculationRange interestCalculationRange, EndOfDayDetail... deposit) {
        // TODO Auto-generated method stub
        return null;
    }

}
