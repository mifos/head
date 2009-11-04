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

package org.mifos.application.customer.client.struts.actionforms;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.config.ClientRules;
import org.mifos.framework.components.configuration.business.ConfigurationKeyValueInteger;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.components.logger.MifosLogManager;
import org.testng.annotations.Test;

@Test(groups = { "unit", "fastTestsSuite" }, dependsOnGroups = { "productMixTestSuite" })
public class ClientCustActionFormTest extends TestCase {

    public ClientCustActionFormTest() {
        MifosLogManager.configureLogging();
    }

    private ClientCustActionForm form;
    private ActionErrors errors;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        form = new ClientCustActionForm();
        errors = new ActionErrors();

        ConfigurationKeyValueInteger groupCanApplyLoansKey = new ConfigurationKeyValueInteger(
                ClientRules.GroupCanApplyLoansKey, 1);
        ConfigurationKeyValueInteger clientCanExistOutsideGroupKey = new ConfigurationKeyValueInteger(
                ClientRules.ClientCanExistOutsideGroupKey, 1);

        ConfigurationPersistence configPersistence = mock(ConfigurationPersistence.class);
        when(configPersistence.getConfigurationKeyValueInteger(ClientRules.GroupCanApplyLoansKey)).thenReturn(
                groupCanApplyLoansKey);
        when(configPersistence.getConfigurationKeyValueInteger(ClientRules.ClientCanExistOutsideGroupKey)).thenReturn(
                clientCanExistOutsideGroupKey);

        ClientRules.setConfigPersistence(configPersistence);
        ClientRules.init();
    }

    public void testGoodDate() throws Exception {
        form.setDateOfBirthDD("6");
        form.setDateOfBirthMM("12");
        form.setDateOfBirthYY("1950");
        form.validateDateOfBirth(null, errors);
        Assert.assertEquals(0, errors.size());
    }

    public void testFutureDate() throws Exception {
        form.setDateOfBirthDD("6");
        form.setDateOfBirthMM("12");
        form.setDateOfBirthYY("2108");
        form.validateDateOfBirth(null, errors);
        Assert.assertEquals(1, errors.size());
        ActionMessage message = (ActionMessage) errors.get().next();
        Assert.assertEquals(ClientConstants.FUTURE_DOB_EXCEPTION, message.getKey());
    }

    public void testInvalidDate() throws Exception {
        form.setDateOfBirthDD("2");
        form.setDateOfBirthMM("20");
        form.setDateOfBirthYY("1980");
        form.validateDateOfBirth(null, errors);
        Assert.assertEquals(1, errors.size());
        ActionMessage message = (ActionMessage) errors.get().next();
        Assert.assertEquals(ClientConstants.INVALID_DOB_EXCEPTION, message.getKey());

    }

    public void testStringDate() throws Exception {
        form.setDateOfBirthDD("2");
        form.setDateOfBirthMM("02");
        form.setDateOfBirthYY("1qw0");
        form.validateDateOfBirth(null, errors);
        Assert.assertEquals(1, errors.size());
        ActionMessage message = (ActionMessage) errors.get().next();
        Assert.assertEquals(ClientConstants.INVALID_DOB_EXCEPTION, message.getKey());

    }

    public void testLessThanMinimumAge() throws Exception {
        if(ClientRules.isAgeCheckDisabled()){
            ClientRules.setMinimumAgeForNewClient(18);
            ClientRules.setMaximumAgeForNewClient(60);
            ClientRules.setAgeCheckDisabled(false);
        }
        form.setDateOfBirthDD("2");
        form.setDateOfBirthMM("2");
        form.setDateOfBirthYY("1999");
        form.validateDateOfBirth(null, errors);
        Assert.assertEquals(1, errors.size());
        ActionMessage message = (ActionMessage) errors.get().next();
        Assert.assertEquals(ClientConstants.INVALID_AGE, message.getKey());

    }

    public void testMoreThanMaximumAge() throws Exception {
        if(ClientRules.isAgeCheckDisabled()){
            ClientRules.setMinimumAgeForNewClient(18);
            ClientRules.setMaximumAgeForNewClient(60);
            ClientRules.setAgeCheckDisabled(false);
        }
        form.setDateOfBirthDD("2");
        form.setDateOfBirthMM("2");
        form.setDateOfBirthYY("1940");
        form.validateDateOfBirth(null, errors);
        Assert.assertEquals(1, errors.size());
        ActionMessage message = (ActionMessage) errors.get().next();
        Assert.assertEquals(ClientConstants.INVALID_AGE, message.getKey());

    }

    @Override
    public void tearDown() {
        form = null;
        errors = null;
    }

}
