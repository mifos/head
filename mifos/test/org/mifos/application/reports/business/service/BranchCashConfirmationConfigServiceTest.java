package org.mifos.application.reports.business.service;

import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FilePaths;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import junit.framework.TestCase;

public class BranchCashConfirmationConfigServiceTest extends TestCase {

	private BranchCashConfirmationConfigService branchCashConfirmationConfigService;

	public void testGetActionDate() {
		assertEquals(DateUtils.currentDate(),
				branchCashConfirmationConfigService.getActionDate());
	}

	public void testGetProductOfferingsForRecoveries() throws ServiceException {
		assertEquals(CollectionUtils.asList(Short.valueOf("1"), Short
				.valueOf("2"), Short.valueOf("3")),
				branchCashConfirmationConfigService
						.getProductOfferingsForRecoveries());
	}

	public void testGetProductOfferingsForIssues() throws Exception {
		assertEquals(CollectionUtils.asList(Short.valueOf("1"), Short
				.valueOf("2"), Short.valueOf("3")), branchCashConfirmationConfigService
				.getProductOfferingsForIssues());
	}

	public void testGetProductOfferingsForDisbursements() throws Exception {
		assertEquals(CollectionUtils.asList(Short.valueOf("1"), Short
				.valueOf("2")), branchCashConfirmationConfigService
				.getProductOfferingsForDisbursements());
	}

	@Override
	protected void setUp() throws Exception {
		Resource branchCashConfirmationReportConfig = new ClassPathResource(
				FilePaths.BRANCH_CASH_CONFIRMATION_REPORT_CONFIG);
		branchCashConfirmationConfigService = new BranchCashConfirmationConfigService(
				branchCashConfirmationReportConfig);
	}

}
