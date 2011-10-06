/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import org.mifos.core.MifosRuntimeException;
import org.mifos.platform.util.CollectionUtils;
import org.mifos.platform.util.Transformer;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.apache.commons.lang.StringUtils.EMPTY;

public class DatabaseUpgradeSupport {

    public static final String EXPANSION = "expansion";
    public static final String CONTRACTION = "contraction";
    public static final String BEAN_NAME = "databaseUpgradeSupport";
    private Liquibase liquibase;
    private Database database;

    public DatabaseUpgradeSupport(DataSource dataSource, ResourceOpener resourceOpener) throws SQLException, LiquibaseException {
        database = initDatabase(dataSource);
        liquibase = initLiquibase(resourceOpener);
    }

    // constructor for unit tests support
    public DatabaseUpgradeSupport(Database database, Liquibase liquibase) {
        this.database = database;
        this.liquibase = liquibase;
    }

    private Database initDatabase(DataSource dataSource) throws DatabaseException, SQLException {
        return DatabaseFactory.getInstance().
                findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
    }

    private Liquibase initLiquibase(ResourceOpener resourceOpener) throws LiquibaseException {
        return new Liquibase(resourceOpener.getChangeLog(), resourceOpener, database);
    }

    public DbUpgradeValidationResult validate() throws SQLException, LiquibaseException {
        return new DbUpgradeValidationResult(liquibase.listUnrunChangeSets(EXPANSION));
    }

    public void expansion() throws SQLException, LiquibaseException {
        liquibase.update(EXPANSION);
    }

    public void contraction() throws SQLException, LiquibaseException {
        liquibase.update(CONTRACTION);
    }

    public List<ChangeSetInfo> listRanUpgrades() {
        try {
            return CollectionUtils.collect(database.getRanChangeSetList(), new Transformer<RanChangeSet, ChangeSetInfo>() {
                @Override
                public ChangeSetInfo transform(RanChangeSet ranChangeSet) {
                    return new ChangeSetInfo(ranChangeSet);
                }
            });
        } catch (DatabaseException e) {
            throw new MifosRuntimeException(e);
        }
    }

    public List<UnRunChangeSetInfo> listUnRunChangeSets() {
        try {
            return CollectionUtils.collect(liquibase.listUnrunChangeSets(EMPTY), new Transformer<ChangeSet, UnRunChangeSetInfo>() {
                @Override
                public UnRunChangeSetInfo transform(ChangeSet changeSet) {
                    return new UnRunChangeSetInfo(changeSet);
                }
            });
        } catch (LiquibaseException e) {
            throw new MifosRuntimeException(e);
        }
    }
}
