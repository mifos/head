package org.mifos.framework.util.helpers;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FrameworkUtilsSuite extends TestSuite {

	public FrameworkUtilsSuite() {
		super();
	}

    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(FlowManagerHelperTest.class);
        suite.addTestSuite(FlowManagerTest.class);
        suite.addTestSuite(FlowTest.class);
        suite.addTestSuite(MifosDoubleConverterTest.class);
        suite.addTestSuite(ConvertionUtilTest.class);
        suite.addTestSuite(MifosNodeTest.class);
        suite.addTestSuite(CacheTest.class);
        suite.addTestSuite(BundleKeyTest.class);
        suite.addTestSuite(MifosSelectHelperTest.class);
        return suite;
    }

}
