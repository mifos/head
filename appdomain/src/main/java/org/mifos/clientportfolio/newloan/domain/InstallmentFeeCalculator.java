package org.mifos.clientportfolio.newloan.domain;

import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.framework.util.helpers.Money;

public interface InstallmentFeeCalculator {

    Money calculate(Double feeAmount, Money loanAmount, Money loanInterest, FeeBO fee);

}
