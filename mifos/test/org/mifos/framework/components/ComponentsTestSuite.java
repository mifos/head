package org.mifos.framework.components;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.framework.MifosTestSuite;
import org.mifos.framework.components.taggenerator.TestTagGenerator;

public class ComponentsTestSuite extends MifosTestSuite{
	public ComponentsTestSuite() {
		super();
	}

	public static Test suite()throws Exception	{
		TestSuite testSuite = new ComponentsTestSuite();
		testSuite.addTestSuite(TestTagGenerator.class);
		return testSuite;
	}
}
