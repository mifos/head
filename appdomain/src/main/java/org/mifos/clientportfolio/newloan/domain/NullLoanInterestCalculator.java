package org.mifos.clientportfolio.newloan.domain;

import org.mifos.framework.util.helpers.Money;

public class NullLoanInterestCalculator implements LoanInterestCalculator {

    @Override
    public Money calculate(@SuppressWarnings("unused") LoanInterestCalculationDetails loanInterestCalculationDetails) {
        return null;
    }

}