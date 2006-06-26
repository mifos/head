package org.mifos.application.bulkentry;

import junit.framework.Test;
import junit.framework.TestSuite;


import org.mifos.application.bulkentry.business.TestBulkEntryView;
import org.mifos.application.bulkentry.business.service.TestBulkEntryBusinessService;
import org.mifos.application.bulkentry.persistance.TestBulkEntryPersistance;
import org.mifos.application.bulkentry.persistance.service.TestBulkEntryPersistanceService;
import org.mifos.application.bulkentry.struts.action.TestBulkEntryAction;
import org.mifos.application.master.business.service.TestMasterBusinessService;
import org.mifos.application.master.persistence.TestMasterPersistence;
import org.mifos.application.master.persistence.service.TestMasterPersistenceService;
import org.mifos.application.office.persistence.TestOfficePersistence;
import org.mifos.application.office.persistence.service.TestOfficePersistenceService;
import org.mifos.application.personnel.persistence.TestPersonnelPersistence;
import org.mifos.application.personnel.persistence.service.TestPersonnelPersistenceService;
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
		testSuite.addTestSuite(TestMasterPersistence.class);
		testSuite.addTestSuite(TestMasterPersistenceService.class);
		testSuite.addTestSuite(TestMasterBusinessService.class);
		testSuite.addTestSuite(TestOfficePersistence.class);
		testSuite.addTestSuite(TestOfficePersistenceService.class);
		testSuite.addTestSuite(TestPersonnelPersistence.class);
		testSuite.addTestSuite(TestPersonnelPersistenceService.class);
		testSuite.addTestSuite(TestBulkEntryView.class);
		
		return testSuite;
	}

}
