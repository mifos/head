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

package org.mifos.test.acceptance.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.operation.DatabaseOperation;
import org.mifos.framework.persistence.SqlExecutor;


public class DataSetUpgrade {
    private final String databaseName;
    private final String databaseUser;
    private final String databasePassword;
    private final IDataSet dataSet;

    // Table and misc. constants
    private static final String UPGRADE_SQLS_DIR = "application/src/main/sql/";
    private static final String DATABASE_VERSION = "DATABASE_VERSION";

    public DataSetUpgrade(IDataSet dataSet,
                          String databaseName,
                          String databaseUser,
                          String databasePassword
                          ) {
        this.databaseName = databaseName;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
        this.dataSet = dataSet;
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.CloseResource"})
    // Rationale: There's no way to open new file in the loop if I don't want to
    // instansiate them there + The resource is closed.
    public void upgrade() throws ClassNotFoundException, SQLException, DatabaseUnitException, FileNotFoundException {
        // 1. Figure out what changes to apply
        // 2. Apply

        List<String> changesToApply = getSQLsToApply(dataSet);


        Class.forName("com.mysql.jdbc.Driver");
        Connection jdbcConnection = DriverManager.getConnection("jdbc:mysql://localhost/" + databaseName
                + "?sessionVariables=FOREIGN_KEY_CHECKS=0", databaseUser, databasePassword);

        // clear the database and insert the data set
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(jdbcConnection), dataSet);

        // apply files
        for (String file : changesToApply) {
            InputStream in = new FileInputStream(UPGRADE_SQLS_DIR + file);
            SqlExecutor.execute(in, jdbcConnection);
        }

        // dump...

        jdbcConnection.close();
    }

    private static List<String> getSQLsToApply(IDataSet dataSet) throws DataSetException {
        List<String> sqls = new ArrayList<String>();
        List<String> availableVersions = getAvailableVersionsAboveCurrent(getCurrentVersion(dataSet));

        for (String version : availableVersions) {
            sqls.add("upgrade_to_" + version + ".sql");
        }

        return sqls;
    }

    @SuppressWarnings("PMD.ConfusingTernary")
    private static List<String> getAvailableVersionsAboveCurrent(String currentVersion) throws DataSetException {
        File sqlDirectory = new File(UPGRADE_SQLS_DIR);

        String[] files = sqlDirectory.list();
        List<String> versions = new ArrayList<String>();

        /*
         * The format of the filenames are 1. upgrade_to_xxx.sql where xxx is
         * the database version. 2. upgrade_to_xxx_part_y.sql where xxx is the
         * same as above and y is used to apply the changes in the right order.
         */

        for (String file : files) {
            // Make sure the first 11 chars are "upgrade_to_"
            // and if so discard them + the last 4 (".sql").
            if (file.length() > 12 && "upgrade_to_".equals(file.substring(0, 11))) {
                String version = file.substring(11, file.length() - 4);
                versions.add(version);
            }
        }

        Collections.sort(versions);

        // We now have to find out what versions are more recent than
        // the one in the dataset.
        int cutOff = 0;

        if (versions.indexOf(currentVersion) != -1) {
            cutOff = versions.indexOf(currentVersion);
        } else if (versions.indexOf(currentVersion + "_part_1") != -1) {
            cutOff = versions.indexOf(currentVersion + "_part_1");
        }

        // We've found the index of the version so everything after the index is
        // what we want.
        cutOff += 1;

        // TODO if the upgrade sql's didn't contain the version, so it's
        // probably
        // the "first ever" version. Is this possible?

        return versions.subList(cutOff, versions.size());
    }

    /**
     * Returns the version of the data set.
     *
     * @param dataSet
     *            The data set that is examined.
     * @return The data set version.
     * @throws DataSetException
     */
    private static String getCurrentVersion(IDataSet dataSet) throws DataSetException {
        String version = "-1";
        // The current version is stored in the DATABASE_VERSION table.
        ITable databaseVersionTable = dataSet.getTable(DATABASE_VERSION);

        if (databaseVersionTable.getRowCount() > 0) {
            // the column name is the same as the database name
            version = databaseVersionTable.getValue(0, DATABASE_VERSION).toString();
        }

        return version;
    }
}
