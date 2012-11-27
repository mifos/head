package org.mifos.clientportfolio.newloan.domain;

public class LoanDecliningInterestAnnualPeriodCalculatorForDailyRecurrence implements
        LoanDecliningInterestAnnualPeriodCalculator {

    @Override
    public Double calculate(Integer recurEvery, Integer interestDays) {
        return interestDays.doubleValue() / recurEvery.doubleValue();
    }

}
