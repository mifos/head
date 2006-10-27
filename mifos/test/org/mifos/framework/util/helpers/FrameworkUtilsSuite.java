package org.mifos.framework.util.helpers;

import org.mifos.framework.MifosTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FrameworkUtilsSuite extends MifosTestSuite {

	public FrameworkUtilsSuite() {
		super();
	}

    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(FlowManagerHelperTest.class);
        suite.addTestSuite(MifosDoubleConverterTest.class);
        suite.addTestSuite(ConvertionUtilTest.class);
        suite.addTestSuite(MethodInvokerTest.class);
        suite.addTestSuite(MifosNodeTest.class);
        suite.addTestSuite(CacheTest.class);
        suite.addTestSuite(BundleKeyTest.class);
        suite.addTestSuite(MifosSelectHelperTest.class);
        return suite;
    }

}
