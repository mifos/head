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

package org.mifos.framework.persistence;

import static org.mifos.framework.util.helpers.DatabaseSetup.executeScript;

import java.io.IOException;
import java.sql.SQLException;

import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DatabaseMigratorIntegrationTest {

    private static java.sql.Connection connection;
    private DatabaseMigrator databaseMigrator;

    @BeforeClass
    public void berforeClass() throws Exception {
        StaticHibernateUtil.initialize();
        StaticHibernateUtil.getSessionFactory().openSession(connection);
        connection.setAutoCommit(false);
    }

    @AfterClass
    public void afterClass() throws Exception {
        StaticHibernateUtil.flushAndCloseSession();
    }

    /**
     * Demonstrate the simplest possible non-sequential database upgrade works. For example, upgrading a schema from
     * "1274760000" to "1274761395".
     */
    @Test
    public void testHappyPath() throws Exception {
        loadNonSeqDatabaseSchema();

        databaseMigrator.checkUnAppliedUpgradesAndUpgrade();


        // check if database is upgraded to 1274761395

    }

    private void loadNonSeqDatabaseSchema() throws SQLException, IOException {
        executeScript("mifosdroptables.sql", connection);
        executeScript("latest-schema-nonseq-checkpoint", connection);

    }

    @Test
    public void testMergedUpgrade() throws Exception {
    }
}
