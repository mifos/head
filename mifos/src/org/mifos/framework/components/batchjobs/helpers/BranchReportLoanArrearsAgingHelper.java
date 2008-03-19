package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.application.branchreport.BranchReportBO;
import org.mifos.application.branchreport.BranchReportLoanArrearsAgingBO;
import org.mifos.application.branchreport.LoanArrearsAgingPeriod;
import org.mifos.application.reports.business.service.BranchReportConfigService;
import org.mifos.application.reports.business.service.IBranchReportService;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.ServiceException;

public class BranchReportLoanArrearsAgingHelper {

	private final BranchReportBO branchReport;
	private final IBranchReportService branchReportService;
	private final BranchReportConfigService branchReportConfigService;

	public BranchReportLoanArrearsAgingHelper(BranchReportBO branchReport,
			IBranchReportService branchReportService, BranchReportConfigService branchReportConfigService) {
		this.branchReport = branchReport;
		this.branchReportService = branchReportService;
		this.branchReportConfigService = branchReportConfigService;
	}

	public void populateLoanArrearsAging() throws BatchJobException {
		LoanArrearsAgingPeriod[] values = LoanArrearsAgingPeriod.values();
		for (LoanArrearsAgingPeriod period : values) {
			try {
				BranchReportLoanArrearsAgingBO loanArrears = branchReportService
						.extractLoanArrearsAgingInfoInPeriod(branchReport.getBranchId(),
								period, branchReportConfigService.getCurrency());
				branchReport.addLoanArrearsAging(loanArrears);
			}
			catch (ServiceException e) {
				throw new BatchJobException(e);
			}
		}
	}

}
