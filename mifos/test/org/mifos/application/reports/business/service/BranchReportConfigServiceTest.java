package org.mifos.application.reports.business.service;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.FilePaths;
import org.springframework.core.io.ClassPathResource;

public class BranchReportConfigServiceTest extends MifosTestCase {

	private BranchReportConfigService branchReportConfigService;

	public void testGetDaysInArrears() throws ServiceException {
		Integer daysInArrears = branchReportConfigService.getGracePeriodDays();
		assertNotNull(daysInArrears);
	}

	public void testGetLoanCyclePeriod() throws Exception {
		Integer loanCyclePeriod = branchReportConfigService
				.getLoanCyclePeriod();
		assertNotNull(loanCyclePeriod);
	}

	public void testGetReplacementFieldId() throws Exception {
		Short replacementFieldId = branchReportConfigService
				.getReplacementFieldId();
		assertNotNull(replacementFieldId);
	}

	public void testGetReplacementFieldValue() throws Exception {
		String replacementFieldValue = branchReportConfigService
				.getReplacementFieldValue();
		assertNotNull(replacementFieldValue);
	}

	public void testGetCurrency() throws Exception {
		MifosCurrency currency = branchReportConfigService.getCurrency();
		assertNotNull(currency);
	}

	@Override
	protected void setUp() throws Exception {
		ClassPathResource branchReportConfig = new ClassPathResource(
				FilePaths.BRANCH_REPORT_CONFIG);
		branchReportConfigService = new BranchReportConfigService(
				branchReportConfig);
	}
}
