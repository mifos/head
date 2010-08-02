/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import static org.junit.matchers.JUnitMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.Localization;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.customers.center.struts.action.OfficeHierarchyDto;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeDetailsDto;
import org.mifos.customers.office.business.OfficeLevelEntity;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.dto.domain.OfficeDto;
import org.mifos.dto.domain.OfficeLevelDto;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/messageSourceBean.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml"})
public class ApplicationConfigurationDaoHibernateIntegrationTest {

    // class under test
    @Autowired
    private ApplicationConfigurationDao applicationConfigurationDao;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static MifosCurrency oldDefaultCurrency;

    @BeforeClass
    public static void initialiseHibernateUtil() {

        Locale locale = Localization.getInstance().getMainLocale();
        AuditConfigurtion.init(locale);

        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        new StandardTestingService().setTestMode(TestMode.INTEGRATION);
        DatabaseSetup.initializeHibernate();
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
    }

    @Before
    public void cleanDatabaseTables() {
        databaseCleaner.clean();

    }

    @Test
    public void shouldDoX() {
        LookUpEntity entity =  applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.CLIENT);

        assertThat(entity, is(notNullValue()));
    }
}