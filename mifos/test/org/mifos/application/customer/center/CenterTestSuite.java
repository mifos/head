package org.mifos.application.customer.center;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.mifos.application.fees.TestFees;

public class CenterTestSuite extends TestSuite{
	public CenterTestSuite(String arg0) {
		super(arg0);
		this.addTest(new TestCenter("Center"));
	}

	public CenterTestSuite() {
		super();
		this.addTest(new TestCenter("Center"));
	}

	
	public static void main(String[] args){
		TestSuite suite = new TestSuite();
		suite.addTest(new TestCenter("Center"));
		suite.run(new TestResult());
	}
}
