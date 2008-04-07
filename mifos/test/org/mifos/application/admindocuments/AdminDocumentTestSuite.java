package org.mifos.application.admindocuments;

import org.mifos.application.admindocuments.persistence.TestAdminDocAccStateMixPersistence;
import org.mifos.application.admindocuments.persistence.TestAdminDocumentPersistence;
import org.mifos.application.admindocuments.struts.action.TestBirtAdminDocumentUploadAction;

import junit.framework.Test;
import junit.framework.TestSuite;


public class AdminDocumentTestSuite extends TestSuite {
	public static Test suite() throws Exception {
		TestSuite testSuite = new AdminDocumentTestSuite();
		testSuite.addTestSuite(TestBirtAdminDocumentUploadAction.class);
		testSuite.addTestSuite(TestAdminDocAccStateMixPersistence.class);
		testSuite.addTestSuite(TestAdminDocumentPersistence.class);
		return testSuite;
	}
}
