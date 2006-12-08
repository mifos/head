package org.mifos.application.reports;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.reports.persistence.service.TestReportsPersistenceService;
import org.mifos.application.reports.struts.action.TestReportsAction;

public class ReportsTestSuite extends TestSuite{
	
	public ReportsTestSuite() throws Exception {
		super();
	}
	
	public static void main(String[] args){
		try{
			Test testSuite = suite();
			TestRunner.run (testSuite);	
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static Test suite() throws Exception {
		TestSuite testSuite = new ReportsTestSuite();		
		testSuite.addTestSuite(TestReportsPersistenceService.class);
		testSuite.addTestSuite(TestReportsAction.class);
		return testSuite;
	}
}
