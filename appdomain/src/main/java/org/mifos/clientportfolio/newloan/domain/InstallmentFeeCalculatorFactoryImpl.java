package org.mifos.clientportfolio.newloan.domain;

import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;

public class InstallmentFeeCalculatorFactoryImpl implements InstallmentFeeCalculatorFactory {

    @Override
    public InstallmentFeeCalculator create(FeeDao feeDao, RateAmountFlag feeType) {

        switch (feeType) {
        case AMOUNT:
            return new AmountInstalmentFeeCalculator();
        case RATE:
            return new RateInstalmentFeeCalculator(feeDao);
        default:
            throw new UnsupportedOperationException("unsupported rateAmountFlag: " + feeType);
        }
    }

}
