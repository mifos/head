package org.mifos.framework.components.fieldConfiguration;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.framework.MifosTestSuite;
import org.mifos.framework.components.fieldConfiguration.business.TestFieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.persistence.TestFieldConfigurationPersistence;
import org.mifos.framework.components.fieldConfiguration.persistence.service.TestFieldConfigurationPersistenceService;
import org.mifos.framework.components.fieldConfiguration.util.helpers.TestFieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.TestFieldConfigurationHelper;


public class FieldConfigurationTestSuite extends MifosTestSuite{

	public FieldConfigurationTestSuite() {
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
		TestSuite testSuite = new FieldConfigurationTestSuite();
		testSuite.addTestSuite(TestFieldConfigurationEntity.class);
		testSuite.addTestSuite(TestFieldConfigurationPersistence.class);
		testSuite.addTestSuite(TestFieldConfigurationPersistenceService.class);
		testSuite.addTestSuite(TestFieldConfigImplementer.class);
		testSuite.addTestSuite(TestFieldConfigurationHelper.class);
		return testSuite;
	}
}
