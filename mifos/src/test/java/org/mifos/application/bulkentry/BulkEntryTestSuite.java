package org.mifos.application.bulkentry;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.bulkentry.business.TestBulkEntryView;
import org.mifos.application.bulkentry.business.service.TestBulkEntryBusinessService;
import org.mifos.application.bulkentry.persistance.TestBulkEntryPersistence;
import org.mifos.application.bulkentry.persistance.service.TestBulkEntryPersistenceService;
import org.mifos.application.bulkentry.struts.action.TestBulkEntryAction;
import org.mifos.application.bulkentry.struts.uihelpers.BulkEntryDisplayHelperTest;

public class BulkEntryTestSuite extends TestSuite {

	public BulkEntryTestSuite() throws Exception {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new BulkEntryTestSuite();
		testSuite.addTestSuite(TestBulkEntryPersistence.class);
		testSuite.addTestSuite(TestBulkEntryPersistenceService.class);
		testSuite.addTestSuite(TestBulkEntryBusinessService.class);
		testSuite.addTestSuite(TestBulkEntryAction.class);
		testSuite.addTestSuite(TestBulkEntryView.class);
		testSuite.addTestSuite(BulkEntryDisplayHelperTest.class);
		return testSuite;
	}

}
