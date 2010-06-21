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

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.master.persistence.Upgrade213;
import org.mifos.application.master.persistence.Upgrade223;
import org.mifos.application.master.persistence.Upgrade225;
import org.mifos.application.master.persistence.Upgrade237;
import org.mifos.application.master.persistence.Upgrade238;
import org.mifos.application.master.persistence.Upgrade240;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.reports.business.ReportsCategoryBO;
import org.mifos.reports.persistence.AddReport;
import org.mifos.security.AddActivity;
import org.mifos.security.util.SecurityConstants;

public class DatabaseVersionPersistence {

    public static final int APPLICATION_VERSION = 257;
    public static final int LATEST_CHECKPOINT_VERSION = 212;
    private final Connection connection;
    private final Map<Integer, Upgrade> registeredUpgrades;

    public static void register(Map<Integer, Upgrade> register, Upgrade upgrade) {
        int higherVersion = upgrade.higherVersion();
        if (register.containsKey(higherVersion)) {
            throw new IllegalStateException("already have an upgrade to " + higherVersion);
        }
        register.put(higherVersion, upgrade);
    }

    public static final short ENGLISH_LOCALE = 1;

    public static Map<Integer, Upgrade> masterRegister() {
        Map<Integer, Upgrade> register = new HashMap<Integer, Upgrade>();
        register(register, new Upgrade213());
        register216(register);
        register(register, new Upgrade223());
        register(register, new Upgrade225());
        register236(register);
        register(register, new Upgrade237());
        register(register, new Upgrade238());
        register(register, new Upgrade240());
        register248(register);
        register255(register);
        register256(register);
        return Collections.unmodifiableMap(register);
    }


    /**
     * Adds activity/role/permission data for transactions import bulk settings.
     */
    private static void register216(Map<Integer, Upgrade> register) {
        register(register, new AddActivity(216, "Permissions-CanImportTransactions",
                SecurityConstants.CAN_IMPORT_TRANSACTIONS, SecurityConstants.BULK));
    }

    private static void register236(Map<Integer, Upgrade> register) {
        register(register, new AddActivity(236, "Permissions-CanShutdownMifos",
                SecurityConstants.CAN_SHUTDOWN_MIFOS, SecurityConstants.SYSTEM_INFORMATION));
    }

    private static void register248(Map<Integer, Upgrade> register) {
        register(register, new AddActivity(248, "Permissions-CanDefineHoliday",
                SecurityConstants.CAN_DEFINE_HOLIDAY, SecurityConstants.ORGANIZATION_MANAGEMENT));
//        register(register, new CompositeUpgrade(
//                new AddActivity(248, SecurityConstants.PRODUCT_MIX, null, ENGLISH_LOCALE, "Product mix"),
//                new AddActivity(248, SecurityConstants.CAN_DEFINE_PRODUCT_MIX, SecurityConstants.PRODUCT_MIX, ENGLISH_LOCALE, "Can Define product mix"),
//                new AddActivity(248, SecurityConstants.CAN_EDIT_PRODUCT_MIX, SecurityConstants.PRODUCT_MIX, ENGLISH_LOCALE, "Can Edit product mix")));
    }

    private static void register255(Map<Integer, Upgrade> register) {
        register(register, new AddReport(255, ReportsCategoryBO.ANALYSIS,
                "Detailed Aging Of Portfolio At Risk Report",
                "DetailedAgingPortfolioAtRiskReport.rptdesign"));
    }

    private static void register256(Map<Integer, Upgrade> register) {
        register(register, new AddReport(256, ReportsCategoryBO.ANALYSIS,
                "General Ledger Report",
                "GeneralLedgerReport.rptdesign"));
    }

    public DatabaseVersionPersistence() {
        this(StaticHibernateUtil.getSessionTL().connection());
    }

    public DatabaseVersionPersistence(Connection connection) {
        this(connection, masterRegister());
    }

    public DatabaseVersionPersistence(Connection connection, Map<Integer, Upgrade> registeredUpgrades) {
        this.connection = connection;
        this.registeredUpgrades = registeredUpgrades;
    }

    private Connection getConnection() {
        return connection;
    }

    public int read() throws SQLException {
        return read(getConnection());
    }

    public int read(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery("select database_version from database_version");
        if (results.next()) {
            int version = results.getInt("database_version");
            if (results.next()) {
                throw new RuntimeException("too many rows in database_version");
            }
            statement.close();
            return version;
        }
        throw new RuntimeException("No row in database_version");
    }

    public void write(int version) throws SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        int rows = statement.executeUpdate("update database_version set database_version = " + version);
        statement.close();
        if (rows != 1) {
            throw new RuntimeException("Unable to update database version (" + rows + " rows updated)");
        }
        connection.commit();
    }

    public boolean isVersioned() throws SQLException {
        return isVersioned(getConnection());
    }

    boolean isVersioned(Connection conn) throws SQLException {
        ResultSet results = conn.getMetaData().getColumns(null, null, "database_version", "database_version");
        boolean foundColumns = results.next();
        results.close();
        return foundColumns;
    }

    public List<Upgrade> scripts(int applicationVersion, int databaseVersion) {
        if (applicationVersion < databaseVersion) {
            /*
             * Automatically applying downgrades would be a mistake because a
             * downgrade is likely to destroy data (for example, if the upgrade
             * had added a column and the application had put some data into
             * that column prior to the downgrade).
             */
            throw new UnsupportedOperationException("your database needs to be downgraded from " + databaseVersion
                    + " to " + applicationVersion);
        }

        if (applicationVersion == databaseVersion) {
            return Collections.emptyList();
        }

        List<Upgrade> upgrades = new ArrayList<Upgrade>(applicationVersion - databaseVersion);
        for (int i = databaseVersion; i < applicationVersion; i++) {
            Upgrade upgrade = findUpgrade(i + 1);
            upgrades.add(upgrade);
        }
        return Collections.unmodifiableList(upgrades);
    }

    Upgrade findUpgrade(int higherVersion) {
        boolean foundInJava = registeredUpgrades.containsKey(higherVersion);

        String name = "upgrade_to_" + higherVersion + ".sql";
        URL url = getSqlResourceLocation(name);
        boolean foundInSql = url != null;

        if (foundInJava && foundInSql) {
            throw new IllegalStateException("Found upgrade to " + higherVersion + " both in java and in " + name);
        } else if (foundInJava) {
            return registeredUpgrades.get(higherVersion);
        } else if (foundInSql) {
            return new SqlUpgrade(url, higherVersion);
        } else {
            String location;
            try {
                location = " in " + getClass().getProtectionDomain().getCodeSource().getLocation().toString();
            } catch (Throwable e) {
                location = "";
            }
            throw new IllegalStateException("Did not find upgrade to " + higherVersion + " in java or in " + name
                    + " next to " + getClass().getName() + location);
        }
    }

    URL getSqlResourceLocation(String name) {
        return SqlResource.getInstance().getUrl(name);
    }

    public SqlUpgrade findUpgradeScript(int higherVersion, String scriptName) {
        // Currently, SQL files are located in the same package as
        // DatabaseVersionPersistence so we need to load the file from this
        // class
        URL url = getSqlResourceLocation(scriptName);
        boolean foundInSql = url != null;

        if (foundInSql) {
            return new SqlUpgrade(url, higherVersion);
        }

        String location;
        try {
            location = " in " + getClass().getProtectionDomain().getCodeSource().getLocation().toString();
        } catch (Throwable e) {
            location = "";
        }
        throw new IllegalStateException("Did not find upgrade to " + higherVersion + " in java or in " + scriptName
                + " next to " + getClass().getName() + location);
    }

    public void upgradeDatabase() throws Exception {
        Connection conn = getConnection();
        try {
            upgradeDatabase(conn, APPLICATION_VERSION);
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            throw e;
        }
    }

    void upgradeDatabase(Connection connection, int upgradeTo) throws Exception {
        if (!isVersioned(connection)) {
            throw new RuntimeException("Database version is too old to be upgraded automatically");
        }

        int version = read(connection);
        for (Upgrade upgrade : scripts(upgradeTo, version)) {
            try {
                upgrade.upgrade(connection);
            } catch (Exception e) {
                throw new RuntimeException("error in upgrading to " + upgrade.higherVersion(), e);
            }

            int upgradedVersion = read(connection);
            if (upgradedVersion != version + 1) {
                throw new RuntimeException("upgrade script from " + version + " did not end up at " + (version + 1)
                        + "(was instead " + upgradedVersion + ")");
            }
            version = upgradedVersion;
        }
    }

}
