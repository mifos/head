/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import org.mifos.db.upgrade.DatabaseUpgradeSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.springframework.context.ApplicationContext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigrator.class);

    private Connection connection;

    private static final String APPLIED_UPGRADES = "applied_upgrades";

    public DatabaseMigrator() {
        this.connection = StaticHibernateUtil.getSessionTL().connection();
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            logger.error("couldn't change autocommit", e);
        }
    }

    public void upgrade(ApplicationContext applicationContext) throws Exception {
        DatabaseUpgradeSupport databaseUpgradeSupport = (DatabaseUpgradeSupport) applicationContext.getBean("databaseUpgradeSupport");
        databaseUpgradeSupport.expansion();
    }

    public List<Integer> getAppliedUpgrades() {

        List<Integer> appliedUpgrades = new ArrayList<Integer>();

        Statement stmt = null;
        try {
            stmt = connection.createStatement(ResultSet.FETCH_FORWARD, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery("select upgrade_id from " + APPLIED_UPGRADES + " order by upgrade_id");

            while (rs.next()) {
                appliedUpgrades.add(rs.getInt(1));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            logger.error("error fetching applied upgrades", e);
        }

        return appliedUpgrades;

    }

    public boolean isNSDU() throws SQLException {
        ResultSet results = connection.getMetaData().getColumns(null, null, APPLIED_UPGRADES, "upgrade_id");
        boolean foundAppliedUpgrades = results.next();
        results.close();

        results = connection.getMetaData().getColumns(null, null, "database_version", "database_version");
        boolean foundDatabaseVersion = results.next();

        if (!(foundAppliedUpgrades || foundDatabaseVersion)) {
            throw new RuntimeException("Database is too old to be upgraded");
        }
        return foundAppliedUpgrades;
    }

}
