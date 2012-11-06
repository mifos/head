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

package org.mifos.config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;

import org.mifos.config.exceptions.ConfigurationException;
import javax.annotation.PostConstruct;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.mifos.db.populate.DefaultDatabaseLoader;

public class DatabaseConfiguration {

    private final static String VCAP_SERVICES_VAR = "VCAP_SERVICES";
    private final static String INVALID_STRUCTURE = "Invalid configuration variable structure";

    private String host;
    private String port;
    private String user;
    private String password;
    private String dbName;
    private String dbPentahoDW;
    private String params;
    
    public DatabaseConfiguration() { 
        //Empty constructor required by Spring 
    }
    
    public DatabaseConfiguration(final String user, final String password, final String dbName, final String host,
            final String port, final String dbPentahoDW, final String params) throws ConfigurationException {
        this.user = user;
        this.password = password;
        this.dbName = dbName;
        this.host = host;
        this.port = port;
        this.dbPentahoDW = dbPentahoDW;
        this.params = params;

        readVCAPConfiguration();
    }
    
    @PostConstruct
    public void init() throws SQLException, IOException {
        Connection jdbcConnection = null;
        try {
        jdbcConnection = 
                DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/?%s", host, port, params), user, password);
        DefaultDatabaseLoader defaultDatabaseLoader = new DefaultDatabaseLoader(jdbcConnection, dbName, dbPentahoDW);
        defaultDatabaseLoader.createDatabase();
        defaultDatabaseLoader.populateDatabase();
        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }
    
    private void readVCAPConfiguration() throws ConfigurationException {
        final String vcapServicesVar = System.getenv(VCAP_SERVICES_VAR);

        if (vcapServicesVar != null) {
            // use database configuration from the system variable to replace the default config
            final JSONObject json = (JSONObject) JSONSerializer.toJSON(vcapServicesVar);

            String mysqlKey = null;
            @SuppressWarnings("rawtypes")
            final Iterator iterator = json.keys();
            while (iterator.hasNext()) {
                final String key = (String) iterator.next();
                if (key.startsWith("mysql")) {
                    mysqlKey = key;
                    break;
                }
            }

            if (mysqlKey == null) {
                throw new ConfigurationException(INVALID_STRUCTURE);
            }

            final JSON mysqlJson = (JSON) json.get(mysqlKey);
            JSONObject dbJson;
            if (mysqlJson.isArray()) {
                final JSONArray mysqlJsonArray = (JSONArray) mysqlJson;
                if (mysqlJsonArray.size() < 1) {
                    throw new ConfigurationException(INVALID_STRUCTURE);
                }
                dbJson = (JSONObject) mysqlJsonArray.get(0);
            } else {
                dbJson = (JSONObject) mysqlJson;
            }

            final JSONObject credentialsJson = (JSONObject) dbJson.get("credentials");

            this.dbName = credentialsJson.getString("name");
            this.host = credentialsJson.getString("host");
            this.port = credentialsJson.getString("port");
            this.user = credentialsJson.getString("user");
            this.password = credentialsJson.getString("password");
            this.dbPentahoDW=credentialsJson.getString("dbPentahoDW");
        }
    }
    
    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbPentahoDW() {
        return dbPentahoDW;
    }

    public void setDbPentahoDW(String dbPentahoDW) {
        this.dbPentahoDW = dbPentahoDW;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
