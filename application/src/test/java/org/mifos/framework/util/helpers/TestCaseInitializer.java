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

package org.mifos.framework.util.helpers;

import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.config.AccountingRules;
import org.mifos.config.Localization;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.service.test.TestMode;

/**
 * Many tests initialize themselves via this class.
 * 
 * However, the fact that it is a static block, and initializes more than it may
 * need to for a given test, means that it might be desirable to call
 * {@link DatabaseSetup} directly in some cases, or to avoid everything here in
 * others (that is, those tests written to not need the database).
 */
public class TestCaseInitializer {

    private static Boolean initialized = false;

    public TestCaseInitializer() {
        // do nothing
    }

    public synchronized void initialize() throws SystemException, ApplicationException {
        if (initialized == false) {
            initialized = true;
            /*
             * Make sure TestingService is aware that we're running integration
             * tests. This is for integration test cases that use a database,
             * but could also apply to other "black box" tests.
             */
            new StandardTestingService().setTestMode(TestMode.INTEGRATION);

            MifosLogManager.configureLogging();
            DatabaseSetup.initializeHibernate();
            // add this because it is added to Application Initializer
            Localization.getInstance().init();
            /*
             * initializeSpring needs to come before AuditConfiguration.init in
             * order for MasterDataEntity data to be loaded.
             */

            /*
             * shouldn't we have other initialization from
             * ApplicationInitializer in here ?
             */

            Money.setDefaultCurrency(AccountingRules.getMifosCurrency(new ConfigurationPersistence()));

            TestUtils.initializeSpring();
            // Spring must be initialized before FinancialInitializer
            FinancialInitializer.initialize();
            ActivityMapper.getInstance().init();
            AuthorizationManager.getInstance().init();
            HierarchyManager.getInstance().init();

            MifosConfiguration.getInstance().init();
            AuditConfigurtion.init(Localization.getInstance().getMainLocale());
        }
    }
}
