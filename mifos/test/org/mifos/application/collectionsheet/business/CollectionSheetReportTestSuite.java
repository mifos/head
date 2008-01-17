package org.mifos.application.collectionsheet.business;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.mifos.application.reports.business.service.CollectionSheetReportServiceTest;
import org.mifos.application.reports.business.service.CollectionSheetServiceTest;


public class CollectionSheetReportTestSuite extends TestSuite {
	public static Test suite() throws Exception {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(CollectionSheetBOTest.class);
		suite.addTestSuite(CollectionSheetReportServiceTest.class);
		suite.addTestSuite(CollectionSheetServiceTest.class);
		return suite;
	}
}
