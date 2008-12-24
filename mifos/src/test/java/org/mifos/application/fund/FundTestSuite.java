package org.mifos.application.fund;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.fund.business.FundBOTest;
import org.mifos.application.fund.business.service.FundBusinessServiceTest;
import org.mifos.application.fund.persistence.FundPersistenceTest;
import org.mifos.application.fund.struts.action.FundActionTest;

public class FundTestSuite extends TestSuite{
	public FundTestSuite() {
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
		FundTestSuite testSuite = new FundTestSuite();
		testSuite.addTestSuite(FundBOTest.class);
		testSuite.addTestSuite(FundActionTest.class);
		testSuite.addTestSuite(FundBusinessServiceTest.class);
		testSuite.addTestSuite(FundPersistenceTest.class);
		return testSuite;
	}
}
