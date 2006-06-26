package org.mifos.application.office;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.office.persistence.TestOfficePersistence;
import org.mifos.application.office.persistence.service.TestOfficePersistenceService;
import org.mifos.framework.MifosTestSuite;

public class OfficeTestSuite extends MifosTestSuite{
	public OfficeTestSuite() throws Exception {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new OfficeTestSuite();
		testSuite.addTestSuite(TestOfficePersistence.class);
		testSuite.addTestSuite(TestOfficePersistenceService.class);
		return testSuite;
	}
}
