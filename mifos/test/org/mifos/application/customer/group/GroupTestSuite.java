package org.mifos.application.customer.group;


import junit.framework.TestResult;
import junit.framework.TestSuite;



public class GroupTestSuite extends TestSuite{
	
	public GroupTestSuite(String arg0) {
		super(arg0);
		this.addTest(new TestGroup("Group"));
	}

	public GroupTestSuite() {
		super();
		this.addTest(new TestGroup("Group"));
	}

	
	public static void main(String[] args){
		TestSuite suite = new TestSuite();
		suite.addTest(new TestGroup("Group"));
		suite.run(new TestResult());
	}
}
