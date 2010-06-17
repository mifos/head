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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mifos.framework.persistence.Upgrade;
import org.mifos.reports.business.ReportsCategoryBO;
import org.mifos.security.util.SecurityConstants;

public class AddReport extends Upgrade {

    private short lookupId;
    private short lookupValueId;
    private short reportId;
    private short activityId;
    private short parentId;
    private final short category;
    private final String name;
    private final String lookupValue;
    private final String lookupName;
    private final String identifier;
    private final String fileName;

    public AddReport(int higherVersion, short category, String name, String fileName) {
        super(higherVersion);
        this.category = category;
        this.parentId = getParentId(category);
        this.name = name;
        this.lookupValue = "Can View " + name;
        this.lookupName = "Permissions." + lookupValue.replace(" ", "");
        this.identifier = name.replace(" ", "_").toLowerCase();
        this.fileName = fileName;
    }

    private short getParentId(short category) {
        if (category == ReportsCategoryBO.ANALYSIS) {
            return SecurityConstants.ANALYSIS;
        }
        throw new RuntimeException("Unimplemented category");
    }

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
        this.lookupId = getNextId(connection, "LOOKUP_ID", "LOOKUP_VALUE");
        this.lookupValueId = getNextId(connection, "LOOKUP_VALUE_ID", "LOOKUP_VALUE_LOCALE");
        this.reportId = getNextId(connection, "REPORT_ID", "REPORT");
        this.activityId = getNextId(connection, "ACTIVITY_ID", "ACTIVITY");
        doUpgrade(connection);
        upgradeVersion(connection);
    }

    private short getNextId(Connection connection, String idName, String table) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SELECT MAX(" + idName + ") FROM " + table;
        short id = 0;
        ResultSet results = statement.executeQuery(query);
        if (results.next()) {
            id = results.getShort(1);
            id++;
            statement.close();
        } else {
            statement.close();
            throw new RuntimeException("unable to find max " + idName + " from " + table);
        }
        return id;
    }

    void doUpgrade(Connection connection) throws SQLException {
        String sql = "INSERT INTO LOOKUP_VALUE (LOOKUP_ID, ENTITY_ID, LOOKUP_NAME) VALUES (" + lookupId + ",87,'"
                + lookupName + "')";
        connection.createStatement().executeUpdate(sql);
        sql = "INSERT INTO LOOKUP_VALUE_LOCALE (LOOKUP_VALUE_ID, LOCALE_ID, LOOKUP_ID, LOOKUP_VALUE) VALUES   ("
                + lookupValueId + ",1," + lookupId + ",'" + lookupValue + "')";
        connection.createStatement().executeUpdate(sql);
        sql = "INSERT INTO ACTIVITY (ACTIVITY_ID, PARENT_ID, ACTIVITY_NAME_LOOKUP_ID, DESCRIPTION_LOOKUP_ID) VALUES ("
                + activityId + "," + parentId + "," + lookupId + "," + lookupId + ")";
        connection.createStatement().executeUpdate(sql);
        sql = "INSERT INTO ROLES_ACTIVITY (ACTIVITY_ID, ROLE_ID) VALUES (" + activityId + ",1)";
        connection.createStatement().executeUpdate(sql);
        sql = "INSERT INTO REPORT (REPORT_ID, REPORT_CATEGORY_ID, REPORT_NAME, REPORT_IDENTIFIER, ACTIVITY_ID, REPORT_ACTIVE) VALUES ("
                + reportId + "," + category + ",'" + name + "','" + identifier + "'," + activityId + ",1)";
        connection.createStatement().executeUpdate(sql);
        sql = "INSERT INTO REPORT_JASPER_MAP (REPORT_ID, REPORT_CATEGORY_ID, REPORT_NAME, REPORT_IDENTIFIER, REPORT_JASPER) VALUES  ("
                + reportId + "," + category + ",'" + name + "','" + identifier + "','" + fileName + "')";
        connection.createStatement().executeUpdate(sql);
    }

}
