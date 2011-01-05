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

package org.mifos.platform.accounting.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.mifos.framework.util.ConfigurationLocator;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DatabaseManager {

    private static DatabaseManager databaseManager;

    public static final Properties properties = new Properties();

    public static DataSource ds;

    private DatabaseManager() throws SQLException {
	try {
	    properties.load(new FileReader(new ConfigurationLocator()
		    .getFile("mainDatabase.properties")));
	    properties.load(new FileReader(new ConfigurationLocator()
		    .getFile("local.properties")));
	    configureDataSource();
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
    }

    public static DatabaseManager getInstance() throws SQLException {
	if (databaseManager == null) {
	    databaseManager = new DatabaseManager();
	}
	return databaseManager;
    }

    private void configureDataSource() throws SQLException {
	ds = new MysqlDataSource();
	((MysqlDataSource) ds).setURL("jdbc:mysql://" + getHost() + ":"
		+ getPort() + "/" + getSchemaName());
	((MysqlDataSource) ds).setUser(getUserName());
	((MysqlDataSource) ds).setPassword(getPassword());
	((MysqlDataSource) ds).getConnection();
    }

    public Connection getConnection() throws SQLException {
	return ds.getConnection();
    }

    public String getSchemaName() {
	return getProperty("main.database");
    }

    public String getUserName() {
	return getProperty("main.database.user");
    }

    public String getPassword() {
	return getProperty("main.database.password");
    }

    public String getHost() {
	return getProperty("main.database.host");
    }

    public String getPort() {
	return getProperty("main.database.port");
    }

    private String getProperty(String key) {
	String value = properties.getProperty(key);
	if (StringUtils.isEmpty(value)) {
	    throw new IllegalAccessError("No property defined for " + key);
	}
	return value;
    }
}
