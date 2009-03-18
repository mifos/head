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

import org.junit.After;
import org.junit.Before;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.DatabaseSetup;

/**
 * Marker superclass for integration tests that can be run using an in-memory
 * database like Mayfly.
 */
public class MifosInMemoryIntegrationTest {
    protected TestDatabase database;

    @Before
    public void setUp() {
        database = TestDatabase.makeStandard();
        database.installInThreadLocal();
    }

    @After
    public void tearDown() {
        HibernateUtil.resetDatabase();
    }

    public MifosInMemoryIntegrationTest() {
        MifosLogManager.configureLogging();
        DatabaseSetup.initializeHibernate(true);
    }
}
