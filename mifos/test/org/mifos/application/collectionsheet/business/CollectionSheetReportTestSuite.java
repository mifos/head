package org.mifos.application.collectionsheet.business;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.reports.business.service.CollectionSheetReportServiceTest;
import org.mifos.application.reports.business.service.CollectionSheetServiceTest;
import org.mifos.application.reports.business.service.ReportProductOfferingServiceTest;
import org.mifos.application.reports.business.service.ReportServiceFactoryTest;


public class CollectionSheetReportTestSuite extends TestSuite {
	public static Test suite() throws Exception {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(CollectionSheetBOTest.class);
		suite.addTestSuite(CollectionSheetReportServiceTest.class);
		suite.addTestSuite(CollectionSheetServiceTest.class);
		suite.addTestSuite(ReportServiceFactoryTest.class);
		suite.addTestSuite(ReportProductOfferingServiceTest.class);
		return suite;
	}
}
