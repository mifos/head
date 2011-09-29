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
import liquibase.exception.LiquibaseException;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseUpgradeSupportTest {
    public static final String EXPANSION = "expansion";
    public static final String CONTRACTION = "contraction";
    private DatabaseUpgradeSupport databaseUpgradeSupport;
    @Mock
    private Liquibase liquibase;
    @Mock
    private ChangeSet changeSet1;
    @Mock
    private ChangeSet changeSet2;
    @Mock
    private Database database;

    @Before
    public void setUp() throws Exception {
        databaseUpgradeSupport = new DatabaseUpgradeSupport(database, liquibase);
    }

    @Test
    public void testValidateWhenUnAppliedUpgradesExist() throws Exception {
        when(changeSet1.toString()).thenReturn("changeSet1");
        when(changeSet2.toString()).thenReturn("changeSet2");
        when(liquibase.listUnrunChangeSets(EXPANSION)).thenReturn(Arrays.<ChangeSet>asList(changeSet1, changeSet2));
        DbUpgradeValidationResult dbUpgradeValidationResult = databaseUpgradeSupport.validate();
        assertFalse(dbUpgradeValidationResult.allUpgradesApplied());
        assertEquals("\nList of unapplied upgrades:\n" +
                "\tchangeSet1\n" +
                "\tchangeSet2\n", dbUpgradeValidationResult.getUnAppliedChangeSets());
        verify(liquibase).listUnrunChangeSets(EXPANSION);
    }

    @Test
    public void testValidateWhenAllUpgradesAreApplied() throws Exception {
        when(liquibase.listUnrunChangeSets(EXPANSION)).thenReturn(Arrays.<ChangeSet>asList());
        DbUpgradeValidationResult dbUpgradeValidationResult = databaseUpgradeSupport.validate();
        assertTrue(dbUpgradeValidationResult.allUpgradesApplied());
        assertEquals("\nList of unapplied upgrades:\n", dbUpgradeValidationResult.getUnAppliedChangeSets());
        verify(liquibase).listUnrunChangeSets(EXPANSION);
    }

    @Test
    public void testUpgrade() throws Exception {
        databaseUpgradeSupport.expansion();
        verify(liquibase).update(EXPANSION);
    }

    @Test
    public void shouldRetrieveAllRanUpgrades() throws Exception {
        RanChangeSet ranChangeSet1 = new RanChangeSet(changeSet1);
        RanChangeSet ranChangeSet2 = new RanChangeSet(changeSet2);
        when(database.getRanChangeSetList()).thenReturn(Arrays.asList(ranChangeSet1, ranChangeSet2));
        List<ChangeSetInfo> changeSets = databaseUpgradeSupport.listRanUpgrades();
        assertThat(changeSets, is(not(nullValue())));
        assertThat(changeSets.size(), is(2));
        verify(database).getRanChangeSetList();
    }

    @Test
    public void listUnrunChangeSets() throws LiquibaseException {
        when(changeSet1.getId()).thenReturn("id1");
        when(changeSet1.getAuthor()).thenReturn("author1");
        Set<String> contextSet = new HashSet<String>();
        contextSet.add(EXPANSION);
        when(changeSet1.getContexts()).thenReturn(contextSet);
        when(liquibase.listUnrunChangeSets(StringUtils.EMPTY)).thenReturn(Arrays.<ChangeSet>asList(changeSet1));
        List<UnRunChangeSetInfo> unRunChangeSetInfo = databaseUpgradeSupport.listUnRunChangeSets();
        verify(liquibase).listUnrunChangeSets(StringUtils.EMPTY);
        UnRunChangeSetInfo unRunChangeSetInfo1 = unRunChangeSetInfo.get(0);
        assertThat(unRunChangeSetInfo1.getId(), is("id1"));
        assertThat(unRunChangeSetInfo1.getAuthor(), is("author1"));
        assertThat(unRunChangeSetInfo1.getContexts(), is("["+EXPANSION+"]"));
    }


}
