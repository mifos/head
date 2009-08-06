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

package org.mifos.framework;

import junit.framework.TestCase;

import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.DatabaseSetup;

/**
 * Marker superclass for integration tests that can be run using an in-memory
 * database like Mayfly.
 */
public class MifosInMemoryIntegrationTestCase extends TestCase {
    protected TestDatabase database;

    public void setUp() {
        database = TestDatabase.makeStandard();
        database.installInThreadLocal();
    }

    public void tearDown() {
        StaticHibernateUtil.resetDatabase();
    }

    public MifosInMemoryIntegrationTestCase() {
        MifosLogManager.configureLogging();
        DatabaseSetup.initializeHibernate(true);
    }
}
