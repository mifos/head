package org.mifos.application.cashconfirmationreport;

import org.mifos.framework.util.helpers.Money;

public class BranchCashConfirmationIssueBO extends BranchCashConfirmationInfoBO {

	public BranchCashConfirmationIssueBO() {
	}

	public BranchCashConfirmationIssueBO(String productOffering, Money actual) {
		super(productOffering, actual);
	}
}
