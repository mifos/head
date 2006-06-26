package org.mifos.application.accounts.util.helpers;

import java.util.HashMap;
import java.util.Map;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.framework.util.helpers.Money;

public class LoanPaymentData  extends AccountPaymentData {

	private Money principalPaid;

	private Money interestPaid;

	private Money penaltyPaid;

	private Money miscFeePaid;

	private Money miscPenaltyPaid;

	private Map<Short, Money> feesPaid;

	public Map<Short, Money> getFeesPaid() {
		return feesPaid;
	}

	private void setFeesPaid(Map<Short, Money> feesPaid) {
		this.feesPaid = feesPaid;
	}

	public Money getInterestPaid() {
		return interestPaid;
	}

	private void setInterestPaid(Money interestPaid) {
		this.interestPaid = interestPaid;
	}

	public Money getPenaltyPaid() {
		return penaltyPaid;
	}

	private void setPenaltyPaid(Money penaltyPaid) {
		this.penaltyPaid = penaltyPaid;
	}

	public Money getPrincipalPaid() {
		return principalPaid;
	}

	private void setPrincipalPaid(Money principalPaid) {
		this.principalPaid = principalPaid;
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

	public LoanPaymentData(
			AccountActionDateEntity accountActionDate) {
		super(accountActionDate);
		setPrincipalPaid(accountActionDate.getPrincipal());
		setInterestPaid(accountActionDate.getInterest());
		setPenaltyPaid(accountActionDate.getPenalty());
		setMiscFeePaid(accountActionDate.getMiscFee());
		setMiscPenaltyPaid(accountActionDate.getMiscPenalty());
		Map<Short, Money> feesPaid = new HashMap<Short, Money>();
		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountActionDate
				.getAccountFeesActionDetails()) {
			if (accountFeesActionDetailEntity.getFeeAmount() != null
					&& accountFeesActionDetailEntity.getFeeAmount()
							.getAmountDoubleValue() != 0)
				feesPaid.put(accountFeesActionDetailEntity.getFee().getFeeId(),
						accountFeesActionDetailEntity.getFeeAmount());
		}
		setFeesPaid(feesPaid);
	}

}
