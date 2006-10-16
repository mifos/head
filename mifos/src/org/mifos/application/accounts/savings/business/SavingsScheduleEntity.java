package org.mifos.application.accounts.savings.business;

import java.sql.Date;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.framework.util.helpers.Money;

public class SavingsScheduleEntity extends AccountActionDateEntity {
	protected Money deposit;

	protected Money depositPaid;

	protected SavingsScheduleEntity() {
		super(null, null, null, null, null);
	}

	public SavingsScheduleEntity(AccountBO account, CustomerBO customer,
			Short installmentId, Date actionDate, PaymentStatus paymentStatus,
			Money deposit) {
		super(account, customer, installmentId, actionDate, paymentStatus);
		this.deposit = deposit;
	}

	public Money getDeposit() {
		return deposit;
	}

	void setDeposit(Money deposit) {
		this.deposit = deposit;
	}

	public Money getDepositPaid() {
		return depositPaid;
	}

	void setDepositPaid(Money depositPaid) {
		this.depositPaid = depositPaid;
	}

	public Money getTotalDepositDue() {
		return getDeposit().subtract(getDepositPaid());
	}

	void setPaymentDetails(Money depositAmount,
			PaymentStatus paymentStatus, Date paymentDate) {
		this.depositPaid = this.depositPaid.add(depositAmount);
		this.paymentStatus = paymentStatus.getValue();
		this.paymentDate = paymentDate;
	}

	void waiveDepositDue() {
		Money depositDue = getTotalDepositDue();
		deposit = deposit.subtract(depositDue);
		setPaymentStatus(PaymentStatus.PAID.getValue());
	}
	
	@Override
	protected void setActionDate(Date actionDate) {
		super.setActionDate(actionDate);
	}

	@Override
	protected void setPaymentDate(Date paymentDate) {
		super.setPaymentDate(paymentDate);
	}

	@Override
	protected void setPaymentStatus(Short paymentStatus) {
		super.setPaymentStatus(paymentStatus);
	}
}
