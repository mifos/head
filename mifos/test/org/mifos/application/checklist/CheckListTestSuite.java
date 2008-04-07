package org.mifos.application.checklist;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.checklist.business.TestCheckListBO;
import org.mifos.application.checklist.business.service.TestCheckListBusinessService;
import org.mifos.application.checklist.persistence.TestCheckListPersistence;
import org.mifos.application.checklist.struts.action.TestChkListAction;

public class CheckListTestSuite extends TestSuite {

	public static void main(String[] args) {
		Test testSuite = suite();
		TestRunner.run(testSuite);
	}

	public static Test suite() {
		TestSuite testSuite = new CheckListTestSuite();
		testSuite.addTestSuite(TestCheckListBO.class);
		testSuite.addTestSuite(TestCheckListBusinessService.class);
		testSuite.addTestSuite(TestCheckListPersistence.class);
		testSuite.addTestSuite(TestChkListAction.class);
		return testSuite;
	}
}
