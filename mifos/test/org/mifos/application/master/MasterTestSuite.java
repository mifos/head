package org.mifos.application.master;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.master.business.service.TestMasterBusinessService;
import org.mifos.application.master.persistence.TestMasterPersistence;

public class MasterTestSuite extends TestSuite {
	
	public static Test suite() throws Exception {
		MasterTestSuite testSuite = new MasterTestSuite();
		testSuite.addTestSuite(TestMasterPersistence.class);
		testSuite.addTestSuite(TestMasterBusinessService.class);
		return testSuite;
	}

}
