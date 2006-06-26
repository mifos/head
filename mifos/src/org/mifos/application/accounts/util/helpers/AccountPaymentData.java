package org.mifos.application.accounts.util.helpers;

import org.mifos.application.accounts.business.AccountActionDateEntity;


public abstract class AccountPaymentData {

	private Short installmentId;

	private Short paymentStatus;

	public Short getInstallmentId() {
		return installmentId;
	}

	protected void setInstallmentId(Short installmentId) {
		this.installmentId = installmentId;
	}

	public Short getPaymentStatus() {
		return paymentStatus;
	}

	protected void setPaymentStatus(Short paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	
	public  AccountPaymentData(AccountActionDateEntity accountActionDate) {
		if(accountActionDate != null)
			setInstallmentId(accountActionDate.getInstallmentId());
			setPaymentStatus(AccountConstants.PAYMENT_PAID);
	}
} 
