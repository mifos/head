package org.mifos.framework.components;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.framework.MifosTestSuite;
import org.mifos.framework.components.customTableTag.TableTagParserTest;
import org.mifos.framework.components.logger.TestLogging;
import org.mifos.framework.components.logger.TestMessage;
import org.mifos.framework.components.tabletag.TableTagExceptionTest;
import org.mifos.framework.components.taggenerator.TestTagGenerator;

public class ComponentsTestSuite extends MifosTestSuite{
	public ComponentsTestSuite() {
		super();
	}

	public static Test suite()throws Exception	{
		TestSuite testSuite = new ComponentsTestSuite();
		testSuite.addTestSuite(TestTagGenerator.class);
		testSuite.addTestSuite(TestLogging.class);
		testSuite.addTestSuite(TestMessage.class);
		testSuite.addTestSuite(TableTagExceptionTest.class);
		return testSuite;
	}
}
