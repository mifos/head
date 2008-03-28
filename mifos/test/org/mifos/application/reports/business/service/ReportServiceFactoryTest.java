/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
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
