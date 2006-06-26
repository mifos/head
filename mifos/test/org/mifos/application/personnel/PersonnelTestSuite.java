package org.mifos.application.personnel;

import junit.framework.TestResult;
import junit.framework.TestSuite;

public class PersonnelTestSuite extends TestSuite{

	
	
	public PersonnelTestSuite(String arg0) {
		super(arg0);
		this.addTest(new TestPersonnel("Personnel"));
	}

	public PersonnelTestSuite() {
		super();
		this.addTest(new TestPersonnel("Personnel"));
	}

	
	public static void main(String[] args){
		TestSuite suite = new TestSuite();
		suite.addTest(new TestPersonnel("Personnel"));
		suite.run(new TestResult());
	}

}
