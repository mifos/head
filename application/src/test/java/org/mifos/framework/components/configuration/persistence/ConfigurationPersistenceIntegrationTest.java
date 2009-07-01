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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.FrameworkRuntimeException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.ExceptionConstants;

public class ConfigurationPersistenceIntegrationTest extends MifosIntegrationTest {

    public ConfigurationPersistenceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private ConfigurationPersistence configurationPersistence;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        configurationPersistence = new ConfigurationPersistence();
    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetDefaultCurrency() throws Exception {
        MifosCurrency defaultCurrency = configurationPersistence.getDefaultCurrency();
        assertEquals("Indian Rupee", defaultCurrency.getCurrencyName());
    }

    public void testNoDefaultCurrency() throws Exception {
        try {
            configurationPersistence.defaultCurrencyFromList(Collections.EMPTY_LIST);
            fail();
        } catch (FrameworkRuntimeException e) {
            assertEquals("No Default Currency Specified", e.getMessage());
            e.setValues(null);
            assertNull(e.getValues());
            assertEquals(ExceptionConstants.FRAMEWORKRUNTIMEEXCEPTION, e.getKey());
        }
    }

    public void testAmbiguousDefaultCurrency() throws Exception {
        try {
            List currencies = new ArrayList();
            currencies.add(new MifosCurrency((short) 8, "Franc", "Fr", MifosCurrency.CEILING_MODE, 0.0f, (short) 1,
                    (short) 0, "FRC"));
            currencies.add(new MifosCurrency((short) 9, "Euro", "\u20ac", MifosCurrency.CEILING_MODE, 0.0f, (short) 1,
                    (short) 0, "ERO"));

            configurationPersistence.defaultCurrencyFromList(currencies);
            fail();
        } catch (FrameworkRuntimeException e) {
            assertEquals("Both Franc and Euro are marked as default currencies", e.getMessage());
        }
    }

    public void testGetCurrencyForCurrencyId() throws Exception {
        ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
        MifosCurrency currency = (MifosCurrency) configurationPersistence.getPersistentObject(MifosCurrency.class,
                Short.valueOf("2"));
        assertNotNull(currency);
        assertEquals("Indian Rupee", currency.getCurrencyName());
    }

    public void testCheckIndividualMonitoringKeyExists() throws Exception {
        assertNotNull(new ConfigurationPersistence()
                .getConfigurationKeyValueInteger(LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED));
    }

    public void testIfGroupLoanWithIndividualMonitoringIsEnabled() throws Exception {
        assertFalse(new ConfigurationPersistence().isGlimEnabled());
    }

}
