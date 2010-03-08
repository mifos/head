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

package org.mifos.framework.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.Assert;

import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class CompositeUpgradeIntegrationTest extends MifosIntegrationTestCase {

    public CompositeUpgradeIntegrationTest() throws Exception {
        super();
    }

    private Connection connection;

    @Override
    public void setUp() {
        connection = StaticHibernateUtil.getSessionTL().connection();
    }

    @Override
    public void tearDown() throws Exception {
        databaseWithVersion();
    }

    public void testBasics() throws Exception {
        databaseWithVersion();
        DummyUpgrade upgradeOne = new DummyUpgrade(DatabaseVersionPersistence.APPLICATION_VERSION+1);
        DummyUpgrade upgradeTwo = new DummyUpgrade(DatabaseVersionPersistence.APPLICATION_VERSION+1);
        Upgrade composite = new CompositeUpgrade(upgradeOne, upgradeTwo);
        Assert.assertEquals(DatabaseVersionPersistence.APPLICATION_VERSION+1, composite.higherVersion());
        composite.upgrade(connection);
        Assert.assertEquals("upgrade to "+(DatabaseVersionPersistence.APPLICATION_VERSION+1)+"\n", upgradeOne.getLog());
        Assert.assertEquals("upgrade to "+(DatabaseVersionPersistence.APPLICATION_VERSION+1)+"\n", upgradeTwo.getLog());
        Assert.assertEquals((DatabaseVersionPersistence.APPLICATION_VERSION+1), new DatabaseVersionPersistence(connection).read());
    }

    public void testMismatch() throws Exception {
        DummyUpgrade upgradeOne = new DummyUpgrade(111);
        DummyUpgrade upgradeTwo = new DummyUpgrade(112);
        try {
            new CompositeUpgrade(upgradeOne, upgradeTwo);
            Assert.fail();
        } catch (RuntimeException e) {
           Assert.assertEquals("got upgrades to 111 and 112 but expected matching versions", e.getMessage());
        }
    }

    public void testEmpty() throws Exception {
        try {
            new CompositeUpgrade();
            Assert.fail();
        } catch (RuntimeException e) {
           Assert.assertEquals("must specify at least one upgrade", e.getMessage());
        }
    }

    StringBuilder log;

    public void testOrder() throws Exception {
        log = new StringBuilder();
        Upgrade composite = new CompositeUpgrade(new MyUpgrade("first"), new MyUpgrade("second"),
                new MyUpgrade("third"));
        composite.upgrade(connection);
       Assert.assertEquals("upgrading first\n" + "upgrading second\n" + "upgrading third\n", log.toString());
       Assert.assertEquals(DatabaseVersionPersistence.APPLICATION_VERSION, new DatabaseVersionPersistence(connection).read());
    }


    private void databaseWithVersion() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("truncate table DATABASE_VERSION");
        statement.execute("insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(" + DatabaseVersionPersistence.APPLICATION_VERSION + ")");
    }

    class MyUpgrade extends Upgrade {
        private final String which;
        MyUpgrade(String which) {
            super(DatabaseVersionPersistence.APPLICATION_VERSION);
            this.which = which;
        }

        @Override
        public void upgrade(Connection connection)
                throws IOException, SQLException {
            log.append("upgrading " + which + "\n");
        }
    }
}
