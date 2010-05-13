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

package org.mifos.reports.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mifos.framework.persistence.Upgrade;
import org.mifos.reports.business.ReportsBO;

public class AddReport extends Upgrade {

    private static final int HIGHER_VERSION = 185;
    private short newId;
    private final short category;
    private final String name;
    private final String identifier;
    private final String design;
    private Short activityId;

    /**
     * Activity ID is non-null for Database version > 184. Please use the other
     * contructor.
     */
    @Deprecated
    public AddReport(int higherVersion, short newId, short category, String name, String identifier, String design) {
        this(higherVersion, newId, category, name, identifier, design, null);
    }

    public AddReport(int higherVersion, short newId, short category, String name, String identifier, String design,
            Short activityId) {
        super(higherVersion);
        this.newId = newId;
        this.category = category;
        this.name = name;
        this.identifier = identifier;
        this.design = design;
        this.activityId = activityId;
    }

    @Override
    public void upgrade(Connection connection)
            throws IOException, SQLException {
        doUpgrade(connection);
    }

    void doUpgrade(Connection connection) throws SQLException {
        insertIntoReport(connection);
        insertIntoReportJasperMap(connection);
    }

    private short getNextReportId(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SELECT MAX(REPORT_ID) FROM REPORT";
        short reportId = 0;
        ResultSet results = statement.executeQuery(query);
        if (results.next()) {
            reportId = results.getShort(1);
            reportId++;
            statement.close();
        } else {
            statement.close();
            throw new RuntimeException("unable to find max report id to upgrade");
        }

        return reportId;
    }

    private void insertIntoReport(Connection connection) throws SQLException {
        if (newId == 0) {
            newId = getNextReportId(connection);
        }
        PreparedStatement statement = connection.prepareStatement(isLowerVersion() ? getSqlForLowerVersion()
                : getSqlForHigherVersion());
        statement.setShort(1, newId);
        statement.setShort(2, category);
        statement.setString(3, name);
        statement.setString(4, identifier);
        if (!isLowerVersion()) {
            statement.setShort(5, ReportsBO.ACTIVE);
            statement.setShort(6, activityId);
        }
        statement.executeUpdate();
        statement.close();
    }

    private boolean isLowerVersion() {
        return higherVersion() < HIGHER_VERSION;
    }

    private String getSqlForLowerVersion() {
        return "INSERT INTO REPORT(REPORT_ID, REPORT_CATEGORY_ID," + "REPORT_NAME,REPORT_IDENTIFIER)"
                + "VALUES(?,?,?,?)";
    }

    private String getSqlForHigherVersion() {
        return "INSERT INTO REPORT(REPORT_ID, REPORT_CATEGORY_ID,"
                + "REPORT_NAME,REPORT_IDENTIFIER, REPORT_ACTIVE, ACTIVITY_ID)" + "VALUES(?,?,?,?,?,?)";
    }

    private void insertIntoReportJasperMap(Connection connection) throws SQLException {
        PreparedStatement statement = connection
                .prepareStatement("INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,"
                        + "REPORT_NAME, REPORT_IDENTIFIER, REPORT_JASPER) " + "VALUES (?,?,?,?,?)");
        statement.setShort(1, newId);
        statement.setShort(2, category);
        statement.setString(3, name);
        statement.setString(4, identifier);
        statement.setString(5, design);
        statement.executeUpdate();
        statement.close();
    }
}
