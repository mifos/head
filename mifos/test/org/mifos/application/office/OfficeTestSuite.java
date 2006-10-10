package org.mifos.application.office;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.office.business.TestOfficeBO;
import org.mifos.application.office.business.service.OfficeHierarchyBusinessServiceTest;
import org.mifos.application.office.business.service.TestOfficeBusinessService;
import org.mifos.application.office.persistence.OfficeHierarchyPersistenceTest;
import org.mifos.application.office.persistence.TestOfficePersistence;
import org.mifos.application.office.struts.action.OffHierarchyActionTest;
import org.mifos.application.office.struts.action.TestOfficeAction;
import org.mifos.framework.MifosTestSuite;

public class OfficeTestSuite extends MifosTestSuite {
	public OfficeTestSuite() throws Exception {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new OfficeTestSuite();
		testSuite.addTestSuite(TestOfficePersistence.class);
		testSuite.addTestSuite(TestOfficeAction.class);
		testSuite.addTestSuite(TestOfficeBO.class);
		testSuite.addTestSuite(TestOfficeBusinessService.class);
		testSuite.addTestSuite(OfficeHierarchyPersistenceTest.class);
		testSuite.addTestSuite(OfficeHierarchyBusinessServiceTest.class);
		testSuite.addTestSuite(OffHierarchyActionTest.class);
		return testSuite;
	}
}
