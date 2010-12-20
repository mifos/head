package org.mifos.accounts.loan.business;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.mifos.framework.util.helpers.Money;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "calculated_interest_on_payment")
public class CalculatedInterestOnPayment implements Serializable {
    @Id
    @OneToOne
    @JoinColumn(name = "loan_account_trxn_id")
    private LoanTrxnDetailEntity loanTrxnDetailEntity;

    @Type(type = "org.mifos.framework.util.helpers.MoneyCompositeUserType")
    @Columns(columns = {
            @Column(name = "original_interest_currency_id"),
            @Column(name = "original_interest")
    })
    private Money originalInterest;

    @Type(type = "org.mifos.framework.util.helpers.MoneyCompositeUserType")
    @Columns(columns = {
            @Column(name = "extra_interest_paid_currency_id"),
            @Column(name = "extra_interest_paid")
    })
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
