package org.mifos.application.servicefacade;

import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.framework.util.helpers.Money;

import java.util.List;

public class RepayLoanDto {
    private Money earlyRepaymentMoney;
    private Money waivedRepaymentMoney;
    private List<PaymentTypeEntity> paymentTypeEntities;
    private boolean waiverInterest;

    public RepayLoanDto(Money earlyRepaymentMoney, Money waivedRepaymentMoney, List<PaymentTypeEntity> paymentTypeEntities, boolean waiverInterest) {
        this.earlyRepaymentMoney = earlyRepaymentMoney;
        this.waivedRepaymentMoney = waivedRepaymentMoney;
        this.paymentTypeEntities = paymentTypeEntities;
        this.waiverInterest = waiverInterest;
    }

    public Money getEarlyRepaymentMoney() {
        return earlyRepaymentMoney;
    }

    public Money getWaivedRepaymentMoney() {
        return waivedRepaymentMoney;
    }

    public List<PaymentTypeEntity> getPaymentTypeEntities() {
        return paymentTypeEntities;
    }

    public boolean shouldWaiverInterest() {
        return waiverInterest;
    }
}
