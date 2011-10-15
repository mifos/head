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

package org.mifos.framework.util.helpers;

import org.hibernate.SessionFactory;
import org.mifos.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.config.AccountingRules;
import org.mifos.config.Localization;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.db.upgrade.DatabaseUpgradeSupport;
import org.mifos.framework.components.audit.util.helpers.AuditConfiguration;
import org.mifos.framework.hibernate.helper.AuditInterceptorFactory;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.authorization.HierarchyManager;
import org.mifos.security.util.ActivityMapper;

/**
 * Many tests initialize themselves via this class.
 *
 * However, the fact that it is a static block, and initializes more than it may
 * need to for a given test, means that it might be desirable to call
 * {@link DatabaseSetup} directly in some cases, or to avoid everything here in
 * others (that is, those tests written to not need the database).
 */
public class TestCaseInitializer {

    private static boolean initialized = false;

    @SuppressWarnings("deprecation")
    private DatabaseUpgradeSupport databaseUpgradeSupport = ApplicationContextProvider.getBean(DatabaseUpgradeSupport.class);

    public void initialize(SessionFactory sessionFactory) throws Exception {
        if (!initialized) {
            databaseUpgradeSupport.expansion();
            initializeDB(sessionFactory);
            initialized = true;
        }
    }

    private void initializeDB(SessionFactory sessionFactory) throws Exception{
        StaticHibernateUtil.initialize(new AuditInterceptorFactory(), sessionFactory);

        Money.setDefaultCurrency(AccountingRules.getMifosCurrency(new ConfigurationPersistence()));

        FinancialInitializer.initialize();
        ActivityMapper.getInstance().init();
        HierarchyManager.getInstance().init();

        AuditConfiguration.init(Localization.getInstance().getConfiguredLocale());
        AccountingRules.init();
        StaticHibernateUtil.commitTransaction();
    }
}
