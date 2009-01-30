package org.mifos.application.bulkentry.business;

import java.util.Date;
import java.util.List;

import org.mifos.framework.util.helpers.Money;

public class CollectionSheetEntryCustomerAccountInstallmentView extends
		CollectionSheetEntryInstallmentView {

	private final Money miscFee;

	private final Money miscFeePaid;

	private final Money miscPenalty;

	private final Money miscPenaltyPaid;

	private List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActions;

	public CollectionSheetEntryCustomerAccountInstallmentView(Integer accountId,
			Integer customerId, Short installmentId, Integer actionDateId,
			Date actionDate, Money miscFee, Money miscFeePaid,
			Money miscPenalty, Money miscPenaltyPaid) {
		super(accountId, customerId, installmentId, actionDateId, actionDate);
		this.miscFee = miscFee;
		this.miscFeePaid = miscFeePaid;
		this.miscPenalty = miscPenalty;
		this.miscPenaltyPaid = miscPenaltyPaid;
	}

	public CollectionSheetEntryCustomerAccountInstallmentView(Integer accountId,
			Integer customerId) {
		super(accountId, customerId, null, null, null);
		this.miscFee = null;
		this.miscFeePaid = null;
		this.miscPenalty = null;
		this.miscPenaltyPaid = null;
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

	public List<CollectionSheetEntryAccountFeeActionView> getCollectionSheetEntryAccountFeeActions() {
		return collectionSheetEntryAccountFeeActions;
	}

	public void setCollectionSheetEntryAccountFeeActions(
			List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActions) {
		this.collectionSheetEntryAccountFeeActions = collectionSheetEntryAccountFeeActions;
	}

	public Money getMiscFeeDue() {
		return getMiscFee().subtract(getMiscFeePaid());
	}

	public Money getMiscPenaltyDue() {
		return getMiscPenalty().subtract(getMiscPenaltyPaid());
	}

	public Money getTotalFeeDue() {
		Money totalFees = new Money();
		if (collectionSheetEntryAccountFeeActions != null)
			for (CollectionSheetEntryAccountFeeActionView obj : collectionSheetEntryAccountFeeActions) {
				totalFees = totalFees.add(obj.getFeeDue());
			}

		return totalFees;
	}

	public Money getTotalFees() {
		return getMiscFee().add(getTotalFeeDue());
	}

	public Money getTotalDueWithFees() {
		return getMiscPenaltyDue().add(getTotalFees());
	}

}
