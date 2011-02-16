package org.mifos.clientportfolio.newloan.domain;

import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.framework.util.helpers.Money;

public class AmountInstalmentFeeCalculator implements InstallmentFeeCalculator {

    @Override
    public Money calculate(Double feeAmount, Money loanAmount, Money loanInterest, FeeBO fee) {
        return new Money(loanAmount.getCurrency(), feeAmount.toString());
    }

}
