package org.mifos.clientportfolio.newloan.domain;

import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.service.BusinessRuleException;

public class LoanDurationInAccountingYearsCalculatorFactoryForDailyRecurrence implements
        LoanDurationInAccountingYearsCalculator {

    @Override
    public Double calculate(Integer recurEvery, Integer numberOfInstallments, Integer interestDays) {
        if (interestDays != AccountConstants.INTEREST_DAYS_360 && interestDays != AccountConstants.INTEREST_DAYS_365) {
            throw new BusinessRuleException(AccountConstants.NOT_SUPPORTED_INTEREST_DAYS);
        }

        double totalDays = numberOfInstallments * recurEvery;
        return totalDays / interestDays;
    }

}
