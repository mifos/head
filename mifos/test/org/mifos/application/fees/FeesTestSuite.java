package org.mifos.application.fees;

import org.mifos.application.fees.persistence.TestFeePersistence;
import org.mifos.application.fees.persistence.service.TestFeesPersistenceService;

import junit.framework.TestResult;
import junit.framework.TestSuite;

public class FeesTestSuite extends TestSuite{

	public FeesTestSuite(String arg0) {
		super(arg0);
		this.addTest(new TestFees("Fees"));
	}

	public FeesTestSuite() {
		super();
		this.addTest(new TestFees("Fees"));
	}

	
	public static void main(String[] args){
		TestSuite suite = new TestSuite();
		suite.addTest(new TestFees("Fees"));
		suite.addTestSuite(TestFeePersistence.class);
		suite.addTestSuite(TestFeesPersistenceService.class);
		suite.run(new TestResult());
	}
}
