package org.mifos.application.reports.business.service;
import static org.mifos.framework.util.helpers.FilePaths.REPORT_PRODUCT_OFFERING_CONFIG;
import static org.mifos.framework.util.helpers.NumberUtils.convertIntegerToShort;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.collectionsheet.business.CollSheetCustBO;
import org.mifos.application.collectionsheet.business.CollSheetLnDetailsEntity;
import org.mifos.application.collectionsheet.business.CollSheetSavingsDetailsEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.application.productdefinition.business.service.SavingsPrdBusinessService;
import org.mifos.application.reports.business.dto.CollectionSheetReportDTO;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.NumberUtils;
import org.mifos.framework.util.helpers.Predicate;
import org.springframework.core.io.ClassPathResource;

// public interface for services used by collection sheet report
public class CollectionSheetReportService implements
		ICollectionSheetReportService {
	private final OfficeBusinessService officeBusinessService;
	private final CollectionSheetService collectionSheetService;
	private final AccountBusinessService accountBusinessService;
	private final ReportProductOfferingService reportProductOfferingService;
	private final CascadingReportParameterService cascadingReportParameterService;

	CollectionSheetReportService(CollectionSheetService collectionSheetService,
			OfficeBusinessService officeBusinessService,
			AccountBusinessService accountBusinessService,
			ReportProductOfferingService reportProductOfferingService,
			CascadingReportParameterService cascadingReportParameterService) {
		super();
		this.collectionSheetService = collectionSheetService;
		this.officeBusinessService = officeBusinessService;
		this.accountBusinessService = accountBusinessService;
		this.reportProductOfferingService = reportProductOfferingService;
		this.cascadingReportParameterService = cascadingReportParameterService;
	}

	CollectionSheetReportService() {
		this(new CollectionSheetService(), new OfficeBusinessService(),
				new AccountBusinessService(), new ReportProductOfferingService(new LoanPrdBusinessService(),
						new SavingsPrdBusinessService(), new ClassPathResource(
								REPORT_PRODUCT_OFFERING_CONFIG)),
				new CascadingReportParameterService(
						new ReportsParameterService(),
						new PersonnelBusinessService(),
						new CustomerBusinessService()));
	}

	public List<CollectionSheetReportDTO> getCollectionSheets(Integer branchId,
			Integer officerId, Integer customerId, Date meetingDate)
			throws Exception {
		OfficeBO office = officeBusinessService.getOffice(NumberUtils
				.convertIntegerToShort(branchId));
		List<CustomerBO> applicableCustomers = cascadingReportParameterService
				.getApplicableCustomers(convertIntegerToShort(branchId),
						convertIntegerToShort(officerId), customerId);
		java.sql.Date sqlMeetingDate = DateUtils.convertToSqlDate(meetingDate);
		List<CollectionSheetReportDTO> collectionSheetReport = new ArrayList<CollectionSheetReportDTO>();
		for (CustomerBO customer : applicableCustomers) {
			collectionSheetReport.addAll(collectCenterLevelData(office,
					customer, sqlMeetingDate));
		}
		return collectionSheetReport;
	}

	private List<CollectionSheetReportDTO> collectCenterLevelData(
			OfficeBO office, CustomerBO center, java.sql.Date meetingDate)
			throws Exception {
		List<CollectionSheetReportDTO> collectionSheets = new ArrayList<CollectionSheetReportDTO>();
		List<CollSheetCustBO> centerCollectionSheets = collectionSheetService
				.getCollectionSheetForCustomerOnMeetingDate(meetingDate, center
						.getCustomerId(), center.getPersonnel()
						.getPersonnelId(), CustomerLevel.CENTER);
		for (CollSheetCustBO centerCollectionSheet : centerCollectionSheets) {
			List<CollSheetCustBO> collectionSheetForGroups = collectionSheetService
					.getCollectionSheetForGroups(meetingDate,
							centerCollectionSheet, center.getPersonnel()
									.getPersonnelId());
			collectionSheets.addAll(collectGroupLevelData(office, center,
					meetingDate, collectionSheetForGroups));
		}
		return collectionSheets;
	}

	private List<CollectionSheetReportDTO> collectGroupLevelData(
			OfficeBO office, CustomerBO customer, java.sql.Date meetingDate,
			List<CollSheetCustBO> groupCollectionSheets) throws Exception {
		LoanOfferingBO loanOffering1 = reportProductOfferingService
				.getLoanOffering1();
		LoanOfferingBO loanOffering2 = reportProductOfferingService
				.getLoanOffering2();
		SavingsOfferingBO savingsOffering1 = reportProductOfferingService
				.getSavingsOffering1();
		SavingsOfferingBO savingsOffering2 = reportProductOfferingService
				.getSavingsOffering2();
		List<CollectionSheetReportDTO> collectionSheetReport = new ArrayList<CollectionSheetReportDTO>();
		try {
			for (CollSheetCustBO groupCollectionSheet : groupCollectionSheets) {
				List<CollSheetCustBO> clientCollectionSheets = collectionSheetService
						.getCollectionSheetForCustomers(meetingDate,
								groupCollectionSheet, customer.getPersonnel()
										.getPersonnelId());
				for (CollSheetCustBO clientCollectionSheet : clientCollectionSheets) {
					collectionSheetReport
							.add(new CollectionSheetReportDTO(
									getLoanProduct(clientCollectionSheet,
											loanOffering1),
									getLoanProduct(clientCollectionSheet,
											loanOffering2),
									getSavingProduct(clientCollectionSheet,
											savingsOffering1),
									getSavingProduct(clientCollectionSheet,
											savingsOffering2),
									clientCollectionSheet.getCustId(),
									clientCollectionSheet.getParentCustomerId(),
									clientCollectionSheet.getCustDisplayName(),
									office.getOfficeName(),
									groupCollectionSheet.getCustDisplayName(),
									customer, loanOffering1
											.getPrdOfferingShortName(),
									loanOffering2.getPrdOfferingShortName(),
									savingsOffering1.getPrdOfferingShortName(),
									savingsOffering2.getPrdOfferingShortName(), DateUtils.convertSqlToDate(meetingDate)));
				}
			}
		}
		catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
		return collectionSheetReport;
	}

	CollSheetLnDetailsEntity getLoanProduct(CollSheetCustBO collectionSheet,
			final LoanOfferingBO productOffering) throws Exception {
		return CollectionUtils.find(collectionSheet
				.getCollectionSheetLoanDetails(),
				new Predicate<CollSheetLnDetailsEntity>() {
					public boolean evaluate(CollSheetLnDetailsEntity loanDetail)
							throws Exception {
						return ((LoanBO) accountBusinessService
								.getAccount(loanDetail.getAccountId()))
								.isOfProductOffering(productOffering);
					}
				});
	}

	CollSheetSavingsDetailsEntity getSavingProduct(
			CollSheetCustBO collectionSheet,
			final SavingsOfferingBO productOffering) throws Exception {
		return CollectionUtils.find(collectionSheet
				.getCollSheetSavingsDetails(),
				new Predicate<CollSheetSavingsDetailsEntity>() {
					public boolean evaluate(
							CollSheetSavingsDetailsEntity savingDetail)
							throws Exception {
						return ((SavingsBO) accountBusinessService
								.getAccount(savingDetail.getAccountId()))
								.isOfProductOffering(productOffering);
					}
				});
	}
}
