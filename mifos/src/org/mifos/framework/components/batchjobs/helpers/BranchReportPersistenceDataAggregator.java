package org.mifos.framework.components.batchjobs.helpers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.mifos.application.branchreport.BranchReportClientSummaryBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.reports.business.service.BranchReportConfigService;
import org.mifos.application.reports.business.service.IBranchReportService;
import org.mifos.framework.exceptions.ServiceException;

public class BranchReportPersistenceDataAggregator implements
		BranchReportDataAggregator {

	private final CustomerBusinessService customerBusinessService;
	private final IBranchReportService branchReportService;
	private final BranchReportConfigService configService;

	public BranchReportPersistenceDataAggregator(
			CustomerBusinessService customerBusinessService,
			IBranchReportService branchReportService,
			BranchReportConfigService configService) {
		this.customerBusinessService = customerBusinessService;
		this.branchReportService = branchReportService;
		this.configService = configService;
	}

	public List<BranchReportClientSummaryBO> fetchClientSummaries(
			OfficeBO branchOffice) throws ServiceException {
		ArrayList<BranchReportClientSummaryBO> clientSummaries = new ArrayList<BranchReportClientSummaryBO>();
		clientSummaries.add(createCenterSummary(branchOffice));
		clientSummaries.add(createGroupSummary(branchOffice));
		clientSummaries.add(createPortfolioAtRisk(branchOffice));
		clientSummaries.add(createActiveClientSummary(branchOffice));
		clientSummaries.add(createActiveBorrowersSummary(branchOffice));
		clientSummaries.add(createReplacementsSummary(branchOffice));
		clientSummaries
				.add(createLoanAccountDormantClientsSummary(branchOffice));
		clientSummaries
				.add(createSavingAccountDormantClientsSummary(branchOffice));
		clientSummaries.add(createDropOutClientsSummary(branchOffice));
		clientSummaries.add(createOnHoldClientsSummary(branchOffice));
		clientSummaries.add(createActiveSaversSummary(branchOffice));
		clientSummaries.add(createClientDropOutRateSummary(branchOffice));
		return clientSummaries;
	}

	private BranchReportClientSummaryBO createPortfolioAtRisk(
			OfficeBO branchOffice) throws ServiceException {
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
				.getCustomerReplacementsCountForOffice(branchOffice,
						configService.getReplacementFieldId(), configService
								.getReplacementFieldValue());
		Integer veryPoorReplacementsCount = customerBusinessService
				.getCustomerVeryPoorReplacementsCountForOffice(branchOffice,
						configService.getReplacementFieldId(), configService
								.getReplacementFieldValue());
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
