package org.mifos.application.login;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.login.struts.action.LoginActionTest;

public class LoginTestSuite extends TestSuite{

	public LoginTestSuite() {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new LoginTestSuite();
		testSuite.addTestSuite(LoginActionTest.class);
		return testSuite;
	}

}
