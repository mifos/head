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

package org.mifos.framework.components.configuration.persistence;

import junit.framework.Assert;

import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class ConfigurationPersistenceIntegrationTest extends MifosIntegrationTestCase {

    public ConfigurationPersistenceIntegrationTest() throws Exception {
        super();
    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetCurrencyForCurrencyId() throws Exception {
        ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
        MifosCurrency currency = (MifosCurrency) configurationPersistence.getPersistentObject(MifosCurrency.class,
                Short.valueOf("2"));
        Assert.assertNotNull(currency);
       Assert.assertEquals("Indian Rupee", currency.getCurrencyName());
    }

    public void testCheckIndividualMonitoringKeyExists() throws Exception {
        Assert.assertNotNull(new ConfigurationPersistence()
                .getConfigurationKeyValueInteger(LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED));
    }

    public void testIfGroupLoanWithIndividualMonitoringIsEnabled() throws Exception {
        Assert.assertFalse(new ConfigurationPersistence().isGlimEnabled());
    }

}
