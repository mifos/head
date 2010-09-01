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

package org.mifos.application.questionnaire.migration;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.domain.Section;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.DEFAULT_SECTION_NAME;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml", "/org/mifos/config/resources/applicationContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
public class QuestionnaireMigrationIntegrationTest {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private QuestionnaireMigration questionnaireMigration;

    @Autowired
    private QuestionGroupDao questionGroupDao;

    @Ignore("Ignored because of lack of test data & viable means to create it")
    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldMigrateAllAdditionalFieldsForClientEntity() {
        List<CustomFieldDefinitionEntity> customFields = customerDao.retrieveCustomFieldEntitiesForClient();
        assertThat(customFields, is(notNullValue()));
        int size = customFields.size();
        assertThat(size > 0, is(true));
        Integer questionGroupId = questionnaireMigration.migrate(customFields);
        assertThat(questionGroupId, is(notNullValue()));
        QuestionGroup questionGroup = questionGroupDao.getDetails(questionGroupId);
        assertThat(questionGroup, is(notNullValue()));
        assertThat(questionGroup.getTitle(), is("Additional Fields for Create Client"));
        assertThat(questionGroup.getSections(),is(notNullValue()));
        Section section = questionGroup.getSections().get(0);
        assertThat(section.getName(), is(DEFAULT_SECTION_NAME));
        assertThat(section.getQuestions().size(), is(size));
    }
}