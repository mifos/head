package org.mifos.application.reports.business.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.mifos.application.reports.ui.SelectionItem.ALL_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.framework.util.CollectionUtils.asList;
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
import org.mifos.application.collectionsheet.business.CollectionSheetCustomerBOFixture;
import org.mifos.application.collectionsheet.business.CollectionSheetLoanDetailsEntityFixture;
import org.mifos.application.collectionsheet.business.CollectionSheetSavingDetailsEntityFixture;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficecFixture;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.personnel.business.CustomerFixture;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelFixture;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.application.productdefinition.business.service.SavingsPrdBusinessService;
import org.mifos.application.reports.business.dto.CollectionSheetReportDTO;
import org.mifos.application.reports.ui.DateSelectionItem;
import org.mifos.application.reports.ui.SelectionItem;
import org.mifos.application.reports.ui.SelectionItemFixture;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FilePaths;

public class CollectionSheetReportServiceTest extends
		AbstractCollectionSheetTestCase {

	private static final Integer PERSONNEL_ANY_ID = Integer.valueOf(100);
	private static final Short PERSONNEL_ANY_SHORT_ID = convertIntegerToShort(PERSONNEL_ANY_ID);
	private static final Short BSKL_PRODUCT_OFFERING_ID = Short.valueOf("1");
	private static final Short CL_PRODUCT_OFFERING_ID = Short.valueOf("2");
	private static final Short MM1_SAVINGS_PRODUCT_ID = Short.valueOf("4");
	private static final Short MM2_SAVINGS_PRODUCT_ID = Short.valueOf("5");
	private static final Short SK_SAVING_PRD_OFFERING_ID = Short.valueOf("7");

	private static final Integer CL_LOAN_ACCNT_ID = Integer.valueOf(1);
	private static final Integer BSKL_LOAN_ACCNT_ID = Integer.valueOf(2);
	private static final Integer MM1_SAVINGS_ACCNT_ID = Integer.valueOf(3);
	private static final Integer SK_SAVINGS_ACCNT_ID = Integer.valueOf(4);

	private static final OfficeBO OFFICE = OfficecFixture
			.createOffice(BRANCH_SHORT_ID);
	private static final SelectionItem OFFICE_SELECTION_ITEM = new SelectionItem(
			OFFICE.getOfficeId(), OFFICE.getOfficeName());
	private static final PersonnelBO ANY_PERSONNEL = PersonnelFixture
			.createNonLoanOfficer(PERSONNEL_ANY_SHORT_ID);
	private OfficeBusinessService officeBusinessServiceMock;
	private CollectionSheetReportService collectionSheetReportService;
	private CustomerBusinessService customerBusinessServiceMock;
	private List<OfficeBO> branchOffices;
	private List<SelectionItem> branchOfficesSelectionItems;
	private PersonnelBusinessService personnelBusinessServiceMock;
	private List<PersonnelBO> loanOfficers;
	private List<SelectionItem> loanOfficersSelectionItems;
	private List<CustomerBO> centers;
	private List<SelectionItem> centerSelectionItems;
	private PersonnelBO anyPersonnel;
	private OfficeBO anyOffice;
	private CollectionSheetService collectionSheetServiceMock;
	private java.sql.Date sqlMeetingDate;
	private Date meetingDate;
	private List<CollSheetCustBO> anyCollectionSheetCustomer;
	private AccountBusinessService accountBusinessServiceMock;
	private LoanPrdBusinessService loanProductBusinessServiceMock;
	private List<CollSheetCustBO> centerCollectionSheets;
	private List<CollSheetCustBO> groupsCollectionSheets;
	private List<CollSheetCustBO> customerCollectionSheets;
	private CenterBO center;
	private CollSheetCustBO groupCollectionSheet;
	private CollSheetCustBO centerCollectionSheet;
	private CollSheetCustBO collectionSheet;
	private CollSheetLnDetailsEntity bsklLoanDetailsEntity;
	private LoanOfferingBO bsklLoanProductOffering;
	private LoanOfferingBO clLoanProductOffering;
	private CollSheetLnDetailsEntity clLoanDetailsEntity;
	private SavingsPrdBusinessService savingsProductBusinessServiceMock;
	private SavingsOfferingBO mm1SavingsProductOffering;
	private CollSheetSavingsDetailsEntity mm1SavingDetailsEntity;
	private SavingsOfferingBO skSavingsProductOffering;
	private CollSheetSavingsDetailsEntity skSavingDetailsEntity;
	private ReportProductOfferingService reportProductOfferingServiceMock;
	private IReportsParameterService reportsParameterServiceMock;

	public void testGetBranchesCallsOfficeBusinessServiceAndChecksSelect()
			throws Exception {
		PersonnelBO personnel = PersonnelFixture
				.createPersonnel(PERSONNEL_ANY_SHORT_ID);

		expect(
				reportsParameterServiceMock
						.getActiveBranchesUnderUser(personnel
								.getOfficeSearchId())).andReturn(
				branchOfficesSelectionItems);
		List<SelectionItem> returnedBranchOffices = retrieveBranchOffices(
				branchOfficesSelectionItems, personnel);
		assertEquals(MAX_COUNT + 1, returnedBranchOffices.size());
		assertEquals(SELECT_BRANCH_OFFICE_SELECTION_ITEM, returnedBranchOffices
				.get(0));
	}

	public void testGetNotApplicableBranchOfficeIfNoneExists() throws Exception {
		List<SelectionItem> returnedBranchOffices = retrieveBranchOffices(
				new ArrayList<SelectionItem>(), null);
		assertEquals(1, returnedBranchOffices.size());
		assertEquals(NA_BRANCH_OFFICE_SELECTION_ITEM, returnedBranchOffices
				.get(0));
	}

	private List<SelectionItem> retrieveBranchOffices(
			List<SelectionItem> branchOffices, PersonnelBO personnel)
			throws ServiceException, PersistenceException {
		expect(
				personnelBusinessServiceMock
						.getPersonnel(PERSONNEL_ANY_SHORT_ID)).andReturn(
				personnel);
		replay(reportsParameterServiceMock);
		replay(personnelBusinessServiceMock);
		List<SelectionItem> returnedBranchOffices = collectionSheetReportService
				.getBranchOffices(PERSONNEL_ANY_ID);
		verify(reportsParameterServiceMock);
		verify(personnelBusinessServiceMock);
		return returnedBranchOffices;
	}

	public void testGetNotApplicableLoanOfficerIfBranchNotApplicable()
			throws Exception {
		expect(
				personnelBusinessServiceMock
						.getPersonnel(PERSONNEL_ANY_SHORT_ID)).andReturn(null);
		replay(personnelBusinessServiceMock);
		retrieveAndAssertLoanOfficer(NA_BRANCH_OFFICE_SELECTION_ITEM.getId(),
				NA_LOAN_OFFICER_SELECTION_ITEM);
	}

	private void setExpectationForAnyUserWhenRetrievingLoanOfficer()
			throws ServiceException {
		expect(
				personnelBusinessServiceMock
						.getPersonnel(PERSONNEL_ANY_SHORT_ID)).andReturn(
				ANY_PERSONNEL);
	}

	public void testGetSelectLoanOfficerIfBranchIdIsSelect() throws Exception {
		expect(
				personnelBusinessServiceMock
						.getPersonnel(PERSONNEL_ANY_SHORT_ID)).andReturn(null);
		replay(personnelBusinessServiceMock);
		retrieveAndAssertLoanOfficer(SELECT_BRANCH_OFFICE_SELECTION_ITEM
				.getId(), SELECT_LOAN_OFFICER_SELECTION_ITEM);
	}

	public void testGetActiveLoanOfficersWithOptionForAllLoanOfficer()
			throws Exception {
		expect(
				personnelBusinessServiceMock
						.getPersonnel(PERSONNEL_ANY_SHORT_ID)).andReturn(
				ANY_PERSONNEL);
		expect(
				reportsParameterServiceMock
						.getActiveLoanOfficersUnderOffice(ANY_SHORT_ID))
				.andReturn(loanOfficersSelectionItems);
		replay(personnelBusinessServiceMock);
		replay(reportsParameterServiceMock);
		List<SelectionItem> activeLoanOfficers = collectionSheetReportService
				.getActiveLoanOfficers(PERSONNEL_ANY_ID, ANY_ID);
		verify(personnelBusinessServiceMock);
		verify(reportsParameterServiceMock);
		assertEquals(MAX_COUNT + 1, activeLoanOfficers.size());
		assertEquals(ALL_LOAN_OFFICER_SELECTION_ITEM, activeLoanOfficers.get(0));
	}

	public void testWhenBranchOfficeIdIsNull() throws Exception {
		expect(
				personnelBusinessServiceMock
						.getPersonnel(PERSONNEL_ANY_SHORT_ID)).andReturn(null);
		replay(personnelBusinessServiceMock);
		List<SelectionItem> activeLoanOfficers = collectionSheetReportService
				.getActiveLoanOfficers(PERSONNEL_ANY_ID, null);
		verify(personnelBusinessServiceMock);
		assertEquals(1, activeLoanOfficers.size());
		assertEquals(SELECT_LOAN_OFFICER_SELECTION_ITEM, activeLoanOfficers
				.get(0));
	}

	public void testRetrieveLoanOfficersReturnNAWhenUserIdDoesNotExist()
			throws Exception {
		expect(personnelBusinessServiceMock.getPersonnel(ANY_SHORT_ID))
				.andReturn(null);
		replay(personnelBusinessServiceMock);
		List<SelectionItem> loanOfficers = collectionSheetReportService
				.getActiveLoanOfficers(ANY_ID, ANY_ID);
		verify(personnelBusinessServiceMock);
		assertEquals(1, loanOfficers.size());
		assertSame(NA_LOAN_OFFICER_SELECTION_ITEM, loanOfficers.get(0));
	}

	public void testOnlySelfLoanOfficerPopulatedIfUserIsLoanOfficer()
			throws Exception {
		expect(
				personnelBusinessServiceMock
						.getPersonnel(PERSONNEL_ANY_SHORT_ID)).andReturn(
				LOAN_OFFICER);
		replay(personnelBusinessServiceMock);
		List<SelectionItem> activeLoanOfficers = collectionSheetReportService
				.getActiveLoanOfficers(PERSONNEL_ANY_ID, BRANCH_ID);
		assertEquals(2, activeLoanOfficers.size());
		assertSame(SELECT_LOAN_OFFICER_SELECTION_ITEM, activeLoanOfficers
				.get(0));
		assertEquals(new SelectionItem(LOAN_OFFICER.getPersonnelId(),
				LOAN_OFFICER.getDisplayName()), activeLoanOfficers.get(1));
		verify(personnelBusinessServiceMock);
	}

	public void testLoanOfficerNAWhenNoneExistsForABranch() throws Exception {
		setExpectationForAnyUserWhenRetrievingLoanOfficer();
		expect(
				reportsParameterServiceMock
						.getActiveLoanOfficersUnderOffice(ANY_SHORT_ID))
				.andReturn(new ArrayList<SelectionItem>());
		replay(personnelBusinessServiceMock);
		replay(reportsParameterServiceMock);
		retrieveAndAssertLoanOfficer(anyOffice.getOfficeId(),
				NA_LOAN_OFFICER_SELECTION_ITEM);
		verify(personnelBusinessServiceMock);
		verify(reportsParameterServiceMock);
	}

	private void retrieveAndAssertLoanOfficer(Short officeId,
			SelectionItem expectedLoanOfficer) throws ServiceException,
			PersistenceException {
		List<SelectionItem> activeLoanOfficers = collectionSheetReportService
				.getActiveLoanOfficers(PERSONNEL_ANY_ID,
						convertShortToInteger(officeId));
		verify(personnelBusinessServiceMock);
		assertEquals(1, activeLoanOfficers.size());
		assertEquals(expectedLoanOfficer, activeLoanOfficers.get(0));
	}

	public void testCenterIsSelectLoanOfficerIsSelect() throws Exception {
		retrieveAndAssertCenterBO(SELECT_LOAN_OFFICER_SELECTION_ITEM.getId(),
				SelectionItem.SELECT_CENTER_SELECTION_ITEM);
	}

	public void testCenterForLoanOfficerIsNotApplicable() throws Exception {
		retrieveAndAssertCenterBO(NA_LOAN_OFFICER_SELECTION_ITEM.getId(),
				SelectionItem.NA_CENTER_SELECTION_ITEM);
	}

	public void testCenterWhenLoanOfficerIdNull() throws Exception {
		retrieveAndAssertCenterBO(null,
				SelectionItem.SELECT_CENTER_SELECTION_ITEM);
	}

	public void testCenterForExistingLoanOfficer() throws Exception {
		expect(
				reportsParameterServiceMock.getActiveCentersUnderUser(
						BRANCH_SHORT_ID, anyPersonnel.getPersonnelId()))
				.andReturn(centerSelectionItems);
		expect(personnelBusinessServiceMock.getPersonnel(ANY_SHORT_ID))
				.andReturn(anyPersonnel);
		replay(reportsParameterServiceMock);
		replay(personnelBusinessServiceMock);
		List<SelectionItem> activeCenters = collectionSheetReportService
				.getActiveCentersForLoanOfficer(ANY_ID, BRANCH_ID);
		assertEquals(MAX_COUNT + 1, activeCenters.size());
		assertEquals(SelectionItem.ALL_CENTER_SELECTION_ITEM, activeCenters
				.get(0));
		verify(reportsParameterServiceMock);
		verify(personnelBusinessServiceMock);
	}

	public void testCenterForAllLoanOfficers() throws Exception {
		ArrayList<SelectionItem> personnels = new ArrayList<SelectionItem>();
		SelectionItem otherPersonnel = SelectionItemFixture
				.createSelectionItem(new Short("2"));
		personnels.add(otherPersonnel);
		personnels.add(SelectionItemFixture.createSelectionItem(anyPersonnel
				.getPersonnelId()));
		expect(
				reportsParameterServiceMock
						.getActiveLoanOfficersUnderOffice(BRANCH_SHORT_ID))
				.andReturn(personnels);
		ArrayList<SelectionItem> anyCustomers = new ArrayList<SelectionItem>();
		SelectionItem anyOneCustomer = SelectionItemFixture
				.createSelectionItem(1);
		anyCustomers.add(anyOneCustomer);
		anyCustomers.add(SelectionItemFixture.createSelectionItem(2));
		ArrayList<SelectionItem> otherCustomers = new ArrayList<SelectionItem>();
		SelectionItem otherOneCustomer = SelectionItemFixture
				.createSelectionItem(3);
		otherCustomers.add(otherOneCustomer);
		otherCustomers.add(SelectionItemFixture.createSelectionItem(4));
		expect(
				reportsParameterServiceMock.getActiveCentersUnderUser(
						BRANCH_SHORT_ID, anyPersonnel.getPersonnelId()))
				.andReturn(anyCustomers);
		expect(
				reportsParameterServiceMock.getActiveCentersUnderUser(
						BRANCH_SHORT_ID, otherPersonnel.getId())).andReturn(
				otherCustomers);
		replay(reportsParameterServiceMock);
		replay(personnelBusinessServiceMock);
		List<SelectionItem> activeCenters = collectionSheetReportService
				.getActiveCentersForLoanOfficer(
						s2i(ALL_LOAN_OFFICER_SELECTION_ITEM.getId()), BRANCH_ID);
		assertEquals(5, activeCenters.size());
		assertEquals(SelectionItem.ALL_CENTER_SELECTION_ITEM, activeCenters
				.get(0));
		assertTrue(activeCenters.contains(anyOneCustomer));
		assertTrue(activeCenters.contains(otherOneCustomer));
		verify(reportsParameterServiceMock);
		verify(personnelBusinessServiceMock);
	}

	private void retrieveAndAssertCenterBO(Short loanOfficerId,
			SelectionItem expectedCenter) throws ServiceException,
			PersistenceException {
		List<SelectionItem> centersForLoanOfficer = collectionSheetReportService
				.getActiveCentersForLoanOfficer(
						convertShortToInteger(loanOfficerId), null);
		assertEquals(1, centersForLoanOfficer.size());
		assertEquals(expectedCenter, centersForLoanOfficer.get(0));
	}

	public void testGetCollectionSheetForGivenBranchLoanOfficerCenterAndMeetingDate()
			throws Exception {
		expect(customerBusinessServiceMock.getCustomer(CENTER_ID)).andReturn(
				center);
		expect(
				collectionSheetServiceMock
						.getCollectionSheetForCustomerOnMeetingDate(
								sqlMeetingDate, CENTER_ID,
								LOAN_OFFICER_SHORT_ID, CustomerLevel.CENTER))
				.andReturn(centerCollectionSheets);
		expect(
				collectionSheetServiceMock.getCollectionSheetForGroups(
						sqlMeetingDate, centerCollectionSheets.get(0),
						LOAN_OFFICER_SHORT_ID)).andReturn(
				groupsCollectionSheets);

		expect(
				collectionSheetServiceMock.getCollectionSheetForCustomers(
						sqlMeetingDate, groupsCollectionSheets.get(0),
						LOAN_OFFICER_SHORT_ID)).andReturn(
				customerCollectionSheets);

		setProductOfferingExpectation();
		expect(officeBusinessServiceMock.getOffice(BRANCH_SHORT_ID)).andReturn(
				OFFICE);
		replay(collectionSheetServiceMock);
		replay(customerBusinessServiceMock);
		replay(officeBusinessServiceMock);
		replay(loanProductBusinessServiceMock);
		replay(savingsProductBusinessServiceMock);
		replay(reportProductOfferingServiceMock);
		List<CollectionSheetReportDTO> collectionSheets = collectionSheetReportService
				.getCollectionSheets(BRANCH_ID, LOAN_OFFICER_ID, CENTER_ID,
						sqlMeetingDate);
		verify(collectionSheetServiceMock);
		verify(customerBusinessServiceMock);
		verify(officeBusinessServiceMock);
		verify(loanProductBusinessServiceMock);
		verify(savingsProductBusinessServiceMock);
		verify(reportProductOfferingServiceMock);
		assertEquals(2, collectionSheets.size());
	}

	private void setProductOfferingExpectation() throws ServiceException {
		setLoanOfferingExpectation(
				CollectionSheetReportServiceTest.BSKL_PRODUCT_OFFERING_ID,
				CollectionSheetReportServiceTest.CL_PRODUCT_OFFERING_ID);
		setSavingsOfferingExpectation(
				CollectionSheetReportServiceTest.MM1_SAVINGS_PRODUCT_ID,
				CollectionSheetReportServiceTest.MM2_SAVINGS_PRODUCT_ID);

	}

	private void setSavingsOfferingExpectation(Short savingProductOffering1,
			Short savingProductOffering2) throws ServiceException {
		expect(reportProductOfferingServiceMock.getSavingsOffering1())
				.andReturn(
						SavingsOfferingBO
								.createInstanceForTest(savingProductOffering1));
		expect(reportProductOfferingServiceMock.getSavingsOffering2())
				.andReturn(
						SavingsOfferingBO
								.createInstanceForTest(savingProductOffering2));
	}

	private void setLoanOfferingExpectation(Short loanProductOffering1,
			Short loanProductOffering2) throws ServiceException {
		expect(reportProductOfferingServiceMock.getLoanOffering1()).andReturn(
				LoanOfferingBO.createInstanceForTest(loanProductOffering1));
		expect(reportProductOfferingServiceMock.getLoanOffering2()).andReturn(
				LoanOfferingBO.createInstanceForTest(loanProductOffering2));
	}

	public void testGetCollectionSheetForAllCenterOffices() throws Exception {
		expect(personnelBusinessServiceMock.getPersonnel(LOAN_OFFICER_SHORT_ID))
				.andReturn(LOAN_OFFICER);
		expect(
				customerBusinessServiceMock
						.getActiveCentersUnderUser(LOAN_OFFICER)).andReturn(
				centers);
		expect(officeBusinessServiceMock.getOffice(BRANCH_SHORT_ID)).andReturn(
				OFFICE);

		for (CustomerBO center : centers) {
			expect(
					collectionSheetServiceMock
							.getCollectionSheetForCustomerOnMeetingDate(
									sqlMeetingDate, center.getCustomerId(),
									center.getPersonnel().getPersonnelId(),
									CustomerLevel.CENTER)).andReturn(
					centerCollectionSheets);
			expect(
					collectionSheetServiceMock.getCollectionSheetForGroups(
							sqlMeetingDate, centerCollectionSheets.get(0),
							LOAN_OFFICER_SHORT_ID)).andReturn(
					groupsCollectionSheets);

			expect(
					collectionSheetServiceMock.getCollectionSheetForCustomers(
							sqlMeetingDate, groupCollectionSheet,
							LOAN_OFFICER_SHORT_ID)).andReturn(
					anyCollectionSheetCustomer);
			setProductOfferingExpectation();
		}

		replay(customerBusinessServiceMock);
		replay(personnelBusinessServiceMock);
		replay(collectionSheetServiceMock);
		replay(officeBusinessServiceMock);
		replay(loanProductBusinessServiceMock);
		replay(savingsProductBusinessServiceMock);
		replay(reportProductOfferingServiceMock);
		List<CollectionSheetReportDTO> collectionSheets = collectionSheetReportService
				.getCollectionSheets(BRANCH_ID, LOAN_OFFICER_ID, ALL_CENTER_ID,
						meetingDate);
		verify(customerBusinessServiceMock);
		verify(personnelBusinessServiceMock);
		verify(collectionSheetServiceMock);
		verify(officeBusinessServiceMock);
		verify(loanProductBusinessServiceMock);
		verify(savingsProductBusinessServiceMock);
		verify(reportProductOfferingServiceMock);
		assertEquals(MAX_COUNT, collectionSheets.size());
	}

	public void testCollectionSheetForAllLoanOfficerAllCenterOffices()
			throws Exception {
		expect(
				personnelBusinessServiceMock
						.getActiveLoanOfficersUnderOffice(convertIntegerToShort(BRANCH_ID)))
				.andReturn(loanOfficers);
		int centerId = 100;
		for (PersonnelBO loanOfficer : loanOfficers) {
			centerId++;
			ArrayList<CustomerBO> center = new ArrayList<CustomerBO>();
			center.add(CustomerFixture.createCenterBO(
					Integer.valueOf(centerId), loanOfficer));
			expect(
					customerBusinessServiceMock
							.getActiveCentersUnderUser(loanOfficer)).andReturn(
					center);
			expect(
					collectionSheetServiceMock
							.getCollectionSheetForCustomerOnMeetingDate(
									sqlMeetingDate, Integer.valueOf(centerId),
									loanOfficer.getPersonnelId(),
									CustomerLevel.CENTER)).andReturn(
					centerCollectionSheets);
			expect(
					collectionSheetServiceMock.getCollectionSheetForGroups(
							sqlMeetingDate, centerCollectionSheet, loanOfficer
									.getPersonnelId())).andReturn(
					groupsCollectionSheets);
			expect(
					collectionSheetServiceMock.getCollectionSheetForCustomers(
							sqlMeetingDate, groupCollectionSheet, loanOfficer
									.getPersonnelId())).andReturn(
					customerCollectionSheets);
			setProductOfferingExpectation();
		}
		expect(officeBusinessServiceMock.getOffice(BRANCH_SHORT_ID)).andReturn(
				OFFICE);
		replay(personnelBusinessServiceMock);
		replay(customerBusinessServiceMock);
		replay(collectionSheetServiceMock);
		replay(officeBusinessServiceMock);
		replay(loanProductBusinessServiceMock);
		replay(savingsProductBusinessServiceMock);
		replay(reportProductOfferingServiceMock);
		List<CollectionSheetReportDTO> collectionSheets = collectionSheetReportService
				.getCollectionSheets(BRANCH_ID, ALL_LOAN_OFFICER_ID,
						ALL_CENTER_ID, meetingDate);
		verify(personnelBusinessServiceMock);
		verify(customerBusinessServiceMock);
		verify(collectionSheetServiceMock);
		verify(officeBusinessServiceMock);
		verify(loanProductBusinessServiceMock);
		verify(savingsProductBusinessServiceMock);
		verify(reportProductOfferingServiceMock);
		assertEquals(MAX_COUNT * 2, collectionSheets.size());
	}

	public void testBsklProductFilterReturnsLoanProductWithBsklOfferingType()
			throws Exception {
		collectionSheet.addCollectionSheetLoanDetail(bsklLoanDetailsEntity);
		expect(reportProductOfferingServiceMock.getLoanOffering1()).andReturn(
				bsklLoanProductOffering);
		expect(accountBusinessServiceMock.getAccount(BSKL_LOAN_ACCNT_ID))
				.andReturn(
						LoanBO.createInstanceForTest(bsklLoanProductOffering));
		replay(accountBusinessServiceMock);
		replay(reportProductOfferingServiceMock);
		CollSheetLnDetailsEntity filteredLoanDetailEntity = collectionSheetReportService
				.getLoanProduct(collectionSheet,
						reportProductOfferingServiceMock.getLoanOffering1());
		assertNotNull(filteredLoanDetailEntity);
		assertSame(bsklLoanDetailsEntity, filteredLoanDetailEntity);
		verify(reportProductOfferingServiceMock);
		verify(accountBusinessServiceMock);
	}

	public void testClLoanProductFilterReturnsLoanProductWithClLoanOfferingType()
			throws Exception {
		collectionSheet.addCollectionSheetLoanDetail(clLoanDetailsEntity);
		expect(reportProductOfferingServiceMock.getLoanOffering2()).andReturn(
				clLoanProductOffering);
		replay(reportProductOfferingServiceMock);
		expect(accountBusinessServiceMock.getAccount(CL_LOAN_ACCNT_ID))
				.andReturn(LoanBO.createInstanceForTest(clLoanProductOffering));

		replay(accountBusinessServiceMock);

		CollSheetLnDetailsEntity filteredLoanDetailEntity = collectionSheetReportService
				.getLoanProduct(collectionSheet,
						reportProductOfferingServiceMock.getLoanOffering2());
		assertNotNull(filteredLoanDetailEntity);
		assertSame(clLoanDetailsEntity, filteredLoanDetailEntity);
		verify(reportProductOfferingServiceMock);
		verify(accountBusinessServiceMock);
	}

	public void testMm1SavingsProductFilterReturnsSavingsProductWithMm1OfferingType()
			throws Exception {
		collectionSheet.addCollectionSheetSavingsDetail(mm1SavingDetailsEntity);
		expect(reportProductOfferingServiceMock.getSavingsOffering1())
				.andReturn(mm1SavingsProductOffering);

		SavingsBO mm1SavingsBO = new SavingsBO();
		mm1SavingsBO.populateInstanceForTest(mm1SavingsProductOffering);
		expect(accountBusinessServiceMock.getAccount(MM1_SAVINGS_ACCNT_ID))
				.andReturn(mm1SavingsBO);

		replay(reportProductOfferingServiceMock);
		replay(accountBusinessServiceMock);

		CollSheetSavingsDetailsEntity filteredSavingsDetailEntity = collectionSheetReportService
				.getSavingProduct(collectionSheet,
						reportProductOfferingServiceMock.getSavingsOffering1());
		assertNotNull(filteredSavingsDetailEntity);
		assertSame(mm1SavingDetailsEntity, filteredSavingsDetailEntity);
		verify(reportProductOfferingServiceMock);
		verify(accountBusinessServiceMock);
	}

	public void testSkSavingsProductFilterReturnsSavingsProductWithMm1OfferingType()
			throws Exception {
		collectionSheet.addCollectionSheetSavingsDetail(skSavingDetailsEntity);
		expect(reportProductOfferingServiceMock.getSavingsOffering2())
				.andReturn(skSavingsProductOffering);

		SavingsBO skSavingsBO = new SavingsBO();
		skSavingsBO.populateInstanceForTest(skSavingsProductOffering);
		expect(accountBusinessServiceMock.getAccount(SK_SAVINGS_ACCNT_ID))
				.andReturn(skSavingsBO);

		replay(reportProductOfferingServiceMock);
		replay(accountBusinessServiceMock);

		CollSheetSavingsDetailsEntity filteredSavingsDetailEntity = collectionSheetReportService
				.getSavingProduct(collectionSheet,
						reportProductOfferingServiceMock.getSavingsOffering2());
		assertNotNull(filteredSavingsDetailEntity);
		assertSame(skSavingDetailsEntity, filteredSavingsDetailEntity);
		verify(reportProductOfferingServiceMock);
		verify(accountBusinessServiceMock);
	}


	public void testGetMeetingDatesForCenterWhenSpecifiedBranchOfficerAndCenter()
			throws Exception {
		expect(
				reportsParameterServiceMock.getMeetingDates(BRANCH_SHORT_ID,
						LOAN_OFFICER_SHORT_ID, CENTER_ID, DateUtils.sqlToday()))
				.andReturn(new ArrayList<DateSelectionItem>());
		replay(reportsParameterServiceMock);
		collectionSheetReportService.getMeetingDatesForCenter(BRANCH_ID,
				CENTER_ID, LOAN_OFFICER_ID);
		verify(reportsParameterServiceMock);
	}

	public void testGetMeetingDatesForCeneterWhenSpecifiedBranchAllOfficerAndCenterParameters()
			throws Exception {
		expect(
				reportsParameterServiceMock.getMeetingDates(BRANCH_SHORT_ID,
						center.getPersonnel().getPersonnelId(), CENTER_ID,
						DateUtils.sqlToday())).andReturn(
				new ArrayList<DateSelectionItem>());
		expect(customerBusinessServiceMock.getCustomer(CENTER_ID)).andReturn(
				center);
		replay(customerBusinessServiceMock);
		replay(reportsParameterServiceMock);
		collectionSheetReportService.getMeetingDatesForCenter(BRANCH_ID,
				CENTER_ID, ALL_LOAN_OFFICER_ID);
		verify(customerBusinessServiceMock);
		verify(reportsParameterServiceMock);
	}

	public void testGetMeetingDatesForCenterWhenNACenter() throws Exception {
		List<org.mifos.application.reports.ui.DateSelectionItem> meetingDates = collectionSheetReportService
				.getMeetingDatesForCenter(
						BRANCH_ID,
						convertShortToInteger(NA_CENTER_SELECTION_ITEM.getId()),
						convertShortToInteger(NA_LOAN_OFFICER_SELECTION_ITEM
								.getId()));
		assertEquals(1, meetingDates.size());
	}

	public void testForALoanOfficerUserOnlyBranchOfficeRelatedPopulated()
			throws Exception {
		expect(personnelBusinessServiceMock.getPersonnel(LOAN_OFFICER_SHORT_ID))
				.andReturn(LOAN_OFFICER);
		expect(
				reportsParameterServiceMock
						.getActiveBranchesUnderUser(LOAN_OFFICER
								.getOfficeSearchId())).andReturn(
				asList(OFFICE_SELECTION_ITEM));
		replay(personnelBusinessServiceMock);
		replay(reportsParameterServiceMock);
		List<SelectionItem> offices = collectionSheetReportService
				.getBranchOffices(LOAN_OFFICER_ID);
		verify(personnelBusinessServiceMock);
		verify(reportsParameterServiceMock);
		assertEquals(2, offices.size());
		assertSame(SELECT_BRANCH_OFFICE_SELECTION_ITEM, offices.get(0));
		assertSame(OFFICE_SELECTION_ITEM, offices.get(1));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		meetingDate = DateUtils.getDate(2000, Calendar.JANUARY, 1);
		sqlMeetingDate = DateUtils.convertToSqlDate(meetingDate);
		MifosLogManager.configure(FilePaths.LOGFILE);
		collectionSheetServiceMock = createMock(CollectionSheetService.class);
		officeBusinessServiceMock = createMock(OfficeBusinessService.class);
		personnelBusinessServiceMock = createMock(PersonnelBusinessService.class);
		customerBusinessServiceMock = createMock(CustomerBusinessService.class);
		accountBusinessServiceMock = createMock(AccountBusinessService.class);
		loanProductBusinessServiceMock = createMock(LoanPrdBusinessService.class);
		savingsProductBusinessServiceMock = createMock(SavingsPrdBusinessService.class);
		reportProductOfferingServiceMock = createMock(ReportProductOfferingService.class);
		reportsParameterServiceMock = createMock(ReportsParameterService.class);
		collectionSheetReportService = new CollectionSheetReportService(
				collectionSheetServiceMock, officeBusinessServiceMock,
				personnelBusinessServiceMock, accountBusinessServiceMock,
				reportProductOfferingServiceMock,
				new CascadingReportParameterService(
						reportsParameterServiceMock,
						personnelBusinessServiceMock,
						customerBusinessServiceMock));
		branchOffices = new ArrayList<OfficeBO>();
		branchOfficesSelectionItems = new ArrayList<SelectionItem>();
		loanOfficers = new ArrayList<PersonnelBO>();
		loanOfficersSelectionItems = new ArrayList<SelectionItem>();
		centers = new ArrayList<CustomerBO>();
		centerSelectionItems = new ArrayList<SelectionItem>();
		for (int i = 0; i < MAX_COUNT; i++) {
			OfficeBO office = OfficecFixture.createOffice(String.valueOf(i));
			branchOffices.add(office);
			branchOfficesSelectionItems.add(new SelectionItem(office
					.getOfficeId(), office.getOfficeName()));
			PersonnelBO loanOfficer = PersonnelFixture
					.createPersonnel(new Short(String.valueOf(i)));
			loanOfficers.add(loanOfficer);
			loanOfficersSelectionItems.add(new SelectionItem(loanOfficer
					.getPersonnelId(), loanOfficer.getDisplayName()));
			CenterBO centerBO = CustomerFixture.createCenterBO(Integer
					.valueOf(i), LOAN_OFFICER);
			centers.add(centerBO);
			centerSelectionItems.add(new SelectionItem(i2s(centerBO
					.getCustomerId()), centerBO.getDisplayName()));
		}
		anyPersonnel = PersonnelFixture.createPersonnel(ANY_SHORT_ID);
		anyOffice = OfficecFixture.createOffice(ANY_SHORT_ID);
		anyCollectionSheetCustomer = new ArrayList<CollSheetCustBO>();
		anyCollectionSheetCustomer.add(CollectionSheetCustomerBOFixture
				.createCollectionSheet());

		centerCollectionSheets = new ArrayList<CollSheetCustBO>();
		centerCollectionSheet = generateCollectionSheet(CENTER_ID,
				LOAN_OFFICER_SHORT_ID, CustomerLevel.CENTER);
		centerCollectionSheets.add(centerCollectionSheet);

		groupsCollectionSheets = new ArrayList<CollSheetCustBO>();
		groupCollectionSheet = generateCollectionSheet(GROUP_ID,
				LOAN_OFFICER_SHORT_ID, CustomerLevel.GROUP);
		groupsCollectionSheets.add(groupCollectionSheet);


		customerCollectionSheets = generateClientCollectionSheets(700,
				groupCollectionSheet, LOAN_OFFICER_SHORT_ID);

		center = CustomerFixture.createCenterBO(CENTER_ID, LOAN_OFFICER);

		collectionSheet = CollectionSheetCustomerBOFixture
				.createCollectionSheet();
		clLoanDetailsEntity = CollectionSheetLoanDetailsEntityFixture
				.createLoanDetails(CL_LOAN_ACCNT_ID);
		bsklLoanDetailsEntity = CollectionSheetLoanDetailsEntityFixture
				.createLoanDetails(BSKL_LOAN_ACCNT_ID);
		mm1SavingDetailsEntity = CollectionSheetSavingDetailsEntityFixture
				.createSavingsDetails(MM1_SAVINGS_ACCNT_ID);
		skSavingDetailsEntity = CollectionSheetSavingDetailsEntityFixture
				.createSavingsDetails(SK_SAVINGS_ACCNT_ID);
		bsklLoanProductOffering = LoanOfferingBO
				.createInstanceForTest(BSKL_PRODUCT_OFFERING_ID);
		clLoanProductOffering = LoanOfferingBO
				.createInstanceForTest(CL_PRODUCT_OFFERING_ID);
		mm1SavingsProductOffering = SavingsOfferingBO
				.createInstanceForTest(MM1_SAVINGS_PRODUCT_ID);
		skSavingsProductOffering = SavingsOfferingBO
				.createInstanceForTest(SK_SAVING_PRD_OFFERING_ID);

	}
}
