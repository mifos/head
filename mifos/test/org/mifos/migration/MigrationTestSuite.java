package org.mifos.migration;

import junit.framework.Test;
import junit.framework.TestSuite;

public class MigrationTestSuite extends TestSuite {

	public static Test suite() throws Exception {
		TestSuite suite = new MigrationTestSuite();
		suite.addTestSuite(TestMifosDataExchange.class);
		return suite;
	}

}
