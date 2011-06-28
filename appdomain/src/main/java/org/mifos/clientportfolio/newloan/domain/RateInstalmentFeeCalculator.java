package org.mifos.clientportfolio.newloan.domain;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.RateFeeBO;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.framework.util.helpers.Money;

public class RateInstalmentFeeCalculator implements InstallmentFeeCalculator {

    private final FeeDao feeDao;

    public RateInstalmentFeeCalculator(FeeDao feeDao) {
        this.feeDao = feeDao;
    }
    
    @Override
    public Money calculate(Double feeRate, Money loanAmount, Money loanInterest, FeeBO fee) {
        // make sure that we are not using a proxied object when we cast
        RateFeeBO rateFeeBO = (RateFeeBO) feeDao.initializeAndUnproxy(fee);
        FeeFormulaEntity formula = rateFeeBO.getFeeFormula();

        Money amountToCalculateOn = new Money(loanAmount.getCurrency(), "1.0");
        if (formula.getId().equals(FeeFormula.AMOUNT.getValue())) {
            amountToCalculateOn = loanAmount;
        } else if (formula.getId().equals(FeeFormula.AMOUNT_AND_INTEREST.getValue())) {
            amountToCalculateOn = loanAmount.add(loanInterest);
        } else if (formula.getId().equals(FeeFormula.INTEREST.getValue())) {
            amountToCalculateOn = loanInterest;
        }
        return amountToCalculateOn.multiply(feeRate).divide(100);
    }

}
