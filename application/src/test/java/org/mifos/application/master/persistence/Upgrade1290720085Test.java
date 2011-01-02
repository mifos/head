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

package org.mifos.application.master.persistence;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.questionnaire.migration.QuestionnaireMigration;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class Upgrade1290720085Test {

    @Mock
    private QuestionnaireMigration questionnaireMigration;

    @Mock
    private SurveysPersistence surveysPersistence;

    @Mock
    private CustomerDao customerDao;

    private Upgrade1290720085 upgrade1290720085;

    @Before
    public void setUp() {
        upgrade1290720085 = new Upgrade1290720085(questionnaireMigration);
    }

    @Test
    public void shouldMigrateViewClientSurveys() throws PersistenceException, IOException, SQLException {
        List<Integer> qgIds = asList(1111, 2222);
        when(questionnaireMigration.migrateSurveys()).thenReturn(qgIds);
        List<Integer> questionGroupIds = upgrade1290720085.migrateSurveys();
        assertThat(questionGroupIds, is(qgIds));
        verify(questionnaireMigration).migrateSurveys();
    }

    /* this test doesn't make sense
    @Test
    public void shouldMigrateAdditionalFields() throws IOException, SQLException {
        when(questionnaireMigration.migrateAdditionalFields()).thenReturn(asList(3333));
        List<Integer> questionGroupIds = upgrade1290720085.migrateAdditionalFields();
        assertThat(questionGroupIds, is(notNullValue()));
        assertThat(questionGroupIds.size(), is(1));
        assertThat(questionGroupIds.get(0), is(3333));
        verify(questionnaireMigration).migrateAdditionalFields();
    }*/
}
