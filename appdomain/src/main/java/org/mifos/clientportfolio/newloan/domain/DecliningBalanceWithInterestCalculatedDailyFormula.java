package org.mifos.clientportfolio.newloan.domain;

import java.math.BigDecimal;

import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.mifos.framework.util.helpers.Money;

/**
 * (principal outstanding / 100 * periodic interest rate) * number of periods
 */
public class DecliningBalanceWithInterestCalculatedDailyFormula implements InterestCalculationForumula {

    @Override
    public Money calculate(Money principalOutstanding, Double aprInterestRate, LocalDate interestPeriodStartDate, LocalDate interestPeriodEndDate) {
        
        Interval installmentPeriod = new Interval(interestPeriodStartDate.toDateMidnight().toDateTime(), interestPeriodEndDate.toDateMidnight().toDateTime()); 
        Integer installmentPeriodDuration = Days.daysIn(installmentPeriod).getDays();
        
        BigDecimal periodicInterestRate = BigDecimal.valueOf(aprInterestRate / Double.valueOf("365.0"));
        
        BigDecimal interestDue = principalOutstanding.getAmount().divide(BigDecimal.valueOf(Long.valueOf("100"))).multiply(periodicInterestRate).multiply(BigDecimal.valueOf(installmentPeriodDuration.doubleValue()));
        
//        BigDecimal interestRateFractionForInstallmentPeriodDuration = periodicInterestRate.multiply(BigDecimal.valueOf(installmentPeriodDuration.doubleValue()));
//        
//        BigDecimal interestDue = principalOutstanding.getAmount().multiply(interestRateFractionForInstallmentPeriodDuration);
        
        return new Money(principalOutstanding.getCurrency(), interestDue);
    }
}