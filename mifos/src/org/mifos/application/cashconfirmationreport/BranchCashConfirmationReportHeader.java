package org.mifos.application.cashconfirmationreport;
import static org.mifos.application.reports.util.helpers.ReportUtils.toDisplayDate;

import java.util.Date;

public class BranchCashConfirmationReportHeader {

	private final String branchName;
	private final Date runDate;

	public BranchCashConfirmationReportHeader(String branchName, Date runDate) {
		this.branchName = branchName;
		this.runDate = runDate;
	}

	public String getRunDate() {
		return toDisplayDate(runDate);
	}

	public String getBranchName() {
		return branchName;
	}
}
