package org.mifos.application.reports.business.service;

import org.mifos.framework.MifosTestCase;

public class ReportServiceFactoryTest extends MifosTestCase {
	public void testInitializingServiceFactoryDoesNotThrowAnyErrors()
			throws Exception {
		try {
			ReportServiceFactory.getCacheEnabledCollectionSheetReportService();
		}
		catch (Exception e) {
			fail("Failed to initiliaze ReportServiceFactory, Error : "
					+ e.getMessage());
		}
	}

	public void testFetchingBranchReportService() throws Exception {
		try {
			ReportServiceFactory.getLoggingEnabledBranchReportService(Integer
					.valueOf(1));
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail("Failed to fetch BranchReportService");
		}
	}

	public void testBranchCashConfirmationReportService() throws Exception {
		try {
			ReportServiceFactory.getBranchCashConfirmationReportService(Integer
					.valueOf(0));
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail("Failed to fetch BranchCashConfirmationReportService");
		}
	}
}
