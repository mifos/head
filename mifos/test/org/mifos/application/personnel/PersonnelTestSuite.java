package org.mifos.application.personnel;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.personnel.business.TestPersonnelBO;
import org.mifos.application.personnel.business.TestPersonnelStatusEntity;
import org.mifos.application.personnel.business.service.PersonnelBusinessServiceTest;
import org.mifos.application.personnel.persistence.TestPersonnelPersistence;
import org.mifos.application.personnel.struts.action.PersonnelSettingsActionTest;
import org.mifos.application.personnel.struts.action.TestPersonAction;
import org.mifos.application.personnel.struts.action.TestPersonnelNoteAction;
import org.mifos.framework.MifosTestSuite;

public class PersonnelTestSuite extends MifosTestSuite {
	public PersonnelTestSuite() {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new PersonnelTestSuite();
		testSuite.addTestSuite(TestPersonnelStatusEntity.class);
		testSuite.addTestSuite(TestPersonnelPersistence.class);
		testSuite.addTestSuite(TestPersonnelBO.class);
		testSuite.addTestSuite(TestPersonAction.class);
		testSuite.addTestSuite(TestPersonnelNoteAction.class);
		testSuite.addTestSuite(PersonnelSettingsActionTest.class);
		testSuite.addTestSuite(PersonnelBusinessServiceTest.class);
		return testSuite;
	}

}
