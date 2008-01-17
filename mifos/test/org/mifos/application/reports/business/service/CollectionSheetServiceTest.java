package org.mifos.application.reports.business.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.collectionsheet.business.CollSheetCustBO;
import org.mifos.application.collectionsheet.business.CollectionSheetBO;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.DateUtils;

public class CollectionSheetServiceTest extends AbstractCollectionSheetTestCase {
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
		assertEquals(center, retrievedCenterCollectionSheets.get(0));
	}

	public void testReturnsCollectionSheetsForGroup() throws Exception {
		List<CollSheetCustBO> meetingsForGroups = collectionSheetService
				.getCollectionSheetForGroups(meetingDate, center,
						LOAN_OFFICER_SHORT_ID);
		assertEquals(2, meetingsForGroups.size());
		assertTrue(meetingsForGroups.contains(group));
		assertTrue(meetingsForGroups.contains(anotherGroup));
	}

	public void testCollectionSheetForIndividualCustomer() throws Exception {
		List<CollSheetCustBO> collectionSheet = collectionSheetService
				.getCollectionSheetForCustomers(meetingDate, group,
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
		group.setParentCustomerId(CENTER_ID);
		groupUnderDifferentLoanOfficer.setParentCustomerId(CENTER_ID);
		anotherGroup.setParentCustomerId(CENTER_ID);
		generatedCollectionSheets.add(center);
		generatedCollectionSheets.add(centerUnderDifferentLoanOfficer);
		generatedCollectionSheets.add(group);
		generatedCollectionSheets.add(groupUnderDifferentLoanOfficer);
		generatedCollectionSheets.add(anotherGroup);


		generatedCollectionSheets.addAll(generateClientCollectionSheets(300,
				group, LOAN_OFFICER_SHORT_ID));
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

		session = HibernateUtil.getSessionTL();
		transaction = session.beginTransaction();
		session.save(collectionSheetBO);
		session.save(collectionSheetBOForCenterBranchLoanOfficer);
		session.save(collectionSheetBOBeforeSpecifiedMeetingDate);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		transaction.rollback();
	}
}
