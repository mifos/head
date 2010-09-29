package org.mifos.accounts.savings.interest;

import org.mifos.framework.util.helpers.Money;

public class MinimumBalanceCaluclationStrategy implements PrincipalCalculationStrategy {

    @Override
    public Money calculatePrincipal(InterestCalculationRange interestCalculationRange, EndOfDayDetail[] activities) {

        Money minimumBalance = null;
        Money runningBalance = null;

              for (EndOfDayDetail activity : activities) {
                  if(runningBalance == null) {
                      runningBalance = activity.getResultantAmountForDay();
                  } else {
                      runningBalance = runningBalance.add(activity.getResultantAmountForDay());
                  }
                  if (minimumBalance == null) {
                      // initial minimum balance
                      minimumBalance = runningBalance;
                  } else {
                      if (minimumBalance.isGreaterThan(runningBalance)) {
                          minimumBalance = runningBalance;
                      }
                  }
              }

              return minimumBalance;
    }
}
