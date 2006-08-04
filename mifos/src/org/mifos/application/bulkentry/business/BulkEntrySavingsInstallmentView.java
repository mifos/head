package org.mifos.application.bulkentry.business;

import java.util.Date;

import org.mifos.framework.util.helpers.Money;

public class BulkEntrySavingsInstallmentView extends BulkEntryInstallmentView {

	private final Money deposit;

	private final Money depositPaid;

	public BulkEntrySavingsInstallmentView(Integer accountId,
			Integer customerId, Short installmentId, Integer actionDateId,
			Date actionDate, Money deposit, Money depositPaid) {
		super(accountId, customerId, installmentId, actionDateId, actionDate);
		this.deposit = deposit;
		this.depositPaid = depositPaid;
	}

	public BulkEntrySavingsInstallmentView(Integer accountId, Integer customerId) {
		super(accountId, customerId, null, null, null);
		this.deposit = null;
		this.depositPaid = null;
	}

	public Money getDeposit() {
		return deposit;
	}

	public Money getDepositPaid() {
		return depositPaid;
	}

	public Money getTotalDepositDue() {
		return getDeposit().subtract(getDepositPaid());
	}

}
