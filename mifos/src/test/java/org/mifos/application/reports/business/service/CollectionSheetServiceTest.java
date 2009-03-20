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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.collectionsheet.business.CollSheetCustBO;
import org.mifos.application.collectionsheet.business.CollectionSheetBO;
import org.mifos.application.collectionsheet.persistence.CollectionSheetPersistence;
import org.mifos.application.collectionsheet.persistence.CollectionSheetReportPersistence;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.reports.business.dto.CollectionSheetReportData;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.DateUtils;
import static org.easymock.classextension.EasyMock.*;

public class CollectionSheetServiceTest extends AbstractCollectionSheetTestCase {
	public CollectionSheetServiceTest() throws SystemException, ApplicationException {
        super();
    }

    private Session session;
	private Transaction transaction;
	HashSet<CollSheetCustBO> generatedCollectionSheets;
	private Date meetingDate;
	private CollSheetCustBO anotherGroup;

	public void testReturnsCollectionSheetForMeetingDateCenter()
			throws Exception {
		List<CollSheetCustBO> retrievedCenterCollectionSheets = collectionSheetService
				.getCollectionSheetForCustomerOnMeetingDate(meetingDate,
						CENTER_ID, LOAN_OFFICER_SHORT_ID, CustomerLevel.CENTER);
		assertEquals(1, retrievedCenterCollectionSheets.size());
		assertEquals(centerCollectionSheet, retrievedCenterCollectionSheets.get(0));
	}

	public void testReturnsCollectionSheetsForGroup() throws Exception {
		List<CollSheetCustBO> meetingsForGroups = collectionSheetService
				.getCollectionSheetForGroups(meetingDate, centerCollectionSheet,
						LOAN_OFFICER_SHORT_ID);
		assertEquals(2, meetingsForGroups.size());
		assertTrue(meetingsForGroups.contains(groupCollectionSheet));
		assertTrue(meetingsForGroups.contains(anotherGroup));
	}

	public void testCollectionSheetForIndividualCustomer() throws Exception {
		List<CollSheetCustBO> collectionSheet = collectionSheetService
				.getCollectionSheetForCustomers(meetingDate, groupCollectionSheet,
						LOAN_OFFICER_SHORT_ID);
		assertEquals(2, collectionSheet.size());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();		
		collectionSheetService = new CollectionSheetService();
		CollSheetCustBO centerUnderDifferentLoanOfficer = new CollSheetCustBO();		
		centerUnderDifferentLoanOfficer.populateInstanceForTest(
				CENTER_ID, "Sample Center Under Different Loan Officer",
				CustomerLevel.CENTER.getValue(), ANY_SHORT_ID, "", ANY_SHORT_ID);
		CollSheetCustBO groupUnderDifferentLoanOfficer = new CollSheetCustBO();
		groupUnderDifferentLoanOfficer.populateInstanceForTest(
				GROUP_ID, "Sample Group", CustomerLevel.GROUP.getValue(),
				ANY_SHORT_ID, "", ANY_SHORT_ID);
		anotherGroup = new CollSheetCustBO();
		anotherGroup.populateInstanceForTest(ANOTHER_GROUP_ID,
				"Another Sample Group", CustomerLevel.GROUP.getValue(),
				ANY_SHORT_ID, "", LOAN_OFFICER_SHORT_ID);

		generatedCollectionSheets = new HashSet<CollSheetCustBO>();
		groupCollectionSheet.setParentCustomerId(CENTER_ID);
		groupUnderDifferentLoanOfficer.setParentCustomerId(CENTER_ID);
		anotherGroup.setParentCustomerId(CENTER_ID);
		generatedCollectionSheets.add(centerCollectionSheet);
		generatedCollectionSheets.add(centerUnderDifferentLoanOfficer);
		generatedCollectionSheets.add(groupCollectionSheet);
		generatedCollectionSheets.add(groupUnderDifferentLoanOfficer);
		generatedCollectionSheets.add(anotherGroup);


		generatedCollectionSheets.addAll(generateClientCollectionSheets(300,
				groupCollectionSheet, LOAN_OFFICER_SHORT_ID));
		generatedCollectionSheets.addAll(generateClientCollectionSheets(302,
				anotherGroup, LOAN_OFFICER_SHORT_ID));
		generatedCollectionSheets.addAll(generateClientCollectionSheets(304,
				anotherGroup, ANY_SHORT_ID));

		meetingDate = DateUtils.getSqlDate(2001, Calendar.JANUARY, 1);
		CollectionSheetBO collectionSheetBO = new CollectionSheetBO();
		collectionSheetBO.populateTestInstance(
				meetingDate, TODAYS_DATE, generatedCollectionSheets,
				CollectionSheetConstants.COLLECTION_SHEET_GENERATION_SUCCESSFUL);

		CollSheetCustBO customerForMeetingDateAfterSpecifiedDate = new CollSheetCustBO();
		customerForMeetingDateAfterSpecifiedDate.populateInstanceForTest(
				CENTER_ID,
				"Customer with collectionsheet after specified meeting date",
				CustomerLevel.CENTER.getValue(), BRANCH_SHORT_ID, "",
				LOAN_OFFICER_SHORT_ID);
		CollectionSheetBO collectionSheetBOForCenterBranchLoanOfficer = new CollectionSheetBO();
		collectionSheetBOForCenterBranchLoanOfficer.populateTestInstance(DateUtils.getSqlDate(2002, Calendar.FEBRUARY, 2), 
				TODAYS_DATE, CollectionUtils.asSet(customerForMeetingDateAfterSpecifiedDate),
				CollectionSheetConstants.COLLECTION_SHEET_GENERATION_SUCCESSFUL);
		
		CollSheetCustBO customerForMeetingDateBeforeSpecifiedDate = new CollSheetCustBO();
		customerForMeetingDateBeforeSpecifiedDate.populateInstanceForTest(
				CENTER_ID,
				"Customer with collection sheet before meeting date",
				CustomerLevel.CENTER.getValue(), BRANCH_SHORT_ID, "",
				LOAN_OFFICER_SHORT_ID);
		CollectionSheetBO collectionSheetBOBeforeSpecifiedMeetingDate = new CollectionSheetBO();
		collectionSheetBOBeforeSpecifiedMeetingDate.populateTestInstance(DateUtils.getSqlDate(2000, Calendar.DECEMBER, 31), 
				TODAYS_DATE, CollectionUtils.asSet(customerForMeetingDateBeforeSpecifiedDate),
				CollectionSheetConstants.COLLECTION_SHEET_GENERATION_SUCCESSFUL);

		session = StaticHibernateUtil.getSessionTL();
		transaction = session.beginTransaction();
		session.save(collectionSheetBO);
		session.save(collectionSheetBOForCenterBranchLoanOfficer);
		session.save(collectionSheetBOBeforeSpecifiedMeetingDate);
	}
	
	public void testCollectionSheetServiceCallsAllLoanOfficerSingleCenterMethod()
			throws Exception {
		CollectionSheetReportPersistence collectionSheetReportPersistenceMock = createMock(CollectionSheetReportPersistence.class);
		expect(
				collectionSheetReportPersistenceMock
						.extractReportDataAllLoanOfficersOneCenter(BRANCH_ID,
								meetingDate, CENTER_ID)).andReturn(
				new ArrayList<CollectionSheetReportData>());
		replay(collectionSheetReportPersistenceMock);
		CollectionSheetService service = new CollectionSheetService(
				collectionSheetReportPersistenceMock);
		service.extractReportData(BRANCH_ID, meetingDate, ALL_LOAN_OFFICER_ID,
				CENTER_ID);
		verify(collectionSheetReportPersistenceMock);
	}
	
	public void testCollectionSheetServiceCallsAllLoanOfficerAllCentersMethod() throws Exception {
		CollectionSheetReportPersistence collectionSheetReportPersistenceMock = createMock(CollectionSheetReportPersistence.class);
		expect(
				collectionSheetReportPersistenceMock
						.extractReportDataAllLoanOfficersAllCenters(BRANCH_ID,
								meetingDate)).andReturn(
				new ArrayList<CollectionSheetReportData>());
		replay(collectionSheetReportPersistenceMock);
		CollectionSheetService service = new CollectionSheetService(
				collectionSheetReportPersistenceMock);
		service.extractReportData(BRANCH_ID, meetingDate, ALL_LOAN_OFFICER_ID,
				ALL_CENTER_ID);
		verify(collectionSheetReportPersistenceMock);		
	}
	
	public void testCollectionSheetServiceCallsOneLoanOfficerAllCentersMethod() throws Exception {
		CollectionSheetReportPersistence collectionSheetReportPersistenceMock = createMock(CollectionSheetReportPersistence.class);
		expect(
				collectionSheetReportPersistenceMock
						.extractReportDataAllCentersUnderLoanOfficer(BRANCH_ID,
								meetingDate, LOAN_OFFICER_ID)).andReturn(
				new ArrayList<CollectionSheetReportData>());
		replay(collectionSheetReportPersistenceMock);
		CollectionSheetService service = new CollectionSheetService(
				collectionSheetReportPersistenceMock);
		service.extractReportData(BRANCH_ID, meetingDate, LOAN_OFFICER_ID,
				ALL_CENTER_ID);
		verify(collectionSheetReportPersistenceMock);				
	}
	
	public void testCollectionSheetServiceCallsOneLoanOfficerOneCenterMethod() throws Exception {
		CollectionSheetReportPersistence collectionSheetReportPersistenceMock = createMock(CollectionSheetReportPersistence.class);
		expect(
				collectionSheetReportPersistenceMock
						.extractReportData(BRANCH_ID,
								meetingDate, LOAN_OFFICER_ID, CENTER_ID)).andReturn(
				new ArrayList<CollectionSheetReportData>());
		replay(collectionSheetReportPersistenceMock);
		CollectionSheetService service = new CollectionSheetService(
				collectionSheetReportPersistenceMock);
		service.extractReportData(BRANCH_ID, meetingDate, LOAN_OFFICER_ID,
				CENTER_ID);
		verify(collectionSheetReportPersistenceMock);		
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		transaction.rollback();
	}
}
