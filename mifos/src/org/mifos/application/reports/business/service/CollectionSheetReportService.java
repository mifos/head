package org.mifos.application.reports.business.service;

import static org.mifos.application.reports.ui.DateSelectionItem.NA_MEETING_DATE;
import static org.mifos.application.reports.ui.SelectionItem.ALL_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.ALL_GROUP_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.ALL_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_GROUP_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_GROUP_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.framework.util.helpers.NumberUtils.convertIntegerToShort;
import static org.mifos.framework.util.helpers.NumberUtils.convertShortToInteger;

import java.util.ArrayList;
import java.util.Calendar;
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
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.application.productdefinition.business.service.SavingsPrdBusinessService;
import org.mifos.application.reports.business.dto.CollectionSheetReportDTO;
import org.mifos.application.reports.ui.DateSelectionItem;
import org.mifos.application.reports.ui.SelectionItem;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.NumberUtils;
import org.mifos.framework.util.helpers.Predicate;

// public interface for services used by collection sheet report
public class CollectionSheetReportService implements
		ICollectionSheetReportService {
	private final OfficeBusinessService officeBusinessService;
	private final PersonnelBusinessService personnelBusinessService;
	private final CustomerBusinessService customerBusinessService;
	private final CollectionSheetService collectionSheetService;
	private final LoanPrdBusinessService loanProductBusinessService;
	private final AccountBusinessService accountBusinessService;
	private final SavingsPrdBusinessService savingsProductBusinessService;
	private final ReportProductOfferingService reportProductOfferingService;
	private final IReportsParameterService reportsParameterService;

	CollectionSheetReportService(CollectionSheetService collectionSheetService,
			OfficeBusinessService officeBusinessService,
			PersonnelBusinessService personnelBusinessService,
			CustomerBusinessService customerBusinessService,
			AccountBusinessService accountBusinessService,
			LoanPrdBusinessService loanProductBusinessService,
			SavingsPrdBusinessService savingsProductBusinessService,
			ReportProductOfferingService reportProductOfferingService,
			IReportsParameterService reportsParameterService) {
		super();
		this.collectionSheetService = collectionSheetService;
		this.officeBusinessService = officeBusinessService;
		this.personnelBusinessService = personnelBusinessService;
		this.customerBusinessService = customerBusinessService;
		this.accountBusinessService = accountBusinessService;
		this.loanProductBusinessService = loanProductBusinessService;
		this.savingsProductBusinessService = savingsProductBusinessService;
		this.reportProductOfferingService = reportProductOfferingService;
		this.reportsParameterService = reportsParameterService;
	}

	CollectionSheetReportService() {
		this(new CollectionSheetService(), new OfficeBusinessService(),
				new PersonnelBusinessService(), new CustomerBusinessService(),
				new AccountBusinessService(), new LoanPrdBusinessService(),
				new SavingsPrdBusinessService(),
				new ReportProductOfferingService(),
				new ReportsParameterService());
	}

	public List<SelectionItem> getBranchOffices(Integer userId)
			throws ServiceException, PersistenceException {
		ArrayList<SelectionItem> branchOffices = new ArrayList<SelectionItem>();
		PersonnelBO personnel = personnelBusinessService
				.getPersonnel(convertIntegerToShort(userId));
		List<SelectionItem> offices = new ArrayList<SelectionItem>();
		if (personnel != null)
			offices = reportsParameterService
					.getActiveBranchesUnderUser(personnel.getOfficeSearchId());

		if (offices.isEmpty()) {
			branchOffices.add(NA_BRANCH_OFFICE_SELECTION_ITEM);
		}
		else {
			branchOffices.add(SELECT_BRANCH_OFFICE_SELECTION_ITEM);
			branchOffices.addAll(offices);
		}
		return branchOffices;
	}

	public List<SelectionItem> getActiveLoanOfficers(Integer userId,
			Integer branchIdIntValue) throws ServiceException,
			PersistenceException {
		List<SelectionItem> officers = new ArrayList<SelectionItem>();
		Short branchId = convertIntegerToShort(branchIdIntValue);

		if (isBranchNA(branchId)) {
			officers.add(NA_LOAN_OFFICER_SELECTION_ITEM);
			return officers;
		}
		if (isSelectBranch(branchId)) {
			officers.add(SELECT_LOAN_OFFICER_SELECTION_ITEM);
			return officers;
		}

		PersonnelBO personnel = personnelBusinessService
				.getPersonnel(convertIntegerToShort(userId));
		if (personnel == null) {
			officers.add(NA_LOAN_OFFICER_SELECTION_ITEM);
			return officers;
		}

		if (personnel.isLoanOfficer()) {
			officers.add(SELECT_LOAN_OFFICER_SELECTION_ITEM);
			officers.add(new SelectionItem(personnel.getPersonnelId(),
					personnel.getDisplayName()));
			return officers;
		}

		List<SelectionItem> loanOfficers = reportsParameterService
				.getActiveLoanOfficersUnderOffice(branchId);
		officers.add(loanOfficers.isEmpty() ? NA_LOAN_OFFICER_SELECTION_ITEM
				: ALL_LOAN_OFFICER_SELECTION_ITEM);
		officers.addAll(loanOfficers);
		return officers;
	}

	public List<SelectionItem> getActiveCentersForLoanOfficer(
			Integer loanOfficerIdIntValue, Integer branchIdIntValue)
			throws ServiceException, PersistenceException {
		List<SelectionItem> activeCenters = new ArrayList<SelectionItem>();
		Short loanOfficerId = convertIntegerToShort(loanOfficerIdIntValue);
		if (loanOfficerId == null
				|| SELECT_LOAN_OFFICER_SELECTION_ITEM.sameAs(loanOfficerId)) {
			activeCenters.add(SELECT_CENTER_SELECTION_ITEM);
			return activeCenters;
		}
		if (NA_LOAN_OFFICER_SELECTION_ITEM.sameAs(loanOfficerId)) {
			activeCenters.add(NA_CENTER_SELECTION_ITEM);
			return activeCenters;
		}
		List<SelectionItem> loanOfficers = null;
		if (isLoanOfficerAll(loanOfficerId)) {
			loanOfficers = reportsParameterService
					.getActiveLoanOfficersUnderOffice(convertIntegerToShort(branchIdIntValue));
		}
		else {
			PersonnelBO loanOfficer = personnelBusinessService
					.getPersonnel(loanOfficerId);
			loanOfficers = CollectionUtils.asList(new SelectionItem(loanOfficer
					.getPersonnelId(), loanOfficer.getDisplayName()));
		}
		List<SelectionItem> allCentersUnderBranch = new ArrayList<SelectionItem>();
		for (SelectionItem loanOfficer : loanOfficers) {
			List<SelectionItem> activeCentersUnderUser = reportsParameterService
								.getActiveCentersUnderUser(
										convertIntegerToShort(branchIdIntValue),
										loanOfficer.getId());
			allCentersUnderBranch.addAll(activeCentersUnderUser);
		}
		activeCenters
				.add(allCentersUnderBranch.isEmpty() ? NA_CENTER_SELECTION_ITEM
						: ALL_CENTER_SELECTION_ITEM);
		activeCenters.addAll(allCentersUnderBranch);
		return activeCenters;
	}

	public List<SelectionItem> getActiveGroupsForLoanOfficer(
			Integer loanOfficerId, Integer branchId) throws ServiceException,
			PersistenceException {
		List<SelectionItem> activeGroups = new ArrayList<SelectionItem>();
		Short loanOfficerIdShort = NumberUtils
				.convertIntegerToShort(loanOfficerId);

		if (addCustomerIfSelectOrNALoanOfficer(activeGroups,
				loanOfficerIdShort, CustomerLevel.GROUP))
			return activeGroups;
		List<SelectionItem> allGroupsUnderBranch = new ArrayList<SelectionItem>();
		List<SelectionItem> activeLoanOfficers = new ArrayList<SelectionItem>();
		Short branchIdShort = NumberUtils.convertIntegerToShort(branchId);
		if (isLoanOfficerAll(loanOfficerIdShort)) {
			activeLoanOfficers = reportsParameterService
					.getActiveLoanOfficersUnderOffice(branchIdShort);
		}
		else {
			PersonnelBO loanOfficer = personnelBusinessService
					.getPersonnel(loanOfficerIdShort);
			activeLoanOfficers = CollectionUtils
					.asList(new SelectionItem(loanOfficer.getPersonnelId(),
							loanOfficer.getDisplayName()));
		}
		for (SelectionItem loanOfficer : activeLoanOfficers) {
			allGroupsUnderBranch.addAll(reportsParameterService
					.getActiveGroupsUnderUser(branchIdShort, loanOfficer
							.getId()));
		}
		activeGroups
				.add(allGroupsUnderBranch.isEmpty() ? NA_GROUP_SELECTION_ITEM
						: ALL_GROUP_SELECTION_ITEM);
		activeGroups.addAll(allGroupsUnderBranch);
		return activeGroups;
	}

	private List<DateSelectionItem> getMeetingDates(Integer branchIdIntValue,
			Integer officerIdIntValue, Integer customerId,
			CustomerLevel customerLevel) throws PersistenceException,
			ServiceException {
		List<DateSelectionItem> meetingDates = new ArrayList<DateSelectionItem>();
		if (branchIdIntValue == null
				|| SELECT_BRANCH_OFFICE_SELECTION_ITEM.sameAs(branchIdIntValue)
				|| NA_BRANCH_OFFICE_SELECTION_ITEM.sameAs(branchIdIntValue)
				|| SELECT_LOAN_OFFICER_SELECTION_ITEM.sameAs(officerIdIntValue)
				|| NA_LOAN_OFFICER_SELECTION_ITEM.sameAs(officerIdIntValue)
				|| SELECT_CENTER_SELECTION_ITEM.sameAs(customerId)
				|| NA_CENTER_SELECTION_ITEM.sameAs(customerId)
				|| SELECT_GROUP_SELECTION_ITEM.sameAs(customerId)
				|| NA_GROUP_SELECTION_ITEM.sameAs(customerId)) {
			meetingDates.add(NA_MEETING_DATE);
			return meetingDates;
		}

		Short branchId = convertIntegerToShort(branchIdIntValue);
		Short officerId = convertIntegerToShort(officerIdIntValue);
		if (ALL_CENTER_SELECTION_ITEM.sameAs(customerId)
				|| ALL_GROUP_SELECTION_ITEM.sameAs(customerId)) {
			List<SelectionItem> officers = new ArrayList<SelectionItem>();
			if (ALL_LOAN_OFFICER_SELECTION_ITEM.sameAs(officerIdIntValue)) {
				officers = reportsParameterService
						.getActiveLoanOfficersUnderOffice(branchId);
			}
			else {
				officers = CollectionUtils.asList(new SelectionItem(
						convertIntegerToShort(officerIdIntValue), null));
			}
			for (SelectionItem officer : officers) {
				List<SelectionItem> customers = new ArrayList<SelectionItem>();
				if (CustomerLevel.GROUP.equals(customerLevel))
					customers = reportsParameterService
							.getActiveGroupsUnderUser(branchId, officer.getId());
				else customers = reportsParameterService
						.getActiveCentersUnderUser(branchId, officer.getId());
				for (SelectionItem customer : customers) {
					List<DateSelectionItem> meetingDatesForCustomer = reportsParameterService
							.getMeetingDates(branchId, officer.getId(),
									convertShortToInteger(customer.getId()),
									customerLevel, today());
					if (meetingDatesForCustomer != null)
						meetingDates.addAll(meetingDatesForCustomer);
				}
			}
		}
		else {
			if (isLoanOfficerAll(officerId)) {
				CustomerBO customer = customerBusinessService
						.getCustomer(customerId);
				officerId = customer.getPersonnel().getPersonnelId();
			}
			List<DateSelectionItem> meetingDatesForOfficerBranchCustomer = reportsParameterService
					.getMeetingDates(branchId, officerId, customerId,
							customerLevel, today());
			if (meetingDatesForOfficerBranchCustomer != null)
				meetingDates.addAll(meetingDatesForOfficerBranchCustomer);
		}
		if (meetingDates.isEmpty())
			meetingDates.add(NA_MEETING_DATE);
		return meetingDates;
	}

	public List<DateSelectionItem> getMeetingDatesForGroup(Integer branchId,
			Integer groupId, Integer officerId) throws PersistenceException,
			ServiceException {
		return getMeetingDates(branchId, officerId, groupId,
				CustomerLevel.GROUP);
	}

	public List<DateSelectionItem> getMeetingDatesForCenter(Integer branchId,
			Integer groupId, Integer officerId) throws PersistenceException,
			ServiceException {
		return getMeetingDates(branchId, officerId, groupId,
				CustomerLevel.CENTER);
	}

	private List<CollectionSheetReportDTO> getCollectionSheets(
			Integer branchId, Integer officerId, Integer customerId,
			Date meetingDate, CustomerLevel customerLevel) throws Exception {
		OfficeBO office = officeBusinessService.getOffice(NumberUtils
				.convertIntegerToShort(branchId));
		List<CustomerBO> applicableCustomers = getApplicableCustomers(branchId,
				officerId, customerId, customerLevel);
		java.sql.Date sqlMeetingDate = DateUtils.convertToSqlDate(meetingDate);
		List<CollectionSheetReportDTO> collectionSheetReport = new ArrayList<CollectionSheetReportDTO>();
		for (CustomerBO customer : applicableCustomers) {
			if (CustomerLevel.CENTER.equals(customerLevel))
				collectionSheetReport.addAll(collectCenterLevelData(office,
						customer, sqlMeetingDate));
			else if (CustomerLevel.GROUP.equals(customerLevel)) {
				List<CollSheetCustBO> groupCollectionSheets = collectionSheetService
						.getCollectionSheetForCustomerOnMeetingDate(
								sqlMeetingDate, customer.getCustomerId(),
								NumberUtils.convertIntegerToShort(officerId),
								customerLevel);
				collectionSheetReport.addAll(collectGroupLevelData(office,
						customer, sqlMeetingDate, groupCollectionSheets));
			}
		}
		return collectionSheetReport;
	}

	public List<CollectionSheetReportDTO> getCollectionSheets(Integer branchId,
			Integer officerId, Integer customerId, Date meetingDate,
			Integer customerLevel) throws Exception {
		return getCollectionSheets(branchId, officerId, customerId,
				meetingDate, CustomerLevel.getLevel(NumberUtils
						.convertIntegerToShort(customerLevel)));
	}

	private List<CustomerBO> getApplicableCustomers(Integer branchId,
			Integer officerId, Integer customerId, CustomerLevel level)
			throws ServiceException {
		List<CustomerBO> activeCenters = new ArrayList<CustomerBO>();
		if (ALL_CENTER_SELECTION_ITEM.sameAs(customerId)
				|| ALL_GROUP_SELECTION_ITEM.sameAs(customerId)) {
			List<PersonnelBO> activeLoanOfficers = new ArrayList<PersonnelBO>();
			if (ALL_LOAN_OFFICER_SELECTION_ITEM.sameAs(officerId)) {
				activeLoanOfficers
						.addAll(personnelBusinessService
								.getActiveLoanOfficersUnderOffice(convertIntegerToShort(branchId)));
			}
			else {
				activeLoanOfficers.add(personnelBusinessService
						.getPersonnel(convertIntegerToShort(officerId)));
			}
			for (PersonnelBO loanOfficer : activeLoanOfficers) {
				if (CustomerLevel.CENTER.equals(level))
					activeCenters.addAll(customerBusinessService
							.getActiveCentersUnderUser(loanOfficer));
				else if (CustomerLevel.GROUP.equals(level))
					activeCenters.addAll(customerBusinessService
							.getGroupsUnderUser(loanOfficer));
			}
			return activeCenters;
		}
		activeCenters.add(customerBusinessService.getCustomer(customerId));
		return activeCenters;
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

	LoanOfferingBO getLoanOffering(Short productOfferingId)
			throws ServiceException {
		return loanProductBusinessService.getLoanOffering(productOfferingId);
	}

	SavingsOfferingBO getSavingsOffering(Short savingsOfferingId)
			throws ServiceException {
		return savingsProductBusinessService
				.getSavingsProduct(savingsOfferingId);
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
		LoanOfferingBO loanOffering1 = getLoanOffering(reportProductOfferingService
				.getLoanProduct1());
		LoanOfferingBO loanOffering2 = getLoanOffering(reportProductOfferingService
				.getLoanProduct2());
		SavingsOfferingBO savingsOffering1 = getSavingsOffering(reportProductOfferingService
				.getSavingsProduct1());
		SavingsOfferingBO savingsOffering2 = getSavingsOffering(reportProductOfferingService
				.getSavingsProduct2());
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
									savingsOffering2.getPrdOfferingShortName()));
				}
			}
		}
		catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
		return collectionSheetReport;
	}

	private boolean addCustomerIfSelectOrNALoanOfficer(List customers,
			Short loanOfficerId, CustomerLevel customerLevel) {
		if (loanOfficerId == null
				|| SELECT_LOAN_OFFICER_SELECTION_ITEM.sameAs(loanOfficerId)) {
			customers.add(SelectionItem.SELECT_GROUP_SELECTION_ITEM);
			return true;
		}
		else if (NA_LOAN_OFFICER_SELECTION_ITEM.sameAs(loanOfficerId)) {
			customers.add(NA_GROUP_SELECTION_ITEM);
			return true;
		}
		return false;
	}

	private boolean isSelectBranch(Short branchId) {
		return branchId == null
				|| SELECT_BRANCH_OFFICE_SELECTION_ITEM.getId().equals(branchId);
	}

	private boolean isBranchNA(Short branchId) {
		return NA_BRANCH_OFFICE_SELECTION_ITEM.getId().equals(branchId);
	}

	private boolean isLoanOfficerAll(Short loanOfficerIdShort) {
		return ALL_LOAN_OFFICER_SELECTION_ITEM.sameAs(loanOfficerIdShort);
	}

	private java.sql.Date today() {
		//		return DateUtils.sqlToday();
		return DateUtils.getSqlDate(2007, Calendar.JUNE, 30);
	}

	public void invalidateCachedReportParameters() {
		reportsParameterService.invalidate();
	}
}
