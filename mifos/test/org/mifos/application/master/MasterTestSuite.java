package org.mifos.application.master;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.master.business.service.TestMasterBusinessService;
import org.mifos.application.master.persistence.TestMasterPersistence;
import org.mifos.application.master.persistence.service.TestMasterPersistenceService;

public class MasterTestSuite extends TestSuite {

public MasterTestSuite() throws Exception {
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
	
	public static Test suite()throws Exception
	{
		MasterTestSuite testSuite = new MasterTestSuite();
		testSuite.addTestSuite(TestMasterPersistence.class);
		testSuite.addTestSuite(TestMasterPersistenceService.class);
		testSuite.addTestSuite(TestMasterBusinessService.class);
		return testSuite;
		
	}

}
