/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.db.upgrade;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DbUpgrade implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;
    private DataSource dataSource;
    private String changeLog;

    public DbUpgradeValidationResult validate() throws SQLException, LiquibaseException {
        Liquibase liquibase = getLiquibase();
        return new DbUpgradeValidationResult(liquibase.listUnrunChangeSets(StringUtils.EMPTY));
    }

    public void upgrade() throws SQLException, LiquibaseException {
        Liquibase liquibase = getLiquibase();
        liquibase.update(StringUtils.EMPTY);
    }

    Liquibase getLiquibase() throws SQLException, LiquibaseException {
        Connection connection = dataSource.getConnection();
        Database database = getDatabase(connection);
        return new Liquibase(changeLog, new ResourceOpener(changeLog, resourceLoader), database);
    }

    // setter for DI
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // setter for DI
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    // setter for DI
    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    private Database getDatabase(Connection connection) throws DatabaseException {
        return DatabaseFactory.getInstance().
                findCorrectDatabaseImplementation(new JdbcConnection(connection));
    }
}
