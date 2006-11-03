package org.mifos.framework.security;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.framework.MifosTestSuite;
import org.mifos.framework.security.util.SecurityHelperTest;

public class SecurityTestSuite extends MifosTestSuite {

	public SecurityTestSuite() {
		super();
	}

	public static Test suite() throws Exception{
		TestSuite suite = new SecurityTestSuite();
		suite.addTestSuite(SecurityHelperTest.class);
		return suite;
	}
}
