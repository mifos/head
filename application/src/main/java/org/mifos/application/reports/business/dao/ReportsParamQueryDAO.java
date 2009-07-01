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

package org.mifos.application.reports.business.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.mifos.application.reports.business.ReportsDataSource;
import org.mifos.application.reports.business.ReportsParamQuery;
import org.mifos.application.reports.business.ReportsParams;

/**
 * This class encapsulates the Reports Parameters Query
 */

public class ReportsParamQueryDAO {
    /**
     * This method lists all the values of parameter
     */
    public List listValuesOfParameters(ReportsParams rps) throws Exception {
        List<ReportsParamQuery> alValues = new ArrayList();
        ReportsDataSource rds = rps.getReportsDataSource();

        Connection con = null;
        try {
            String driver = rds.getDriver();
            String url = rds.getUrl();
            String username = rds.getUsername();
            String password = rds.getPassword();
            if (driver != null && !driver.equals("")) {
                Class.forName(driver);
                con = DriverManager.getConnection(url, username, password);
                Statement stm = con.createStatement();
                String data = rps.getData();
                if (data != null && !data.equals("")) {
                    ResultSet rs = stm.executeQuery(data);
                    ResultSetMetaData rsmd = rs.getMetaData();

                    while (rs.next()) {
                        ReportsParamQuery obj = new ReportsParamQuery();
                        if (rsmd.getColumnCount() >= 2) {
                            obj.setValue1(rs.getString(1));
                            obj.setValue2(rs.getString(2));
                        } else if (rsmd.getColumnCount() == 1) {
                            obj.setValue1(rs.getString(1));
                            obj.setValue2(rs.getString(1));
                        }
                        alValues.add(obj);
                    }
                }
            }
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return alValues;
    }

}
