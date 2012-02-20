/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.config.persistence;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class ConfigurationPersistenceIntegrationTest extends MifosIntegrationTestCase {

    @After
    public void tearDown() throws Exception {
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testGetCurrencyForCurrencyId() throws Exception {
        ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
        MifosCurrency currency = configurationPersistence.getPersistentObject(MifosCurrency.class,
                Short.valueOf("2"));
        Assert.assertNotNull(currency);
       Assert.assertEquals("Indian Rupee", currency.getCurrencyName());
    }

    @Test
    public void testCheckIndividualMonitoringKeyExists() throws Exception {
        Assert.assertNotNull(new ConfigurationPersistence()
                .getConfigurationKeyValue(LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED));
    }

    @Test
    public void testIfGroupLoanWithIndividualMonitoringIsEnabled() throws Exception {
        Assert.assertFalse(new ConfigurationPersistence().isGlimEnabled());
    }

}
