package org.mifos.application.accounts.util.helpers;

import java.util.HashMap;
import java.util.Map;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.framework.util.helpers.Money;

public class CustomerAccountPaymentData extends AccountPaymentData {

	private Money miscFeePaid;

	private Money miscPenaltyPaid;

	private Map<Short, Money> feesPaid;

	public Map<Short, Money> getFeesPaid() {
		return feesPaid;
	}

	private void setFeesPaid(Map<Short, Money> feesPaid) {
		this.feesPaid = feesPaid;
	}

	public Money getMiscFeePaid() {
		return miscFeePaid;
	}

	private void setMiscFeePaid(Money miscFeePaid) {
		this.miscFeePaid = miscFeePaid;
	}

	public Money getMiscPenaltyPaid() {
		return miscPenaltyPaid;
	}

	private void setMiscPenaltyPaid(Money miscPenaltyPaid) {
		this.miscPenaltyPaid = miscPenaltyPaid;
	}


	public CustomerAccountPaymentData(
			AccountActionDateEntity accountAction) {
		super(accountAction);
		Map<Short, Money> feesPaid = new HashMap<Short, Money>();
		setMiscFeePaid(accountAction.getMiscFee());
		setMiscPenaltyPaid(accountAction.getMiscPenalty());
		for (AccountFeesActionDetailEntity accountFees : accountAction
				.getAccountFeesActionDetails()) {
			feesPaid.put(accountFees.getFee().getFeeId(), accountFees
					.getFeeAmount());
		}
		setFeesPaid(feesPaid);
	}

}
