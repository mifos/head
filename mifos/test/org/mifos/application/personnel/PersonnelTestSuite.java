package org.mifos.application.personnel;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.personnel.business.TestPersonnelBO;
import org.mifos.application.personnel.business.TestPersonnelStatusEntity;
import org.mifos.application.personnel.persistence.TestPersonnelPersistence;
import org.mifos.application.personnel.persistence.service.TestPersonnelPersistenceService;
import org.mifos.framework.MifosTestSuite;

public class PersonnelTestSuite extends MifosTestSuite {
	public PersonnelTestSuite() {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new PersonnelTestSuite();
		testSuite.addTestSuite(TestPersonnelStatusEntity.class);
		testSuite.addTestSuite(TestPersonnelPersistence.class);
		testSuite.addTestSuite(TestPersonnelPersistenceService.class);
		testSuite.addTestSuite(TestPersonnelBO.class);
		return testSuite;
	}

}
