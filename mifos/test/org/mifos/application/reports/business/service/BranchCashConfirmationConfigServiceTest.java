/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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
