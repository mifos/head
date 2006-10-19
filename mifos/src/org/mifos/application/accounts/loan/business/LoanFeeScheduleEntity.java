package org.mifos.application.accounts.loan.business;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.framework.util.helpers.Money;

public class LoanFeeScheduleEntity extends AccountFeesActionDetailEntity {

	protected LoanFeeScheduleEntity() {
		super(null, null, null, null);
	}

	public LoanFeeScheduleEntity(AccountActionDateEntity accountActionDate,
			FeeBO fee, AccountFeesEntity accountFee, Money feeAmount) {
		super(accountActionDate, fee, accountFee, feeAmount);
	}

	@Override
	protected void setFeeAmount(Money feeAmount) {
		super.setFeeAmount(feeAmount);
	}

	@Override
	protected void setFeeAmountPaid(Money feeAmountPaid) {
		super.setFeeAmountPaid(feeAmountPaid);
	}

	@Override
	protected void makePayment(Money feePaid) {
		super.makePayment(feePaid);
	}

	@Override
	protected void makeRepaymentEnteries(String payFullOrPartial) {
		super.makeRepaymentEnteries(payFullOrPartial);
	}

	@Override
	protected Money waiveCharges() {
		return super.waiveCharges();
	}

}
