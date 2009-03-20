/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

import static org.mifos.framework.util.helpers.NumberUtils.nullSafeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mifos.application.branchreport.BranchReportClientSummaryBO;
import org.mifos.application.branchreport.persistence.BranchReportSqlPersistence;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.reports.business.service.BranchReportConfigService;
import org.mifos.application.reports.business.service.IBranchReportService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.NumberUtils;

public class BranchReportSqlDataAggregator implements
		BranchReportDataAggregator {

	private static final String CUSTOMER_WAS_ACTIVE = "Customer Was Active";
	private static final String CUSTOMER_WAS_CLOSE = "Customer Was Close";
	private static final String CUSTOMER_WAS_HOLD = "Customer Was Hold";
	private BranchReportSqlPersistence persistence;
	private final IBranchReportService branchReportService;
	private final BranchReportConfigService configService;
	private CustomerBusinessService customerBusinessService;

	public BranchReportSqlDataAggregator(
			IBranchReportService branchReportService,
			BranchReportConfigService configService,
			CustomerBusinessService customerBusinessService) {
		this.branchReportService = branchReportService;
		this.configService = configService;
		this.customerBusinessService = customerBusinessService;
		persistence = new BranchReportSqlPersistence();
	}

	public List<BranchReportClientSummaryBO> fetchClientSummaries(
			OfficeBO branchOffice) throws ServiceException {
		List<BranchReportClientSummaryBO> clientSummaries = new ArrayList<BranchReportClientSummaryBO>();
		try {
			clientSummaries.add(new BranchReportClientSummaryBO(
					BranchReportClientSummaryBO.CENTER_COUNT, persistence
							.getCustomerCount(branchOffice.getOfficeId(),
									CustomerLevel.CENTER), null));
			clientSummaries.add(new BranchReportClientSummaryBO(
					BranchReportClientSummaryBO.GROUP_COUNT, persistence
							.getCustomerCount(branchOffice.getOfficeId(),
									CustomerLevel.GROUP), null));
			clientSummaries.add(new BranchReportClientSummaryBO(
					BranchReportClientSummaryBO.PORTFOLIO_AT_RISK,
					branchReportService.extractPortfolioAtRiskForOffice(
							branchOffice, configService.getGracePeriodDays()),
					null));

			Map<String, Integer> customerCountBasedOnStatus = persistence
					.getCustomerCountBasedOnStatus(branchOffice.getOfficeId(),
							CustomerLevel.CLIENT, CollectionUtils
									.asList(new String[] { CUSTOMER_WAS_ACTIVE,
											CUSTOMER_WAS_HOLD,
											CUSTOMER_WAS_CLOSE }));
			Map<String, Integer> customerCountBasedOnStatusForPoorClients = persistence
					.getVeryPoorCustomerCountBasedOnStatus(branchOffice
							.getOfficeId(), CustomerLevel.CLIENT,
							CollectionUtils.asList(new String[] {
									CUSTOMER_WAS_ACTIVE, CUSTOMER_WAS_HOLD,
									CUSTOMER_WAS_CLOSE }));
			Integer activeClientCount = nullSafeValue(customerCountBasedOnStatus
					.get(CUSTOMER_WAS_ACTIVE));
			Integer activeVeryPoorClientCount = nullSafeValue(customerCountBasedOnStatusForPoorClients
					.get(CUSTOMER_WAS_ACTIVE));
			Integer holdClientCount = nullSafeValue(customerCountBasedOnStatus
					.get(CUSTOMER_WAS_HOLD));
			Integer holdVeryPoorClientCount = nullSafeValue(customerCountBasedOnStatusForPoorClients
					.get(CUSTOMER_WAS_HOLD));
			Integer closedClientCount = nullSafeValue(customerCountBasedOnStatus
					.get(CUSTOMER_WAS_CLOSE));
			Integer closedVeryPoorClientCount = nullSafeValue(customerCountBasedOnStatusForPoorClients
					.get(CUSTOMER_WAS_CLOSE));

			clientSummaries.add(new BranchReportClientSummaryBO(
					BranchReportClientSummaryBO.ACTIVE_CLIENTS_COUNT,
					activeClientCount, activeVeryPoorClientCount));
			clientSummaries.add(new BranchReportClientSummaryBO(
					BranchReportClientSummaryBO.ON_HOLDS_COUNT,
					holdClientCount, holdVeryPoorClientCount));
			clientSummaries.add(new BranchReportClientSummaryBO(
					BranchReportClientSummaryBO.DROP_OUTS_COUNT,
					closedClientCount, closedVeryPoorClientCount));
			clientSummaries.add(new BranchReportClientSummaryBO(
					BranchReportClientSummaryBO.DROP_OUT_RATE, NumberUtils
							.getPercentage(closedClientCount, closedClientCount
									+ activeClientCount + holdClientCount),
					NumberUtils.getPercentage(closedVeryPoorClientCount,
							closedVeryPoorClientCount
									+ activeVeryPoorClientCount
									+ holdVeryPoorClientCount)));

			clientSummaries.add(new BranchReportClientSummaryBO(
					BranchReportClientSummaryBO.ACTIVE_BORROWERS_COUNT,
					persistence.getActiveBorrowersCount(branchOffice
							.getOfficeId(), CustomerLevel.CLIENT), persistence
							.getVeryPoorActiveBorrowersCount(branchOffice
									.getOfficeId(), CustomerLevel.CLIENT)));
			clientSummaries.add(new BranchReportClientSummaryBO(
					BranchReportClientSummaryBO.ACTIVE_SAVERS_COUNT,
					persistence.getActiveSaversCount(
							branchOffice.getOfficeId(), CustomerLevel.CLIENT),
					persistence.getVeryPoorActiveSaversCount(branchOffice
							.getOfficeId(), CustomerLevel.CLIENT)));
			clientSummaries
					.add(createLoanAccountDormantClientsSummary(branchOffice));
			clientSummaries
					.add(createSavingAccountDormantClientsSummary(branchOffice));
			clientSummaries.add(new BranchReportClientSummaryBO(
					BranchReportClientSummaryBO.REPLACEMENTS_COUNT, persistence
							.getReplacementCountForOffice(branchOffice
									.getOfficeId(), CustomerLevel.CLIENT,
									configService.getReplacementFieldId(),
									configService.getReplacementFieldValue()),
					persistence.getVeryPoorReplaceCountForOffice(branchOffice
							.getOfficeId(), CustomerLevel.CLIENT, configService
							.getReplacementFieldId(), configService
							.getReplacementFieldValue())));

		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
		return clientSummaries;
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
}
