package org.mifos.application.reports.business.dto;

import org.mifos.application.collectionsheet.business.CollSheetLnDetailsEntity;
import org.mifos.framework.util.helpers.MoneyUtils;

public class LoanProductDetails {
	private final Double balance;
	private final Double due;
	private final Double otherFee;
	private final Double otherFine;
	private final Double issues;
	private final String offeringShortName;

	public LoanProductDetails(CollSheetLnDetailsEntity loanDetails,
			String offeringShortName) {
		this.offeringShortName = offeringShortName;
		if (loanDetails == null) {
			balance = due = issues = otherFee = otherFine = 0d;
		}
		else {
			balance = MoneyUtils.getMoneyDoubleValue(loanDetails
					.getTotalPrincipalDue());
			due = MoneyUtils.getMoneyDoubleValue(loanDetails.getTotalAmntDue());
			issues = MoneyUtils.getMoneyDoubleValue(loanDetails
					.getAmntToBeDisbursed());
			otherFee = MoneyUtils.getMoneyDoubleValue(loanDetails
					.getTotalFees());
			otherFine = MoneyUtils.getMoneyDoubleValue(loanDetails
					.getTotalPenalty());
		}
	}

	public Double getBalance() {
		return balance;
	}

	public Double getDue() {
		return due;
	}

	public Double getIssues() {
		return issues;
	}

	Double getOtherFee() {
		return otherFee;
	}

	Double getOtherFine() {
		return otherFine;
	}

	public String getOfferingShortName() {
		return offeringShortName;
	}
}
