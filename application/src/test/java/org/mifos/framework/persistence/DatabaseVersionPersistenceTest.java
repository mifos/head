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

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.CharacterCodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junitx.framework.ObjectAssert;
import junitx.framework.StringAssert;
import net.sourceforge.mayfly.Database;

import org.mifos.framework.components.logger.MifosLogManager;
import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class DatabaseVersionPersistenceTest extends TestCase {

    public void setUp() throws Exception {
        MifosLogManager.configureLogging();
    }

    public void testReadSuccess() throws Exception {
        Database database = new Database();
        database.execute("create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
        database.execute("insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(53)");
        new DatabaseVersionPersistence(database.openConnection()).read();
    }

    public void testReadTwoRows() throws Exception {
        Database database = new Database();
        database.execute("create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
        database.execute("insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(53)");
        database.execute("insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(54)");
        try {
            new DatabaseVersionPersistence(database.openConnection()).read();
            fail();
        } catch (RuntimeException e) {
            assertEquals("too many rows in DATABASE_VERSION", e.getMessage());
        }
    }

    public void testReadNoRows() throws Exception {
        Database database = new Database();
        database.execute("create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
        try {
            new DatabaseVersionPersistence(database.openConnection()).read();
            fail();
        } catch (RuntimeException e) {
            assertEquals("No row in DATABASE_VERSION", e.getMessage());
        }
    }

    public void testReadNoTable() throws Exception {
        /*
         * This is the case where the user has an old database (from before
         * version 100). They will need to upgrade to 100 manually.
         */
        Database database = new Database();
    try {    
        new DatabaseVersionPersistence(database.openConnection()).read();
        fail("SQLException was expected");
    } catch (SQLException e){}
    }

    public void testWrite() throws Exception {
        Database database = DummyUpgrade.databaseWithVersionTable(53);
        new DatabaseVersionPersistence(database.openConnection()).write(77);
        assertEquals(77, new DatabaseVersionPersistence(database.openConnection()).read());
    }

    public void testIsNotVersioned() throws Exception {
        Database database = TestDatabase.makeDatabase();
        DatabaseVersionPersistence persistence = new DatabaseVersionPersistence(database.openConnection());
        assertFalse(persistence.isVersioned());

        database.execute("create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
        database.execute("insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(43)");
        assertTrue(persistence.isVersioned());
    }

    public void testNoUpgrade() throws Exception {
        DatabaseVersionPersistence persistence = new DatabaseVersionPersistence(null);
        List<Upgrade> scripts = persistence.scripts(88, 88);
        assertEquals(0, scripts.size());
    }

    public void testUpgrade() throws Exception {
        DatabaseVersionPersistence persistence = sqlFor89And90();
        List<Upgrade> scripts = persistence.scripts(90, 88);
        assertEquals(2, scripts.size());
        assertEquals("upgrade_to_89.sql", ((SqlUpgrade) scripts.get(0)).sql().getPath());
        assertEquals("upgrade_to_90.sql", ((SqlUpgrade) scripts.get(1)).sql().getPath());
    }

    private DatabaseVersionPersistence sqlFor89And90() {
        return new DatabaseVersionPersistence(null) {
            @Override
            URL getSqlResourceLocation(String name) {
                if ("upgrade_to_89.sql".equals(name) || "upgrade_to_90.sql".equals(name)) {
                    try {
                        return new URL("file:" + name);
                    } catch (MalformedURLException e) {
                        throw (AssertionFailedError) new AssertionFailedError().initCause(e);
                    }
                }
                throw new AssertionFailedError("got unexpected " + name);
            }
        };
    }

    public void testDetectDowngrade() throws Exception {
        DatabaseVersionPersistence persistence = new DatabaseVersionPersistence(null);
        try {
            persistence.scripts(87, 88);
            fail();
        } catch (UnsupportedOperationException e) {
            assertEquals("your database needs to be downgraded from 88 to 87", e.getMessage());
        }
    }

    public void testReadEmpty() throws Exception {
        String[] sqlStatements = SqlUpgrade.readFile(new ByteArrayInputStream(new byte[0]));
        assertEquals(0, sqlStatements.length);
    }

    public void testBlankLines() throws Exception {
        checkSplit("command\n\n\n\n\n\n\n", "\ncommand");
        checkSplit("command;\n", "\ncommand");
        checkSplit("command;\n\n\n\n\n\n\n", "\ncommand");
    }

    public void testSlashStarComments() throws Exception {
        checkSplit("/* foo; bar */", "\n/* foo; bar */");

        /*
         * Trying to fix this case seems to just descend even further down the
         * path of the half-assed lexer.
         */
        checkSplit("/* foo;\n bar */", "\n/* foo", "\nbar */");
    }

    public void testComments() throws Exception {
        /*
         * Many of the details here, like what comments get swallowed and
         * placement of whitespace, aren't very important. It just seems better
         * to have tests so we know what the code is doing.
         */
        checkSplit("-- ignore me");
        checkSplit("// ignore me");
        checkSplit(" // ignore me", "\n// ignore me");
        checkSplit(" -- ignore me", "\n-- ignore me\n");
        checkSplit("insert into foo(x) values('x-y')", "\ninsert into foo(x) values('x-y')");
        checkSplit("insert into foo(x) values('x--y')", "\ninsert into foo(x) values('x--y')\n");
    }

    public void testSplitIntoTwo() throws Exception {
        checkSplit("foo;\nbar\n", "\nfoo", "\nbar");
        checkSplit("foo;\nbar;\n", "\nfoo", "\nbar");
    }

    private void checkSplit(String sql, String... expected) throws UnsupportedEncodingException {
        String[] statements = SqlUpgrade.readFile(new ByteArrayInputStream(sql.getBytes("UTF-8")));
        assertEquals(expected.length, statements.length);
        for (int i = 0; i < expected.length; ++i) {
            assertEquals(expected[i], statements[i]);
        }
    }

    public void testBadUtf8() throws Exception {
        try {
            SqlUpgrade.readFile(new ByteArrayInputStream(new byte[] { (byte) 0x80 }));
            fail();
        } catch (RuntimeException e) {
            ObjectAssert.assertInstanceOf(CharacterCodingException.class, e.getCause());
        }
    }

    public void testGoodUtf8() throws Exception {
        String[] sqlStatements = SqlUpgrade.readFile(new ByteArrayInputStream(new byte[] { (byte) 0xe2, (byte) 0x82,
                (byte) 0xac }));
        assertEquals(1, sqlStatements.length);
        String euroSign = sqlStatements[0];
        assertEquals("\n\u20AC", euroSign);
    }

    public void testExecuteStream() throws Exception {
        SqlUpgrade persistence = new SqlUpgrade(null, -1);
        Connection conn = new Database().openConnection();
        byte[] sql = ("create table FOO(DATABASE_VERSION INTEGER);\n" + "--some comment\n"
                + "insert into FOO(DATABASE_VERSION) VALUES(53);\n").getBytes("UTF-8");
        ByteArrayInputStream in = new ByteArrayInputStream(sql);
        persistence.execute(in, conn);
        conn.commit();
        readOneValueFromFoo(conn);
    }

    public void testUpgradeDatabase() throws Exception {
        Database database = new Database();
        database.execute("create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
        database.execute("insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(78)");
        Connection connection = database.openConnection();
        connection.setAutoCommit(false);
        DatabaseVersionPersistence persistence = new DatabaseVersionPersistence(connection);
        persistence.upgradeDatabase(connection, 80);
        connection.commit();

        readOneValueFromFoo(connection);
    }

    private void readOneValueFromFoo(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery("select * from FOO");
        assertTrue(results.next());
        int valueFromFoo = results.getInt(1);
        assertEquals(53, valueFromFoo);
        assertFalse(results.next());
        results.close();
        statement.close();
    }

    public void testErrorWrapping() throws Exception {
        Database database = new Database();
        database.execute("create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
        database.execute("insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(78)");
        Connection connection = database.openConnection();
        Upgrade upgrade = new Upgrade(79) {

            @Override
            public void upgrade(Connection connection) {
                throw new RuntimeException("tried but failed");
            }

        };
        DatabaseVersionPersistence persistence = javaOnlyPersistence(database, upgrade);
        try {
            persistence.upgradeDatabase(connection, 79);
            fail();
        } catch (RuntimeException e) {
            assertEquals("error in upgrading to 79", e.getMessage());
            assertEquals("tried but failed", e.getCause().getMessage());
        }
    }

    public void testNotJavaOrSql() throws Exception {
        Database database = new Database();
        DatabaseVersionPersistence persistence = new DatabaseVersionPersistence(database.openConnection(),
                Collections.EMPTY_MAP) {
            @Override
            URL getSqlResourceLocation(String name) {
                return null;
            }
        };
        try {
            persistence.findUpgrade(69);
            fail();
        } catch (IllegalStateException e) {
            StringAssert.assertStartsWith("Did not find upgrade to 69 in java " + "or in upgrade_to_69.sql next to ", e
                    .getMessage());
        }
    }

    public void testJavaOnly() throws Exception {
        Database database = new Database();
        DatabaseVersionPersistence persistence = javaOnlyPersistence(database);
        DummyUpgrade found = (DummyUpgrade) persistence.findUpgrade(69);
        found.upgrade(null);
        assertEquals("upgrade to 69\n", found.getLog());
    }

    public void testJavaConditional() throws Exception {
        Database database = new Database();
        DatabaseVersionPersistence persistence = conditionalPersistence(database);
        SqlUpgrade found = persistence.findUpgradeScript(10, CONDITIONAL_UPGRADE_10_NAME);
        assertTrue(found != null);
        SqlUpgrade found_downgrade = persistence.findUpgradeScript(10, CONDITIONAL_DOWNGRADE_10_NAME);
        assertTrue(found_downgrade != null);
    }

    private DatabaseVersionPersistence javaOnlyPersistence(Database database) {
        return javaOnlyPersistence(database, new DummyUpgrade(69));
    }

    private DatabaseVersionPersistence javaOnlyPersistence(Database database, Upgrade upgrade) {
        Map<Integer, Upgrade> registrations = new HashMap<Integer, Upgrade>();
        DatabaseVersionPersistence.register(registrations, upgrade);
        DatabaseVersionPersistence persistence = new DatabaseVersionPersistence(database.openConnection(),
                registrations) {
            @Override
            URL getSqlResourceLocation(String name) {
                return null;
            }
        };
        return persistence;
    }

    public void testJavaAndSql() throws Exception {
        Database database = new Database();
        DatabaseVersionPersistence persistence = javaAndSqlPersistence(database);

        try {
            persistence.findUpgrade(69);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Found upgrade to 69 both in java and in upgrade_to_69.sql", e.getMessage());
        }
    }

    private DatabaseVersionPersistence javaAndSqlPersistence(Database database) {
        Map<Integer, Upgrade> registrations = new HashMap<Integer, Upgrade>();
        DatabaseVersionPersistence.register(registrations, new DummyUpgrade(69));
        DatabaseVersionPersistence persistence = new DatabaseVersionPersistence(database.openConnection(),
                registrations) {
            @Override
            URL getSqlResourceLocation(String name) {
                if ("upgrade_to_69.sql".equals(name)) {
                    try {
                        return new URL("file:" + name);
                    } catch (MalformedURLException e) {
                        throw (AssertionFailedError) new AssertionFailedError().initCause(e);
                    }
                } else {
                    throw new AssertionFailedError("got unexpected " + name);
                }
            }
        };
        return persistence;
    }

    private static String CONDITIONAL_UPGRADE_10_NAME = "upgrade_to_10_conditional.sql";
    private static String CONDITIONAL_DOWNGRADE_10_NAME = "downgrade_from_10_conditional.sql";

    private DatabaseVersionPersistence conditionalPersistence(Database database) {
        DatabaseVersionPersistence persistence = new DatabaseVersionPersistence(database.openConnection()) {
            @Override
            public URL getSqlResourceLocation(String name) {
                if (CONDITIONAL_UPGRADE_10_NAME.equals(name) || CONDITIONAL_DOWNGRADE_10_NAME.equals(name)) {
                    try {
                        return new URL("file:" + name);
                    } catch (MalformedURLException e) {
                        throw (AssertionFailedError) new AssertionFailedError().initCause(e);
                    }
                } else {
                    throw new AssertionFailedError("got unexpected " + name);
                }
            }
        };
        return persistence;
    }

    public void testDuplicateRegistration() throws Exception {
        Map<Integer, Upgrade> register = new HashMap<Integer, Upgrade>();
        DatabaseVersionPersistence.register(register, new DummyUpgrade(70));
        try {
            DatabaseVersionPersistence.register(register, new DummyUpgrade(70));
            fail();
        } catch (IllegalStateException e) {
            assertEquals("already have an upgrade to 70", e.getMessage());
        }
    }
}
