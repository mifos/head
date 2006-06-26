package org.mifos.application.accounts.util.helpers;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.framework.util.helpers.Money;

public class SavingsPaymentData extends AccountPaymentData {
	private Money depositPaid;

	public Money getDepositPaid() {
		return depositPaid;
	}

	private void setDepositPaid(Money depositPaid) {
		this.depositPaid = depositPaid;
	}

	public SavingsPaymentData(
			AccountActionDateEntity accountActionDate) {
		super(accountActionDate);
		if(accountActionDate != null)
			setDepositPaid(accountActionDate.getTotalDepositDue());
	}
}
