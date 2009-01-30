package org.mifos.application.bulkentry.business;

import java.util.Date;
import java.util.List;

import org.mifos.framework.util.helpers.Money;

public class CollectionSheetEntryLoanInstallmentView extends CollectionSheetEntryInstallmentView {

	private final Money principal;

	private final Money interest;

	private final Money penalty;

	private final Money miscFee;

	private final Money miscPenalty;

	private final Money principalPaid;

	private final Money interestPaid;

	private final Money penaltyPaid;

	private final Money miscFeePaid;

	private final Money miscPenaltyPaid;

	private List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActions;

	public CollectionSheetEntryLoanInstallmentView(Integer accountId, Integer customerId,
			Short installmentId, Integer actionDateId, Date actionDate,
			Money principal, Money principalPaid, Money interest,
			Money interestPaid, Money miscFee, Money miscFeePaid,
			Money penalty, Money penaltyPaid, Money miscPenalty,
			Money miscPenaltyPaid) {
		super(accountId, customerId, installmentId, actionDateId, actionDate);
		this.principal = principal;
		this.interest = interest;
		this.penalty = penalty;
		this.miscFee = miscFee;
		this.miscFeePaid = miscFeePaid;
		this.miscPenalty = miscPenalty;
		this.miscPenaltyPaid = miscPenaltyPaid;
		this.principalPaid = principalPaid;
		this.interestPaid = interestPaid;
		this.penaltyPaid = penaltyPaid;
	}

	public CollectionSheetEntryLoanInstallmentView(Integer accountId, Integer customerId) {
		super(accountId, customerId, null, null, null);
		this.principal = null;
		this.interest = null;
		this.penalty = null;
		this.miscFee = null;
		this.miscFeePaid = null;
		this.miscPenalty = null;
		this.miscPenaltyPaid = null;
		this.principalPaid = null;
		this.interestPaid = null;
		this.penaltyPaid = null;
	}

	public Money getInterest() {
		return interest;
	}

	public Money getInterestPaid() {
		return interestPaid;
	}

	public Money getMiscFee() {
		return miscFee;
	}

	public Money getMiscFeePaid() {
		return miscFeePaid;
	}

	public Money getMiscPenalty() {
		return miscPenalty;
	}

	public Money getMiscPenaltyPaid() {
		return miscPenaltyPaid;
	}

	public Money getPenalty() {
		return penalty;
	}

	public Money getPenaltyPaid() {
		return penaltyPaid;
	}

	public Money getPrincipal() {
		return principal;
	}

	public Money getPrincipalPaid() {
		return principalPaid;
	}

	public List<CollectionSheetEntryAccountFeeActionView> getCollectionSheetEntryAccountFeeActions() {
		return collectionSheetEntryAccountFeeActions;
	}

	public void setCollectionSheetEntryAccountFeeActions(
			List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActions) {
		this.collectionSheetEntryAccountFeeActions = collectionSheetEntryAccountFeeActions;
	}

	public Money getPrincipalDue() {
		return getPrincipal().subtract(getPrincipalPaid());
	}

	public Money getInterestDue() {
		return getInterest().subtract(getInterestPaid());
	}

	public Money getPenaltyDue() {
		return (getPenalty().add(getMiscPenalty())).subtract(getPenaltyPaid()
				.subtract(getMiscPenaltyPaid()));
	}

	public Money getMiscFeeDue() {
		return getMiscFee().subtract(getMiscFeePaid());
	}

	public Money getTotalFeeDue() {
		Money totalFees = new Money();
		if (collectionSheetEntryAccountFeeActions != null)
			for (CollectionSheetEntryAccountFeeActionView obj : collectionSheetEntryAccountFeeActions) {
				totalFees = totalFees.add(obj.getFeeDue());
			}

		return totalFees;
	}

	public Money getTotalPenalty() {
		return getPenalty().add(getMiscPenalty());
	}

	public Money getTotalFees() {
		return getMiscFee().add(getTotalFeeDue());
	}

	public Money getTotalDueWithoutPricipal() {
		return getInterestDue().add(getPenaltyDue()).add(getMiscFeeDue());
	}

	public Money getTotalDue() {
		return getPrincipalDue().add(getInterestDue()).add(getPenaltyDue())
				.add(getMiscFeeDue());

	}

	public Money getTotalDueWithFees() {
		return getTotalDue().add(getTotalFeeDue());
	}

}
