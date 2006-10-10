package org.mifos.application.bulkentry;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.bulkentry.business.TestBulkEntryView;
import org.mifos.application.bulkentry.business.service.TestBulkEntryBusinessService;
import org.mifos.application.bulkentry.persistance.TestBulkEntryPersistance;
import org.mifos.application.bulkentry.persistance.service.TestBulkEntryPersistanceService;
import org.mifos.application.bulkentry.struts.action.TestBulkEntryAction;
import org.mifos.application.bulkentry.struts.uihelpers.BulkEntryDisplayHelperTest;
import org.mifos.framework.MifosTestSuite;

public class BulkEntryTestSuite extends MifosTestSuite {

	public BulkEntryTestSuite() throws Exception {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new BulkEntryTestSuite();
		testSuite.addTestSuite(TestBulkEntryPersistance.class);
		testSuite.addTestSuite(TestBulkEntryPersistanceService.class);
		testSuite.addTestSuite(TestBulkEntryBusinessService.class);
		testSuite.addTestSuite(TestBulkEntryAction.class);
		testSuite.addTestSuite(TestBulkEntryView.class);
		testSuite.addTestSuite(BulkEntryDisplayHelperTest.class);
		return testSuite;
	}

}
