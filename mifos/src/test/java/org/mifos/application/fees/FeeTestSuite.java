package org.mifos.application.fees;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.fees.business.FeeBOTest;
import org.mifos.application.fees.business.service.TestFeeBusinessService;
import org.mifos.application.fees.persistence.FeePersistenceTest;
import org.mifos.application.fees.struts.action.FeeActionTest;

public class FeeTestSuite extends TestSuite {

	public FeeTestSuite() {
		super();
	}

	public static void main(String[] args) {
		try {
			Test testSuite = suite();
			TestRunner.run(testSuite);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Test suite() throws Exception {
		FeeTestSuite testSuite = new FeeTestSuite();
		testSuite.addTestSuite(FeeActionTest.class);
		testSuite.addTestSuite(FeeBOTest.class);
		testSuite.addTestSuite(FeePersistenceTest.class);
		testSuite.addTestSuite(TestFeeBusinessService.class);
		return testSuite;
	}
}
