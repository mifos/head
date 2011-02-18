package org.mifos.clientportfolio.newloan.domain;

import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;

public interface InstallmentFeeCalculatorFactory {

    InstallmentFeeCalculator create(FeeDao feeDao, RateAmountFlag feeType);

}
