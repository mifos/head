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

package org.mifos.framework.util.helpers;

import java.util.List;

import org.mifos.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.config.AccountingRules;
import org.mifos.config.Localization;
import org.mifos.config.business.MifosConfiguration;
import org.mifos.config.persistence.ApplicationConfigurationPersistence;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.framework.components.audit.util.helpers.AuditConfiguration;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.authorization.AuthorizationManager;
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

    public void initialize() throws Exception {
        if (!initialized) {
            initializeDB();
            initialized = true;
        }
    }

    private void initializeDB() throws Exception{
        StaticHibernateUtil.initialize();
        List<SupportedLocalesEntity> supportedLocales = new ApplicationConfigurationPersistence().getSupportedLocale();
        Localization.getInstance().init(supportedLocales);

        Money.setDefaultCurrency(AccountingRules.getMifosCurrency(new ConfigurationPersistence()));

        FinancialInitializer.initialize();
        ActivityMapper.getInstance().init();
        AuthorizationManager.getInstance().init();
        HierarchyManager.getInstance().init();

        MifosConfiguration.getInstance().init();
        AuditConfiguration.init(Localization.getInstance().getMainLocale());
        AccountingRules.init();
        StaticHibernateUtil.commitTransaction();
    }
}
