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
 
package org.mifos.application.reports.business.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.mifos.framework.util.helpers.NumberUtils.convertIntegerToShort;

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
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.personnel.business.CustomerFixture;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.application.productdefinition.business.service.SavingsPrdBusinessService;
import org.mifos.application.reports.business.dto.CollectionSheetReportDTO;
import org.mifos.application.reports.business.dto.CollectionSheetReportData;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FilePaths;

public class CollectionSheetReportServiceIntegrationTest extends
		AbstractCollectionSheetIntegrationTestCase {

	public CollectionSheetReportServiceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private static final Short BSKL_PRODUCT_OFFERING_ID = Short.valueOf("1");
	private static final Short CL_PRODUCT_OFFERING_ID = Short.valueOf("2");
	private static final Short MM1_SAVINGS_PRODUCT_ID = Short.valueOf("4");
	private static final Short MM2_SAVINGS_PRODUCT_ID = Short.valueOf("5");
	private static final Short SK_SAVING_PRD_OFFERING_ID = Short.valueOf("7");

	private static final Integer CL_LOAN_ACCNT_ID = Integer.valueOf(1);
	private static final Integer BSKL_LOAN_ACCNT_ID = Integer.valueOf(2);
	private static final Integer MM1_SAVINGS_ACCNT_ID = Integer.valueOf(3);
	private static final Integer SK_SAVINGS_ACCNT_ID = Integer.valueOf(4);

	private OfficeBusinessService officeBusinessServiceMock;
	private CollectionSheetReportService collectionSheetReportService;
	private CustomerBusinessService customerBusinessServiceMock;
	private PersonnelBusinessService personnelBusinessServiceMock;
	private CollectionSheetService collectionSheetServiceMock;
	private java.sql.Date sqlMeetingDate;
	private Date meetingDate;
	private List<CollSheetCustBO> anyCollectionSheetCustomer;
	private AccountBusinessService accountBusinessServiceMock;
	private LoanPrdBusinessService loanProductBusinessServiceMock;
	private List<CollSheetCustBO> centerCollectionSheets;
	private List<CollSheetCustBO> groupsCollectionSheets;
	private List<CollSheetCustBO> customerCollectionSheets;
	
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
	private String meetingDateStr;

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
				CollectionSheetReportServiceIntegrationTest.BSKL_PRODUCT_OFFERING_ID,
				CollectionSheetReportServiceIntegrationTest.CL_PRODUCT_OFFERING_ID);
		setSavingsOfferingExpectation(
				CollectionSheetReportServiceIntegrationTest.MM1_SAVINGS_PRODUCT_ID,
				CollectionSheetReportServiceIntegrationTest.MM2_SAVINGS_PRODUCT_ID);

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

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		meetingDate = DateUtils.getDate(2000, Calendar.JANUARY, 1);
		meetingDateStr = "01/01/2000";
		sqlMeetingDate = DateUtils.convertToSqlDate(meetingDate);
		MifosLogManager.configure(FilePaths.LOG_CONFIGURATION_FILE);
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
				accountBusinessServiceMock, reportProductOfferingServiceMock,
				new CascadingReportParameterService(
						reportsParameterServiceMock,
						personnelBusinessServiceMock,
						customerBusinessServiceMock));
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
	
	public void testGetReportDataReturnsListOfCollectionSheetReportData() throws Exception {
		expect(collectionSheetServiceMock.extractReportData(BRANCH_ID, meetingDate, PERSONNEL_ANY_ID, CENTER_ID)).andReturn(new ArrayList<CollectionSheetReportData>());
		replay(collectionSheetServiceMock);
		List<CollectionSheetReportData> reportData = collectionSheetReportService.getReportData(BRANCH_ID, meetingDateStr, PERSONNEL_ANY_ID, CENTER_ID);
		verify(collectionSheetServiceMock);
	}
	
}
