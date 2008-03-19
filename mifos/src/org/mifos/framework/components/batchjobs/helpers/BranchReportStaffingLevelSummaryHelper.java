package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.application.branchreport.BranchReportBO;
import org.mifos.application.reports.business.service.IBranchReportService;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.ServiceException;

public class BranchReportStaffingLevelSummaryHelper {

	private final BranchReportBO branchReport;
	private final IBranchReportService branchReportService;

	public BranchReportStaffingLevelSummaryHelper(BranchReportBO branchReport,
			IBranchReportService branchReportService) {
		this.branchReport = branchReport;
		this.branchReportService = branchReportService;
	}

	public void populateStaffingLevelSummary() throws BatchJobException {
		try {
			branchReport.addStaffingLevelSummaries(branchReportService
					.extractBranchReportStaffingLevelSummaries(branchReport
							.getBranchId()));
		}
		catch (ServiceException e) {
			throw new BatchJobException(e);
		}
	}

}
