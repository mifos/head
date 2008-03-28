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
package org.mifos.framework.components.batchjobs.helpers;

import java.math.BigDecimal;

import org.mifos.application.branchreport.BranchReportBO;
import org.mifos.application.branchreport.BranchReportClientSummaryBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.reports.business.service.BranchReportConfigService;
import org.mifos.application.reports.business.service.IBranchReportService;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.ServiceException;

public class BranchReportClientSummaryHelper {
	private OfficeBO branchOffice;
	private BranchReportBO branchReport;
	private CustomerBusinessService customerBusinessService;
	private IBranchReportService branchReportService;
	private final BranchReportConfigService configService;

	public BranchReportClientSummaryHelper(OfficeBO branchOffice,
			BranchReportBO branchReport,
			CustomerBusinessService customerBusinessService,
			IBranchReportService branchReportService,
			BranchReportConfigService configService) {
		this.branchOffice = branchOffice;
		this.branchReport = branchReport;
		this.customerBusinessService = customerBusinessService;
		this.branchReportService = branchReportService;
		this.configService = configService;
	}

	void populateClientSummary() throws BatchJobException {
		try {
			branchReport.addClientSummary(createCenterSummary(branchOffice));
			branchReport.addClientSummary(createGroupSummary(branchOffice));
			branchReport.addClientSummary(createPortfolioAtRisk(branchOffice,
					configService));
			branchReport
					.addClientSummary(createActiveClientSummary(branchOffice));
			branchReport
					.addClientSummary(createActiveBorrowersSummary(branchOffice));
			branchReport
					.addClientSummary(createReplacementsSummary(branchOffice));
			branchReport
					.addClientSummary(createLoanAccountDormantClientsSummary(branchOffice));
			branchReport
					.addClientSummary(createSavingAccountDormantClientsSummary(branchOffice));
			branchReport
					.addClientSummary(createDropOutClientsSummary(branchOffice));
			branchReport
					.addClientSummary(createOnHoldClientsSummary(branchOffice));
			branchReport
					.addClientSummary(createActiveSaversSummary(branchOffice));
			branchReport
					.addClientSummary(createClientDropOutRateSummary(branchOffice));
		}
		catch (ServiceException e) {
			throw new BatchJobException(e);
		}
	}

	private BranchReportClientSummaryBO createPortfolioAtRisk(
			OfficeBO branchOffice, BranchReportConfigService configService)
			throws ServiceException {
		BigDecimal portfolioAtRisk = branchReportService
				.extractPortfolioAtRiskForOffice(branchOffice, configService
						.getGracePeriodDays());
		return new BranchReportClientSummaryBO(
				BranchReportClientSummaryBO.PORTFOLIO_AT_RISK, portfolioAtRisk,
				null);
	}

	private BranchReportClientSummaryBO createActiveSaversSummary(
			OfficeBO branchOffice) throws ServiceException {
		Integer activeSaversCount = customerBusinessService
				.getActiveSaversCountForOffice(branchOffice);
		Integer veryPoorActiveSaversCount = customerBusinessService
				.getVeryPoorActiveSaversCountForOffice(branchOffice);
		return new BranchReportClientSummaryBO(
				BranchReportClientSummaryBO.ACTIVE_SAVERS_COUNT,
				activeSaversCount, veryPoorActiveSaversCount);
	}

	private BranchReportClientSummaryBO createActiveBorrowersSummary(
			OfficeBO branchOffice) throws ServiceException {
		Integer activeBorrowersCount = customerBusinessService
				.getActiveBorrowersCountForOffice(branchOffice);
		Integer veryPoorActiveBorrowersCount = customerBusinessService
				.getVeryPoorActiveBorrowersCountForOffice(branchOffice);
		return new BranchReportClientSummaryBO(
				BranchReportClientSummaryBO.ACTIVE_BORROWERS_COUNT,
				activeBorrowersCount, veryPoorActiveBorrowersCount);
	}

	private BranchReportClientSummaryBO createActiveClientSummary(
			OfficeBO branchOffice) throws ServiceException {
		Integer activeClientCount = customerBusinessService
				.getActiveClientCountForOffice(branchOffice);
		Integer veryPoorClientCount = customerBusinessService
				.getVeryPoorClientCountForOffice(branchOffice);
		return new BranchReportClientSummaryBO(
				BranchReportClientSummaryBO.ACTIVE_CLIENTS_COUNT,
				activeClientCount, veryPoorClientCount);
	}

	private BranchReportClientSummaryBO createGroupSummary(OfficeBO branchOffice)
			throws ServiceException {
		Integer groupCount = customerBusinessService
				.getGroupCountForOffice(branchOffice);
		return new BranchReportClientSummaryBO(
				BranchReportClientSummaryBO.GROUP_COUNT, groupCount, null);
	}

	private BranchReportClientSummaryBO createCenterSummary(
			OfficeBO branchOffice) throws ServiceException {
		Integer centerCount = customerBusinessService
				.getCenterCountForOffice(branchOffice);
		return new BranchReportClientSummaryBO(
				BranchReportClientSummaryBO.CENTER_COUNT, centerCount, null);
	}

	private BranchReportClientSummaryBO createReplacementsSummary(
			OfficeBO branchOffice) throws ServiceException {
		Integer replacementsCount = customerBusinessService
				.getCustomerReplacementsCountForOffice(branchOffice, configService.getReplacementFieldId(), configService.getReplacementFieldValue());
		Integer veryPoorReplacementsCount = customerBusinessService
				.getCustomerVeryPoorReplacementsCountForOffice(branchOffice, configService.getReplacementFieldId(), configService.getReplacementFieldValue());
		return new BranchReportClientSummaryBO(
				BranchReportClientSummaryBO.REPLACEMENTS_COUNT,
				replacementsCount, veryPoorReplacementsCount);
	}

	private BranchReportClientSummaryBO createLoanAccountDormantClientsSummary(
			OfficeBO branchOffice) throws ServiceException {
		Integer dormantLoanAccounts = customerBusinessService
				.getDormantClientsCountByLoanAccountForOffice(branchOffice,
						configService.getLoanCyclePeriod());
		Integer veryPoorDormantLoanAccounts = customerBusinessService
				.getVeryPoorDormantClientsCountByLoanAccountForOffice(
						branchOffice, configService.getLoanCyclePeriod());
		return new BranchReportClientSummaryBO(
				BranchReportClientSummaryBO.LOAN_ACCOUNT_DORMANT_COUNT,
				dormantLoanAccounts, veryPoorDormantLoanAccounts);
	}

	private BranchReportClientSummaryBO createSavingAccountDormantClientsSummary(
			OfficeBO branchOffice) throws ServiceException {
		Integer dormantSavingAccounts = customerBusinessService
				.getDormantClientsCountBySavingAccountForOffice(branchOffice,
						configService.getLoanCyclePeriod());
		Integer veryPoorDormantSavingAccounts = customerBusinessService
				.getVeryPoorDormantClientsCountBySavingAccountForOffice(
						branchOffice, configService.getLoanCyclePeriod());
		return new BranchReportClientSummaryBO(
				BranchReportClientSummaryBO.SAVING_ACCOUNT_DORMANT_COUNT,
				dormantSavingAccounts, veryPoorDormantSavingAccounts);
	}

	private BranchReportClientSummaryBO createDropOutClientsSummary(
			OfficeBO branchOffice) throws ServiceException {
		Integer dropOutClientsCount = customerBusinessService
				.getDropOutClientsCountForOffice(branchOffice);
		Integer veryPoorDropOutClientsCount = customerBusinessService
				.getVeryPoorDropOutClientsCountForOffice(branchOffice);
		return new BranchReportClientSummaryBO(
				BranchReportClientSummaryBO.DROP_OUTS_COUNT,
				dropOutClientsCount, veryPoorDropOutClientsCount);
	}

	private BranchReportClientSummaryBO createClientDropOutRateSummary(
			OfficeBO branchOffice) throws ServiceException {
		BigDecimal clientDropOutRateForOffice = customerBusinessService
				.getClientDropOutRateForOffice(branchOffice);
		BigDecimal veryPoorClientDropoutRateForOffice = customerBusinessService
				.getVeryPoorClientDropoutRateForOffice(branchOffice);
		return new BranchReportClientSummaryBO(
				BranchReportClientSummaryBO.DROP_OUT_RATE,
				clientDropOutRateForOffice, veryPoorClientDropoutRateForOffice);
	}

	private BranchReportClientSummaryBO createOnHoldClientsSummary(
			OfficeBO branchOffice) throws ServiceException {
		Integer onHoldClientsCount = customerBusinessService
				.getOnHoldClientsCountForOffice(branchOffice);
		Integer veryPoorOnHoldClientsCount = customerBusinessService
				.getVeryPoorOnHoldClientsCountForOffice(branchOffice);
		return new BranchReportClientSummaryBO(
				BranchReportClientSummaryBO.ON_HOLDS_COUNT, onHoldClientsCount,
				veryPoorOnHoldClientsCount);
	}


}
