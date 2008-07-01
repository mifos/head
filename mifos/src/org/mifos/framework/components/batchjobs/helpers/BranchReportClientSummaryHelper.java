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
package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.application.branchreport.BranchReportBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.reports.business.service.BranchReportConfigService;
import org.mifos.application.reports.business.service.IBranchReportService;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.ServiceException;

public class BranchReportClientSummaryHelper {
	private CustomerBusinessService customerBusinessService;
	private IBranchReportService branchReportService;
	private final BranchReportConfigService configService;

	public BranchReportClientSummaryHelper(
			CustomerBusinessService customerBusinessService,
			IBranchReportService branchReportService,
			BranchReportConfigService configService) {
		this.customerBusinessService = customerBusinessService;
		this.branchReportService = branchReportService;
		this.configService = configService;
	}

	void populateClientSummary(BranchReportBO branchReport,
			OfficeBO branchOffice) throws BatchJobException {
		/**
		 * If want to switch to Hibernate based Batch Job, kindly replace the below line with
		 * 
 			branchReport
					.addClientSummaries(
						new BranchReportPersistenceDataAggregator(
							customerBusinessService, branchReportService,
							configService).fetchClientSummaries(branchOffice)
							);
		 */
		try {
			branchReport
					.addClientSummaries(
							new BranchReportSqlDataAggregator(branchReportService,
							configService, customerBusinessService).fetchClientSummaries(branchOffice)
							);
		}
		catch (ServiceException e) {
			throw new BatchJobException(e);
		}
	}
}
