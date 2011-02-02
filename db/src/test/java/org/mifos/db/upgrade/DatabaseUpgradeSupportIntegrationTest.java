/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.db.upgrade;

import liquibase.exception.LiquibaseException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-dbContext.xml", "/META-INF/spring/DbUpgradeContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
public class DatabaseUpgradeSupportIntegrationTest {

    @Autowired
    DatabaseUpgradeSupport databaseUpgradeSupport;

    @Test
    public void testSpringWiringForDbUpgrade() throws SQLException, LiquibaseException {
        DbUpgradeValidationResult validationResult = databaseUpgradeSupport.validate();
        Assert.assertNotNull(validationResult);
        String unAppliedChangeSets = validationResult.getUnAppliedChangeSets();
        Assert.assertNotNull(unAppliedChangeSets);
        Assert.assertEquals("\nList of unapplied upgrades:\n".equals(unAppliedChangeSets), validationResult.allUpgradesApplied());
    }

}
