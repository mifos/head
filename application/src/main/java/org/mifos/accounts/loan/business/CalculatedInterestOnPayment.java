package org.mifos.accounts.loan.business;

import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.helpers.Money;

public class CalculatedInterestOnPayment extends AbstractEntity {

    private Integer loanTrxnDetailEntityId;

    private LoanTrxnDetailEntity loanTrxnDetailEntity;

    private Money originalInterest;

    private Money extraInterestPaid;

    public CalculatedInterestOnPayment() {
    }

    public LoanTrxnDetailEntity getLoanTrxnDetailEntity() {
        return loanTrxnDetailEntity;
    }

    public void setLoanTrxnDetailEntity(LoanTrxnDetailEntity loanTrxnDetailEntity) {
        this.loanTrxnDetailEntity = loanTrxnDetailEntity;
    }

    public Money getOriginalInterest() {
        return originalInterest;
    }

    public void setOriginalInterest(Money originalInterest) {
        this.originalInterest = originalInterest;
    }

    public Money getExtraInterestPaid() {
        return extraInterestPaid;
    }

    public void setExtraInterestPaid(Money extraInterestPaid) {
        this.extraInterestPaid = extraInterestPaid;
    }
}
