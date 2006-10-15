package org.mifos.application.accounts.savings.business;

import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class SavingsPerformanceEntity extends PersistentObject {

	@SuppressWarnings("unused")
	// See Hibernate mapping
	private final Integer id;

	private Money totalDeposits;

	private Money totalWithdrawals;

	private Money totalInterestEarned;

	private Integer missedDeposits;

	private final SavingsBO savings;

	protected SavingsPerformanceEntity() {
		id = null;
		savings = null;
	}

	protected SavingsPerformanceEntity(SavingsBO savings) {
		id = null;
		this.savings = savings;
	}

	public SavingsBO getSavings() {
		return savings;
	}

	public Integer getMissedDeposits() {
		return missedDeposits;
	}

	void setMissedDeposits(Integer missedDeposits) {
		this.missedDeposits = missedDeposits;
	}

	public Money getTotalDeposits() {
		return totalDeposits;
	}

	void setTotalDeposits(Money totalDeposits) {
		this.totalDeposits = totalDeposits;
	}

	public Money getTotalInterestEarned() {
		return totalInterestEarned;
	}

	void setTotalInterestEarned(Money totalInterstEarned) {
		this.totalInterestEarned = totalInterstEarned;
	}

	public Money getTotalWithdrawals() {
		return totalWithdrawals;
	}

	void setTotalWithdrawals(Money totalWithdrawals) {
		this.totalWithdrawals = totalWithdrawals;
	}

	void setPaymentDetails(Money totalAmount) {
		if (totalDeposits == null)
			totalDeposits = new Money();
		totalDeposits = totalDeposits.add(totalAmount);
	}

	void setWithdrawDetails(Money totalAmount) {
		if (totalWithdrawals == null)
			totalWithdrawals = new Money();
		totalWithdrawals = totalWithdrawals.add(totalAmount);
	}

	void setTotalInterestDetails(Money totalAmount) {
		if (totalInterestEarned == null)
			totalInterestEarned = new Money();
		totalInterestEarned = totalInterestEarned.add(totalAmount);
	}

	void addMissedDeposits(int missedDeposits) {
		if (this.missedDeposits == null) {
			this.missedDeposits = new Integer(0);
		}
		this.missedDeposits = this.missedDeposits + missedDeposits;
	}

}
