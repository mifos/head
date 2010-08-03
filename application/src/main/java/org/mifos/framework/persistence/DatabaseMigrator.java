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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import org.mifos.reports.business.ReportsCategoryBO;
import org.mifos.reports.persistence.AddReport;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.mifos.core.ClasspathResource;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.AddActivity;
import org.mifos.security.util.SecurityConstants;

/**
 * This class handles automated database schema and data changes.
 *
 * <ul>
 * <li>read (for example) application/src/main/resources/sql/upgrades.txt (a file containing names of available upgrades
 * from checkpoint 2 releases back)</li>
 * <li>read which upgrades from upgrades.txt have been applied to a database</li>
 * <li>determine if the database needs upgrading</li>
 * <li>apply any upgrades not currently applied to the database</li>
 * </ul>
 */
public class DatabaseMigrator {

    private Connection connection;

    public SortedMap<Integer, String> availableUpgrades;

    private String upgradesPackage;

    public static final short ENGLISH_LOCALE = 1;

    public static final String CLASS_UPGRADE_TYPE = "class";
    public static final String METHOD_UPGRADE_TYPE = "method";
    public static final String SCRIPT_UPGRADE_TYPE = "sql";

    private static MifosLogger log = null;

    public DatabaseMigrator() {
        this(StaticHibernateUtil.getSessionTL().connection(), getAvailableUpgrades(),
                "org.mifos.application.master.persistence");
        log = MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER);

    }

    public DatabaseMigrator(Connection connection) {
        this(connection, getAvailableUpgrades(), "org.mifos.application.master.persistence");
    }

    public DatabaseMigrator(Connection connection, SortedMap<Integer, String> availableUpgrades, String upgradesPackage) {
        this.connection = connection;
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.availableUpgrades = availableUpgrades;
        this.upgradesPackage = upgradesPackage;

    }

    static SortedMap<Integer, String> getAvailableUpgrades() {
        Reader reader = null;
        BufferedReader bufferedReader = null;
        SortedMap<Integer, String> upgrades = new TreeMap<Integer, String>();
        try {
            reader = ClasspathResource.getInstance("/sql/").getAsReader("upgrades.txt");
            bufferedReader = new BufferedReader(reader);

            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                String upgradeType = line.substring(0, line.indexOf(':'));
                Integer upgradeId = Integer.parseInt(line.substring(line.indexOf(':') + 1));
                upgrades.put(upgradeId, upgradeType);
            }

        } catch (IOException e) {
            log.error("An error occurred whilst reading the upgrades.txt file");

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return upgrades;
    }

    public void upgrade(boolean isFirstRun) throws Exception {
        if (isFirstRun) {
            firstRun();
        }

        List<Integer> appliedUpgrades = getAppliedUpgrades();

        for (int i : availableUpgrades.keySet()) {
            if (!appliedUpgrades.contains(i)) {
                System.out.println("applying upgrade with timestamp: " + i);
                applyUpgrade(i, availableUpgrades.get(i));
            }
        }

    }

    public void upgrade() throws Exception {

        if (isNSDU()) {
            upgrade(false);
        } else {
            upgrade(true);
        }
    }

    public boolean checkForUnAppliedUpgrades() throws Exception {
        if (!this.isNSDU()) {
            createAppliedUpgradesTable();
        }

        boolean unAppliedUpgrades = false;

        List<Integer> appliedUpgrades = getAppliedUpgrades();

        for (int i : availableUpgrades.keySet()) {
            if (!appliedUpgrades.contains(i)) {
                return true;
            }
        }

        return unAppliedUpgrades;

    }

    private void firstRun() {
        try {
            firstRun(getLegacyUpgradesMap());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *
     * @param legacyUpgrades
     *            A mapping from the database version to the Unix time-stamp
     * @throws Exception
     */

    public void firstRun(Map<Integer, Integer> legacyUpgrades) throws Exception {
        createAppliedUpgradesTable();

        // check version number
        int databaseVersion = readDatabaseVersion();

        // insert all upgrades below version number in applied upgrades table
        for (Entry<Integer, Integer> e : legacyUpgrades.entrySet()) {
            if (e.getKey() <= databaseVersion) {
                Statement stmt = connection.createStatement();

                stmt.execute("insert into applied_upgrades values(" + e.getValue() + ")");
                connection.commit();
            }
        }

        if (!isNSDU()) {
            throw new Error("Failed to migrate schema to NSDU");
        }

    }

    public Map<Integer, Integer> getLegacyUpgradesMap() {
        Map<Integer, Integer> legacyUpgrades = new TreeMap<Integer, Integer>();
        legacyUpgrades.put(225, 1277565389);
        legacyUpgrades.put(226, 1277567194);
        legacyUpgrades.put(228, 1277567768);
        legacyUpgrades.put(229, 1277567885);
        legacyUpgrades.put(230, 1277567949);
        legacyUpgrades.put(231, 1277568944);
        legacyUpgrades.put(233, 1277569001);
        legacyUpgrades.put(234, 1277571296);
        legacyUpgrades.put(235, 1277571560);
        legacyUpgrades.put(236, 1277571792);
        legacyUpgrades.put(237, 1277571837);
        legacyUpgrades.put(238, 1277586926);
        legacyUpgrades.put(239, 1277587117);
        legacyUpgrades.put(240, 1277587199);
        legacyUpgrades.put(241, 1277587465);
        legacyUpgrades.put(242, 1277587818);
        legacyUpgrades.put(243, 1277587878);
        legacyUpgrades.put(244, 1277587947);
        legacyUpgrades.put(245, 1277588038);
        legacyUpgrades.put(246, 1277588072);
        legacyUpgrades.put(247, 1277588240);
        legacyUpgrades.put(248, 1277588373);
        legacyUpgrades.put(249, 1277588885);
        legacyUpgrades.put(250, 1277588973);
        legacyUpgrades.put(251, 1277589055);
        legacyUpgrades.put(252, 1277589236);
        legacyUpgrades.put(253, 1277589321);
        legacyUpgrades.put(254, 1277589383);
        legacyUpgrades.put(255, 1278540763);
        legacyUpgrades.put(256, 1278540832);
        legacyUpgrades.put(257, 1278542100);
        legacyUpgrades.put(258, 1278542119);
        legacyUpgrades.put(259, 1278542138);
        legacyUpgrades.put(260, 1278542152);
        legacyUpgrades.put(261, 1278542171);
        legacyUpgrades.put(262, 1279140399);
        legacyUpgrades.put(263, 1279272090);
        legacyUpgrades.put(264, 1280719328);
        legacyUpgrades.put(265, 1280719447);
        legacyUpgrades.put(266, 1280719676);
        legacyUpgrades.put(267, 1280721170);
        legacyUpgrades.put(268, 1280793109);

        return legacyUpgrades;
    }

    public int readDatabaseVersion() throws SQLException {
        return readDatabaseVersion(connection);
    }

    public int readDatabaseVersion(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery("select database_version from database_version");
        if (results.next()) {
            int version = results.getInt("database_version");
            if (results.next()) {
                throw new RuntimeException("too many rows in database_version");
            }
            return version;
        }
        throw new RuntimeException("No row in database_version");
    }

    private void createAppliedUpgradesTable() {
        try {
            Statement stmt = connection.createStatement();
            stmt.execute("create table  applied_upgrades" + "( upgrade_id integer not null,"
                    + "primary key(upgrade_id)" + ")engine=innodb character set utf8;");
            connection.commit();
        } catch (SQLException e) {
            System.err.print("Unable to create table applied_upgrade table for Non Sequential Database Upgrade");
        }
    }

    private void applyUpgrade(int upgradeNumber, String type) throws Exception {

        if (SCRIPT_UPGRADE_TYPE.equals(type)) {
            URL url = SqlResource.getInstance().getUrl("upgrade" + upgradeNumber + ".sql");
            SqlUpgrade sqlUpgrade = new SqlUpgrade(url);
            sqlUpgrade.upgrade(connection);

        } else if (CLASS_UPGRADE_TYPE.equals(type)) {
            String className = upgradesPackage + ".Upgrade" + upgradeNumber;

            Upgrade upgradeClass = getInstanceOfUpgradeClass(className);
            upgradeClass.upgrade(connection);

        } else if (METHOD_UPGRADE_TYPE.equals(type)) {

            Method method = DatabaseMigrator.class.getDeclaredMethod("upgrade" + upgradeNumber);
            Upgrade up = (Upgrade) method.invoke(this);
            up.upgrade(connection);
        }

        Statement stmt = connection.createStatement();
        stmt.execute("insert into applied_upgrades values (" + upgradeNumber + ")");
        connection.commit();
    }

    private Upgrade getInstanceOfUpgradeClass(String className) {
        Upgrade upgrade = null;
        Class<?> c = null;
        Constructor<?> cs = null;
        try {
            c = Class.forName(className);
            cs = c.getDeclaredConstructor();
            cs.setAccessible(true);
            upgrade = (Upgrade) cs.newInstance();

        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return upgrade;
    }

    private List<Integer> getAppliedUpgrades() {

        List<Integer> appliedUpgrades = new ArrayList<Integer>();

        Statement stmt = null;
        try {
            stmt = connection.createStatement(ResultSet.FETCH_FORWARD, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery("select upgrade_id from applied_upgrades order by upgrade_id");

            while (rs.next()) {
                appliedUpgrades.add(rs.getInt(1));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appliedUpgrades;

    }

    public boolean isNSDU() throws SQLException {
        return isNSDU(connection);
    }

    public boolean isNSDU(Connection conn) throws SQLException {
        ResultSet results = conn.getMetaData().getColumns(null, null, "applied_upgrades", "upgrade_id");
        boolean foundAppliedUpgrades = results.next();
        results.close();

        results = conn.getMetaData().getColumns(null, null, "database_version", "database_version");
        boolean foundDatabaseVersion = results.next();

        if (!(foundAppliedUpgrades || foundDatabaseVersion)) {
            throw new RuntimeException("Database is too old to be upgraded");
        }
        return foundAppliedUpgrades;
    }

    @SuppressWarnings("unused")
    private static Upgrade upgrade1277571792() {
        return new AddActivity("Permissions-CanShutdownMifos", SecurityConstants.CAN_OPEN_SHUTDOWN_PAGE,
                SecurityConstants.SYSTEM_INFORMATION);
    }

    @SuppressWarnings("unused")
    private static Upgrade upgrade1277588373() {
        return new AddActivity("Permissions-CanDefineHoliday", SecurityConstants.CAN_DEFINE_HOLIDAY,
                SecurityConstants.ORGANIZATION_MANAGEMENT);
        // register(register, new CompositeUpgrade(
        // new AddActivity(248, SecurityConstants.PRODUCT_MIX, null, ENGLISH_LOCALE, "Product mix"),
        // new AddActivity(248, SecurityConstants.CAN_DEFINE_PRODUCT_MIX, SecurityConstants.PRODUCT_MIX, ENGLISH_LOCALE,
        // "Can Define product mix"),
        // new AddActivity(248, SecurityConstants.CAN_EDIT_PRODUCT_MIX, SecurityConstants.PRODUCT_MIX, ENGLISH_LOCALE,
        // "Can Edit product mix")));
    }

    @SuppressWarnings("unused")
    private static Upgrade upgrade1278540763() {
        return new AddReport(ReportsCategoryBO.ANALYSIS, "Detailed Aging Of Portfolio At Risk Report",
                "DetailedAgingPortfolioAtRiskReport.rptdesign");
    }

    @SuppressWarnings("unused")
    private static Upgrade upgrade1278540832() {
        return new AddReport(ReportsCategoryBO.ANALYSIS, "General Ledger Report", "GeneralLedgerReport.rptdesign");
    }

}