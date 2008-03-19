package org.mifos.application.cashconfirmationreport;


import org.mifos.framework.business.BusinessObject;

public abstract class BranchCashConfirmationSubReport extends BusinessObject {

	@SuppressWarnings("unused")
	private BranchCashConfirmationReportBO branchCashConfirmationReport;

	public BranchCashConfirmationSubReport() {
		super();
	}

	public void setBranchCashConfirmationReport(
			BranchCashConfirmationReportBO branchCashConfirmationReport) {
		this.branchCashConfirmationReport = branchCashConfirmationReport;
	}
}
