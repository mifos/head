package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.application.branchreport.BranchReportBO;
import org.mifos.application.branchreport.BranchReportLoanArrearsProfileBO;
import org.mifos.application.reports.business.service.BranchReportConfigService;
import org.mifos.application.reports.business.service.IBranchReportService;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.ServiceException;

public class BranchReportLoanArrearsProfileHelper {

	private final BranchReportBO branchReport;
	private final IBranchReportService branchReportService;
	private BranchReportConfigService branchReportConfigService;

	public BranchReportLoanArrearsProfileHelper(BranchReportBO branchReport,
			IBranchReportService branchReportService,
			BranchReportConfigService branchReportConfigService) {
		this.branchReport = branchReport;
		this.branchReportService = branchReportService;
		this.branchReportConfigService = branchReportConfigService;
	}

	public void populateLoanArrearsProfile() throws BatchJobException {
		try {
			branchReport
					.addLoanArrearsProfile(createLoansInArrearsCountProfile());
		}
		catch (ServiceException e) {
			throw new BatchJobException(e);
		}
	}

	private BranchReportLoanArrearsProfileBO createLoansInArrearsCountProfile()
			throws ServiceException {
		return branchReportService.extractLoansInArrearsCount(branchReport
				.getBranchId(), branchReportConfigService
				.getCurrency());
	}
}
