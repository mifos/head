package org.mifos.application.cashconfirmationreport;

import org.mifos.framework.util.helpers.Money;

public class BranchCashConfirmationDisbursementBO extends
		BranchCashConfirmationInfoBO {

	public BranchCashConfirmationDisbursementBO() {
	}

	public BranchCashConfirmationDisbursementBO(String productOffering,
			Money actual) {
		super(productOffering, actual);
	}
}
