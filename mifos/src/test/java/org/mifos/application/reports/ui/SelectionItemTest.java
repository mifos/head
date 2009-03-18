package org.mifos.application.reports.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.collectionsheet.business.CollSheetCustBO;
import org.mifos.application.collectionsheet.business.CollectionSheetBO;
import org.mifos.application.reports.persistence.SelectionItemPersistence;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.NumberUtils;

public class SelectionItemTest extends MifosIntegrationTest {
	public SelectionItemTest() throws SystemException, ApplicationException {
        super();
    }

    private static final Short LOAN_OFFICER_ID = Short.valueOf("2");
	private static final Short STATUS_FLAG = Short.valueOf("2");
	private static final Short CUST_LEVEL = Short.valueOf("3");
	private static final Integer CUST_ID = Integer.valueOf(1);
	private static final Date FROM_DATE = DateUtils.getDate(2006,
			Calendar.JANUARY, 1);
	private static final Integer CUSTOMER_ID = CUST_ID;
	private static final Integer BRANCH_ID = Integer.valueOf(3);
	private SelectionItemPersistence selectionItemPersistence;
	private Session session;
	private Transaction transaction;

	public void testRetrievesOfficesThroughNamedQuery() throws Exception {
		List<SelectionItem> activeBranchesUnderUser = null;
		try {
			activeBranchesUnderUser = selectionItemPersistence
					.getActiveBranchesUnderUser("1.1");
		}
		catch (Exception e) {
			fail("Should not fail while retrieving SelectionItem");
		}
		assertFalse(activeBranchesUnderUser.isEmpty());
	}

	public void testRetrievesLoanOfficersThroughNamedQuery() throws Exception {
		List<SelectionItem> activeLoanOfficers = null;
		try {
			activeLoanOfficers = selectionItemPersistence
					.getActiveLoanOfficersUnderOffice(BRANCH_ID);
		}
		catch (Exception e) {
			fail("Should not fail while retrieving SelectionItem");
		}
		assertFalse(activeLoanOfficers.isEmpty());
	}

	public void testRetrievesMeetingDateInclusiveOfFromDate() throws Exception {
		Set<CollSheetCustBO> clientSheets = new HashSet<CollSheetCustBO>();
		clientSheets.add(new CollSheetCustBO(CUST_ID, "", CUST_LEVEL,
				BRANCH_ID.shortValue(), "", LOAN_OFFICER_ID));
		CollectionSheetBO collectionSheet = new CollectionSheetBO();
		collectionSheet.populateTestInstance(DateUtils
				.convertToSqlDate(FROM_DATE), DateUtils.currentDateAsSqlDate(),
				clientSheets, STATUS_FLAG);
		session.save(collectionSheet);
		DateSelectionItem meetingOnFromDate = new DateSelectionItem(FROM_DATE);
		List<DateSelectionItem> meetingDates = selectionItemPersistence
				.getMeetingDates(BRANCH_ID,NumberUtils.convertShortToInteger(LOAN_OFFICER_ID), CUSTOMER_ID,
						FROM_DATE);
		assertTrue(meetingDates.contains(meetingOnFromDate));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		selectionItemPersistence = new SelectionItemPersistence();
		session = StaticHibernateUtil.getSessionTL();
		transaction = session.beginTransaction();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		transaction.rollback();
	}
}
