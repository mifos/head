package org.mifos.application.reports.ui;

import java.util.List;

import org.mifos.application.reports.persistence.SelectionItemPersistence;
import org.mifos.framework.MifosTestCase;

public class SelectionItemTest extends MifosTestCase {
	private static final Short BRANCH_ID = Short.valueOf("3");
	private SelectionItemPersistence selectionItemPersistence;

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

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		selectionItemPersistence = new SelectionItemPersistence();		
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}	
}