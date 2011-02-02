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
import liquibase.changelog.ChangeSet;
import liquibase.exception.LiquibaseException;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DbUpgradeTest {
    public static final String CONTEXTS = "expansion";
    private DbUpgrade dbUpgrade;
    private String changeLog = "classpath:/change.xml";
    @Mock
    private DataSource dataSource;
    @Mock
    private ResourceLoader resourceLoader;
    @Mock
    private Connection connection;
    @Mock
    private Liquibase liquibase;
    @Mock
    private ChangeSet changeSet1;
    @Mock
    private ChangeSet changeSet2;

    @Before
    public void setUp() throws Exception {
        dbUpgrade = new DbUpgrade() {
            @Override
            Liquibase getLiquibase() throws SQLException, LiquibaseException {
                return liquibase;
            }
        };
        dbUpgrade.setChangeLog(changeLog);
        dbUpgrade.setDataSource(dataSource);
        dbUpgrade.setResourceLoader(resourceLoader);
        connection = null;
    }

    @Test
    public void testValidateWhenUnAppliedUpgradesExist() throws Exception {
        when(changeSet1.toString()).thenReturn("changeSet1");
        when(changeSet2.toString()).thenReturn("changeSet2");
        when(liquibase.listUnrunChangeSets(CONTEXTS)).thenReturn(Arrays.<ChangeSet>asList(changeSet1, changeSet2));
        DbUpgradeValidationResult dbUpgradeValidationResult = dbUpgrade.validate();
        assertFalse(dbUpgradeValidationResult.allUpgradesApplied());
        assertEquals("\nList of unapplied upgrades:\n" +
                "\tchangeSet1\n" +
                "\tchangeSet2\n", dbUpgradeValidationResult.getUnAppliedChangeSets());
        verify(liquibase).listUnrunChangeSets(CONTEXTS);
    }

    @Test
    public void testValidateWhenAllUpgradesAreApplied() throws Exception {
        when(liquibase.listUnrunChangeSets(CONTEXTS)).thenReturn(Arrays.<ChangeSet>asList());
        DbUpgradeValidationResult dbUpgradeValidationResult = dbUpgrade.validate();
        assertTrue(dbUpgradeValidationResult.allUpgradesApplied());
        assertEquals("\nList of unapplied upgrades:\n", dbUpgradeValidationResult.getUnAppliedChangeSets());
        verify(liquibase).listUnrunChangeSets(CONTEXTS);
    }

    @Test
    public void testUpgrade() throws Exception {
       dbUpgrade.upgrade();
       verify(liquibase).update(CONTEXTS);
    }

}
