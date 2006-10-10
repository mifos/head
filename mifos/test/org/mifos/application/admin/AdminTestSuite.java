package org.mifos.application.admin;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.admin.struts.action.TestAdminAction;
import org.mifos.framework.MifosTestSuite;

public class AdminTestSuite extends MifosTestSuite{

	public AdminTestSuite() throws Exception {
		super();
	}

	public static void main(String[] args){
		try{
			Test testSuite = suite();
			TestRunner.run (testSuite);
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new AdminTestSuite();
		testSuite.addTestSuite(TestAdminAction.class);
		return testSuite;
	}
}
