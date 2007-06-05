package org.mifos.framework.security;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.framework.security.util.LoginFilterTest;
import org.mifos.framework.security.util.SecurityHelperTest;

public class SecurityTestSuite extends TestSuite {

    public static Test suite() throws Exception{
        TestSuite suite = new SecurityTestSuite();
        suite.addTestSuite(SecurityHelperTest.class);
        suite.addTestSuite(LoginFilterTest.class);
        suite.addTest(AddActivityTest.suite());
        return suite;
    }
}
