/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.config;

import junit.framework.Assert;

import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.config.business.ConfigurationKeyValueInteger;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.framework.util.helpers.Constants;
import org.testng.annotations.Test;

@Test(groups={"integration", "configTestSuite"})
public class ClientRulesIntegrationTest extends MifosIntegrationTestCase {

    public ClientRulesIntegrationTest() throws Exception {
        super();
    }

    @Test
    public void testGetGroupCanApplyLoans() throws Exception {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        ConfigurationPersistence configPersistence = new ConfigurationPersistence();
        ConfigurationKeyValueInteger savedDBValue = null;
        savedDBValue = configPersistence.getConfigurationKeyValueInteger(ClientRules.GroupCanApplyLoansKey);
        Boolean savedValue = ClientRules.getGroupCanApplyLoans();
        configMgr.setProperty(ClientRules.ClientRulesGroupCanApplyLoans, false);
        configPersistence.updateConfigurationKeyValueInteger(ClientRules.GroupCanApplyLoansKey, Constants.NO);
        ClientRules.refresh();
        // set db value to false, too
        Assert.assertFalse(ClientRules.getGroupCanApplyLoans());
        // now the return value from accounting rules class has to be what is in
        // the database (0)
        ClientRules.refresh();
        Assert.assertFalse(ClientRules.getGroupCanApplyLoans());
        // set the saved value back for following tests
        configPersistence
                .updateConfigurationKeyValueInteger(ClientRules.GroupCanApplyLoansKey, savedDBValue.getValue());
        configMgr.setProperty(ClientRules.ClientRulesGroupCanApplyLoans, savedValue);
        ClientRules.refresh();
    }

    @Test
    public void testClientCanExistOutsideGroup() throws Exception {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        ConfigurationPersistence configPersistence = new ConfigurationPersistence();
        ConfigurationKeyValueInteger savedDBValue = null;
        savedDBValue = configPersistence.getConfigurationKeyValueInteger(ClientRules.ClientCanExistOutsideGroupKey);
        Boolean savedValue = ClientRules.getClientCanExistOutsideGroup();
        configMgr.setProperty(ClientRules.ClientRulesClientCanExistOutsideGroup, false);
        configPersistence.updateConfigurationKeyValueInteger(ClientRules.ClientCanExistOutsideGroupKey, Constants.NO);
        ClientRules.refresh();
        // set db value to false, too
        Assert.assertFalse(ClientRules.getClientCanExistOutsideGroup());
        // now the return value from accounting rules class has to be what is in
        // the database (0)
        ClientRules.refresh();
        Assert.assertFalse(ClientRules.getClientCanExistOutsideGroup());
        // set the saved value back for following tests
        configPersistence.updateConfigurationKeyValueInteger(ClientRules.ClientCanExistOutsideGroupKey, savedDBValue
                .getValue());
        configMgr.setProperty(ClientRules.ClientRulesClientCanExistOutsideGroup, savedValue);
        ClientRules.refresh();
    }

    /**
     * A name sequence is the order in which client names are displayed.
     * Example: first name, then middle name, then last name.
     * 
     * @see ClientRules#getNameSequence()
     * @see ClientRules#isValidNameSequence(String[])
     */
    @Test
    public void testValidNameSequence() {
       Assert.assertTrue(ClientRules.isValidNameSequence(ClientRules.getNameSequence()));
    }

    /**
     * A name sequence is the order in which client names are displayed.
     * Example: first name, then middle name, then last name.
     * 
     * @see ClientRules#getNameSequence()
     * @see ClientRules#isValidNameSequence(String[])
     */
    @Test
    public void testInvalidNameSequence() {
        String[] invalidSequence = { "" };
        Assert.assertFalse(ClientRules.isValidNameSequence(invalidSequence));
        String[] invalidSequence2 = { "invalid", "", "name", "sequence" };
        Assert.assertFalse(ClientRules.isValidNameSequence(invalidSequence2));
    }
}
