package org.mifos.framework.components.batchjobs.helpers;

import java.util.Date;
import java.util.Set;

import org.junit.Test;
import org.mifos.application.branchreport.BranchReportBO;
import org.mifos.application.branchreport.BranchReportStaffSummaryBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.reports.business.service.BranchReportConfigService;
import org.mifos.application.reports.business.service.BranchReportService;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.springframework.core.io.Resource;

public class BranchReportStaffSummaryHelperTest extends MifosTestCase {

	private static final Date RUN_DATE = DateUtils.currentDate();
	private static final Short BRANCH_ID_SHORT = Short.valueOf("2");

	@Test
	public void testPopulateStaffSummary() throws BatchJobException {
		BranchReportBO branchReportBO = new BranchReportBO(BRANCH_ID_SHORT,
				RUN_DATE);
		BranchReportStaffSummaryHelper staffSummaryHelper = new BranchReportStaffSummaryHelper(
				branchReportBO, new BranchReportService(),
				getConfigServiceStub());
		staffSummaryHelper.populateStaffSummary();
		Set<BranchReportStaffSummaryBO> staffSummaries = branchReportBO
				.getStaffSummaries();
		assertNotNull(staffSummaries);
	}

	private BranchReportConfigService getConfigServiceStub() {
		return new BranchReportConfigService(null) {

			@Override
			protected void initConfig(Resource configResource) {
			}

			@Override
			public Integer getGracePeriodDays() {
				return Integer.valueOf(1);
			}

			@Override
			public MifosCurrency getCurrency() throws ServiceException {
				return Configuration.getInstance().getSystemConfig()
						.getCurrency();
			}
		};
	}

}
