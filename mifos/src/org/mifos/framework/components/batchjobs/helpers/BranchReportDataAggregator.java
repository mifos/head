package org.mifos.framework.components.batchjobs.helpers;

import java.util.List;

import org.mifos.application.branchreport.BranchReportClientSummaryBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.framework.exceptions.ServiceException;

public interface BranchReportDataAggregator {
	public List<BranchReportClientSummaryBO> fetchClientSummaries(OfficeBO branchOffice)
			throws ServiceException;
}
