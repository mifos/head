package org.mifos.application.accounts.util.valueobjects;

import org.mifos.application.accounts.util.valueobjects.Account;
import org.mifos.framework.util.helpers.Money;

public class AccountActivity {

	private Integer activityId;

	private String activityName;
	
	private Short personnelId;

	private Money principal;

	private Money principalOutstanding;

	private Money interest;

	private Money interestOutstanding;

	private Money fee;

	private Money feeOutstanding;

	private Money penalty;

	private Money penaltyOutstanding;
	
	private Account account;

	
	/**
	 * @return Returns the activityId.
	 */
	public Integer getActivityId() {
		return activityId;
	}

	/**
	 * @param activityId
	 *            The activityId to set.
	 */
	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	/**
	 * @return Returns the activityName.
	 */
	public String getActivityName() {
		return activityName;
	}

	/**
	 * @param activityName
	 *            The activityName to set.
	 */
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	/**
	 * @return Returns the fee.
	 */
	public Money getFee() {
		return fee;
	}

	/**
	 * @param fee
	 *            The fee to set.
	 */
	public void setFee(Money fee) {
		this.fee = fee;
	}

	/**
	 * @return Returns the feeOutstanding.
	 */
	public Money getFeeOutstanding() {
		return feeOutstanding;
	}

	/**
	 * @param feeOutstanding
	 *            The feeOutstanding to set.
	 */
	public void setFeeOutstanding(Money feeOutstanding) {
		this.feeOutstanding = feeOutstanding;
	}

	/**
	 * @return Returns the interest.
	 */
	public Money getInterest() {
		return interest;
	}

	/**
	 * @param interest
	 *            The interest to set.
	 */
	public void setInterest(Money interest) {
		this.interest = interest;
	}

	/**
	 * @return Returns the interestOutstanding.
	 */
	public Money getInterestOutstanding() {
		return interestOutstanding;
	}

	/**
	 * @param interestOutstanding
	 *            The interestOutstanding to set.
	 */
	public void setInterestOutstanding(Money interestOutstanding) {
		this.interestOutstanding = interestOutstanding;
	}

	/**
	 * @return Returns the penalty.
	 */
	public Money getPenalty() {
		return penalty;
	}

	/**
	 * @param penalty
	 *            The penalty to set.
	 */
	public void setPenalty(Money penalty) {
		this.penalty = penalty;
	}

	/**
	 * @return Returns the penaltyOutstanding.
	 */
	public Money getPenaltyOutstanding() {
		return penaltyOutstanding;
	}

	/**
	 * @param penaltyOutstanding
	 *            The penaltyOutstanding to set.
	 */
	public void setPenaltyOutstanding(Money penaltyOutstanding) {
		this.penaltyOutstanding = penaltyOutstanding;
	}

	/**
	 * @return Returns the principal.
	 */
	public Money getPrincipal() {
		return principal;
	}

	/**
	 * @param principal
	 *            The principal to set.
	 */
	public void setPrincipal(Money principal) {
		this.principal = principal;
	}

	/**
	 * @return Returns the principalOutstanding.
	 */
	public Money getPrincipalOutstanding() {
		return principalOutstanding;
	}

	/**
	 * @param principalOutstanding
	 *            The principalOutstanding to set.
	 */
	public void setPrincipalOutstanding(Money principalOutstanding) {
		this.principalOutstanding = principalOutstanding;
	}

	/**
	 * @return Returns the personnelId.
	 */
	public Short getPersonnelId() {
		return personnelId;
	}

	/**
	 * @param personnelId The personnelId to set.
	 */
	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}

	/**
	 * @return Returns the account.
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * @param account The account to set.
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

}
