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

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.branchreport.BranchReportBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.reports.business.service.BranchReportConfigService;
import org.mifos.application.reports.business.service.BranchReportService;
import org.mifos.application.reports.business.service.IBranchReportService;
import org.mifos.application.reports.business.service.ReportServiceFactory;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class BranchReportHelper extends TaskHelper {

	private CustomerBusinessService customerBusinessService;
	private OfficeBusinessService officeBusinessService;
	private IBranchReportService branchReportService;
	private BranchReportConfigService branchReportConfigService;
	

	public BranchReportHelper(MifosTask mifosTask) {
		super(mifosTask);
		customerBusinessService = new CustomerBusinessService();
		officeBusinessService = new OfficeBusinessService();
		branchReportService = new BranchReportService();
		branchReportConfigService = ReportServiceFactory
				.getBranchReportConfigService();
	}

	@Override
	public void execute(long timeInMillis) throws BatchJobException {
		System.out.println("Task BranchReportTask starts ");
		long time1 = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = session.beginTransaction();
		Date runDate = new Date(timeInMillis);
		
		try {
			removeExistingBranchReportsForGivenRunDate(runDate);
			populateBranchReportBatch(session, runDate);
			transaction.commit();
		}
		catch (HibernateException e) {
			transaction.rollback();
			throw new BatchJobException(e);
		}
		catch (ServiceException e) {
			throw new BatchJobException(e);
		}
		long time2 = System.currentTimeMillis();
		long duration = time2 - time1;
		getLogger().info("Time to run BranchReportTask " + duration);
		System.out.println("Task BranchReportTask runs in " + duration);
	}

	void populateBranchReportBatch(Session session, Date runDate) throws BatchJobException,
			ServiceException {
		List<OfficeBO> branchOffices = officeBusinessService.getBranchOffices();
		if (branchOffices == null)
			return;
		System.out.println("There are  " + branchOffices.size() + " offices");
		for (OfficeBO branchOffice : branchOffices) {
			System.out.println("Start generating report for branch office " + branchOffice.getOfficeName());
			long time1 = System.currentTimeMillis();
			createBranchReport(session, branchOffice, runDate);
			System.out.println("Generating report for branch office " + branchOffice.getOfficeName() + 
					" in " + (System.currentTimeMillis() - time1));
		}
	}

	BranchReportBO createBranchReport(Session session, OfficeBO branchOffice,
			Date runDate) throws BatchJobException {
		BranchReportBO branchReport = new BranchReportBO(branchOffice
				.getOfficeId(), runDate);

		new BranchReportClientSummaryHelper(customerBusinessService, branchReportService,
				branchReportConfigService).populateClientSummary(branchReport, branchOffice);
		new BranchReportLoanArrearsAgingHelper(branchReport,
				branchReportService, branchReportConfigService)
				.populateLoanArrearsAging();
		new BranchReportStaffSummaryHelper(branchReport, branchReportService,
				branchReportConfigService).populateStaffSummary();
		new BranchReportStaffingLevelSummaryHelper(branchReport,
				branchReportService).populateStaffingLevelSummary();
		new BranchReportLoanDetailsHelper(branchReport, branchReportService,
				branchReportConfigService).populateLoanDetails();
		new BranchReportLoanArrearsProfileHelper(branchReport,
				branchReportService, branchReportConfigService)
				.populateLoanArrearsProfile();
		session.save(branchReport);
		session.flush();
		session.clear();
		return branchReport;
	}

	void removeExistingBranchReportsForGivenRunDate(Date runDate)
			throws ServiceException {

		if (!branchReportService.isReportDataPresentForRundate(runDate))
			return;

		branchReportService.removeBranchReports(branchReportService
				.getBranchReports(runDate));
	}
}
