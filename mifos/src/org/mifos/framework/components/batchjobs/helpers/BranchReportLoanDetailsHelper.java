package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.application.branchreport.BranchReportBO;
import org.mifos.application.reports.business.service.BranchReportConfigService;
import org.mifos.application.reports.business.service.IBranchReportService;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.ServiceException;

public class BranchReportLoanDetailsHelper {

	private final BranchReportBO branchReport;
	private final IBranchReportService branchReportService;
	private BranchReportConfigService branchReportConfigService;

	public BranchReportLoanDetailsHelper(BranchReportBO branchReport,
			IBranchReportService branchReportService,
			BranchReportConfigService branchReportConfigService) {
		this.branchReport = branchReport;
		this.branchReportService = branchReportService;
		this.branchReportConfigService = branchReportConfigService;
	}

	public void populateLoanDetails() throws BatchJobException {
		try {
			branchReport.addLoanDetails(branchReportService.extractLoanDetails(
					branchReport.getBranchId(), branchReportConfigService
							.getCurrency()));
		}
		catch (ServiceException e) {
			throw new BatchJobException(e);
		}
	}

}
