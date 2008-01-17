package org.mifos.application.reports.business.dto;

import org.mifos.application.collectionsheet.business.CollSheetSavingsDetailsEntity;
import org.mifos.framework.util.helpers.MoneyUtils;

public class SavingsProductDetails {

	private final Double balance;
	private final Double due;
	private final String offeringShortName;

	public SavingsProductDetails(CollSheetSavingsDetailsEntity savingsDetails,
			String offeringShortName) {
		this.offeringShortName = offeringShortName;
		if (savingsDetails != null) {
			balance = MoneyUtils.getMoneyDoubleValue(savingsDetails
					.getAccountBalance());
			due = MoneyUtils.getMoneyDoubleValue(savingsDetails
					.getRecommendedAmntDue());
		}
		else {
			balance = due = 0d;
		}
	}

	public Double getBalance() {
		return balance;
	}

	public Double getDue() {
		return due;
	}

	public String getOfferingShortName() {
		return offeringShortName;
	}
}
