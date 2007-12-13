/*
 * Copyright (c) 2005-2007 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.config;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.FilePaths;
import junit.framework.JUnit4TestAdapter;
import org.mifos.config.ClientRules;
import org.mifos.framework.components.configuration.business.ConfigurationKeyValueInteger;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;


public class TestClientRules {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestClientRules.class);
	}
	
	@BeforeClass
	public static void init() throws Exception {
		MifosLogManager.configure(FilePaths.LOGFILE);
		DatabaseSetup.initializeHibernate();
		
	}
	
	@Test 
	public void testGetGroupCanApplyLoans() throws Exception {	
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		ConfigurationPersistence configPersistence = new ConfigurationPersistence();
		ConfigurationKeyValueInteger savedDBValue = null;
		savedDBValue = configPersistence.getConfigurationKeyValueInteger(ClientRules.GroupCanApplyLoansKey);
		Boolean savedValue = ClientRules.getGroupCanApplyLoans();
		short non_exists = Constants.NO;
		configMgr.setProperty(ClientRules.ClientRulesGroupCanApplyLoans, non_exists);
		configPersistence.updateConfigurationKeyValueInteger(ClientRules.GroupCanApplyLoansKey, non_exists);
		ClientRules.refresh();
		// set db value to false, too
		assertTrue(ClientRules.getGroupCanApplyLoans() == false);
		// clear the property from the config file
		configMgr.clearProperty(ClientRules.ClientRulesGroupCanApplyLoans);
		// now the return value from accounting rules class has to be what is in the database (0)
		ClientRules.refresh();
		assertTrue(ClientRules.getGroupCanApplyLoans() == false);
		//	set the saved value back for following tests
		configPersistence.updateConfigurationKeyValueInteger(ClientRules.GroupCanApplyLoansKey, savedDBValue.getValue());
		if (savedValue)
			configMgr.addProperty(ClientRules.ClientRulesGroupCanApplyLoans, Constants.YES );
		else
			configMgr.addProperty(ClientRules.ClientRulesGroupCanApplyLoans, Constants.NO );
		ClientRules.refresh();
	}
	

	@Test 
	public void testClientCanExistOutsideGroup() throws Exception {	
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		ConfigurationPersistence configPersistence = new ConfigurationPersistence();
		ConfigurationKeyValueInteger savedDBValue = null;
		savedDBValue = configPersistence.getConfigurationKeyValueInteger(ClientRules.ClientCanExistOutsideGroupKey);
		Boolean savedValue = ClientRules.getClientCanExistOutsideGroup();
		short non_exists = Constants.NO;
		configMgr.setProperty(ClientRules.ClientRulesClientCanExistOutsideGroup, non_exists);
		configPersistence.updateConfigurationKeyValueInteger(ClientRules.ClientCanExistOutsideGroupKey, non_exists);
		ClientRules.refresh();
		// set db value to false, too
		assertTrue(ClientRules.getClientCanExistOutsideGroup() == false);
		// clear the property from the config file
		configMgr.clearProperty(ClientRules.ClientRulesClientCanExistOutsideGroup);
		// now the return value from accounting rules class has to be what is in the database (0)
		ClientRules.refresh();
		assertTrue(ClientRules.getClientCanExistOutsideGroup() == false);
		//	set the saved value back for following tests
		configPersistence.updateConfigurationKeyValueInteger(ClientRules.ClientCanExistOutsideGroupKey, savedDBValue.getValue());
		if (savedValue)
			configMgr.addProperty(ClientRules.ClientRulesClientCanExistOutsideGroup, Constants.YES );
		else
			configMgr.addProperty(ClientRules.ClientRulesClientCanExistOutsideGroup, Constants.NO );
		ClientRules.refresh();
		
		
	}
	
	@Test 
	public void testGetCenterHierarchyExists() {
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		Boolean savedValue = ClientRules.getCenterHierarchyExists();
		short non_exists = Constants.NO;
		configMgr.setProperty(ClientRules.ClientRulesCenterHierarchyExists, non_exists);
		ClientRules.refresh();
		assertTrue(false == ClientRules.getCenterHierarchyExists());
		// clear the property from the config file
		configMgr.clearProperty(ClientRules.ClientRulesCenterHierarchyExists);
		ClientRules.refresh();
		// now the return value from accounting rules class has to be the default value = 1
		assertTrue(true == ClientRules.getCenterHierarchyExists());
		//	set the saved value back for following tests
		if (savedValue)
			configMgr.addProperty(ClientRules.ClientRulesCenterHierarchyExists, Constants.YES );
		else
			configMgr.addProperty(ClientRules.ClientRulesCenterHierarchyExists, Constants.NO );
	}
	
	/**
	 * A name sequence is the order in which client names are displayed.
	 * Example: first name, then middle name, then last name.
	 * 
	 * @see {@link ClientRules#getNameSequence()},
	 *      {@link ClientRules#isValidNameSequence(String[])}
	 */
	@Test
	public void testValidNameSequence() {
		assertTrue(ClientRules.isValidNameSequence(ClientRules
				.getNameSequence()));
	}

	/**
	 * A name sequence is the order in which client names are displayed.
	 * Example: first name, then middle name, then last name.
	 * 
	 * @see {@link ClientRules#getNameSequence()},
	 *      {@link ClientRules#isValidNameSequence(String[])}
	 */
	@Test
	public void testInvalidNameSequence() {
		String[] invalidSequence = { "" };
		assertFalse(ClientRules.isValidNameSequence(invalidSequence));
		String[] invalidSequence2 = { "invalid", "", "name", "sequence" };
		assertFalse(ClientRules.isValidNameSequence(invalidSequence2));
	}
}

