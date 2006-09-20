package org.mifos.application.login;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.login.struts.action.LoginActionTest;
import org.mifos.framework.MifosTestSuite;

public class LoginTestSuite extends MifosTestSuite{

	public LoginTestSuite() {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new LoginTestSuite();
		testSuite.addTestSuite(LoginActionTest.class);
		return testSuite;
	}

}
