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
        this.lookupId = getNextId(connection, "lookup_id", "lookup_value");
        this.lookupValueId = getNextId(connection, "lookup_value_id", "lookup_value_locale");
        this.reportId = getNextId(connection, "report_id", "report");
        this.activityId = getNextId(connection, "activity_id", "activity");
        doUpgrade(connection);
        upgradeVersion(connection);
    }

    private short getNextId(Connection connection, String idName, String table) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "select max(" + idName + ") from " + table;
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
        String sql = "insert into lookup_value (lookup_id, entity_id, lookup_name) values (" + lookupId + ",87,'"
                + lookupName + "')";
        connection.createStatement().executeUpdate(sql);
        sql = "insert into lookup_value_locale (lookup_value_id, locale_id, lookup_id, lookup_value) values   ("
                + lookupValueId + ",1," + lookupId + ",'" + lookupValue + "')";
        connection.createStatement().executeUpdate(sql);
        sql = "insert into activity (activity_id, parent_id, activity_NAME_lookup_id, description_lookup_id) values ("
                + activityId + "," + parentId + "," + lookupId + "," + lookupId + ")";
        connection.createStatement().executeUpdate(sql);
        sql = "insert into roles_activity (activity_id, role_id) values (" + activityId + ",1)";
        connection.createStatement().executeUpdate(sql);
        sql = "insert into report (report_id, report_category_id, report_name, report_identifier, activity_id, report_active) values ("
                + reportId + "," + category + ",'" + name + "','" + identifier + "'," + activityId + ",1)";
        connection.createStatement().executeUpdate(sql);
        sql = "insert into report_jasper_map (report_id, report_category_id, report_name, report_identifier, report_jasper) values  ("
                + reportId + "," + category + ",'" + name + "','" + identifier + "','" + fileName + "')";
        connection.createStatement().executeUpdate(sql);
    }

}
