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

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sourceforge.mayfly.Database;

import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class CompositeUpgradeTest extends TestCase {

    public void testBasics() throws Exception {
        DummyUpgrade upgradeOne = new DummyUpgrade(53);
        DummyUpgrade upgradeTwo = new DummyUpgrade(53);
        Upgrade composite = new CompositeUpgrade(upgradeOne, upgradeTwo);
       Assert.assertEquals(53, composite.higherVersion());
        Connection data = simpleDatabase(52);
        composite.upgrade(data);
       Assert.assertEquals("upgrade to 53\n", upgradeOne.getLog());
       Assert.assertEquals("upgrade to 53\n", upgradeTwo.getLog());

       Assert.assertEquals(53, new DatabaseVersionPersistence(data).read());
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

        Connection data = simpleDatabase(52);
        composite.upgrade(data);

       Assert.assertEquals("upgrading first\n" + "upgrading second\n" + "upgrading third\n", log.toString());

       Assert.assertEquals(52, new DatabaseVersionPersistence(data).read());
    }

    class MyUpgrade extends Upgrade {

        private final String which;

        MyUpgrade(String which) {
            super(53);
            this.which = which;
        }

        @Override
        public void upgrade(Connection connection)
                throws IOException, SQLException {
            log.append("upgrading " + which + "\n");
        }

    }

    private Connection simpleDatabase(int version) {
        Database database = DummyUpgrade.databaseWithVersionTable(version);
        return database.openConnection();
    }
}
