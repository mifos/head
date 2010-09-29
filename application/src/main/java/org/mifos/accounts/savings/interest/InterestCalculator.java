package org.mifos.accounts.savings.interest;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.framework.util.helpers.Money;

public interface InterestCalculator {

    Money calculateInterest(Money principal, Double interestRate, LocalDate startDate, LocalDate endDate);

    Money getPrincipal(List<EndOfDayBalance> balanceRecords, LocalDate fromDate, LocalDate toDate);

    List<EndOfDayBalance> getEndOfDayBalanceDetails(LocalDate sDate, LocalDate endDate, List<AccountTrxnEntity> orderedList);

    // -------------------------------------------------------------------------

    BigDecimal calcInterest(InterestCalculationRange interestCalculationRange, EndOfDayDetail... depositDetail);

}
