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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.questionnaire.migration.mappers.QuestionnaireMigrationMapper;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireMigrationTest {

    @Mock
    private QuestionnaireMigrationMapper questionnaireMigrationMapper;

    @Mock
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    private QuestionnaireMigration questionnaireMigration;

    private static final int QUESTION_GROUP_ID = 123;

    @Before
    public void setUp() {
        questionnaireMigration = new QuestionnaireMigration(questionnaireMigrationMapper, questionnaireServiceFacade);
    }

    @Test
    public void shouldMapCustomFields() {
        List<CustomFieldDefinitionEntity> customFields = getCustomFields();
        QuestionGroupDto questionGroupDto = new QuestionGroupDto();
        when(questionnaireMigrationMapper.map(customFields)).thenReturn(questionGroupDto);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDto)).thenReturn(QUESTION_GROUP_ID);
        Integer questionGroupId = questionnaireMigration.migrate(customFields);
        assertThat(questionGroupId, is(QUESTION_GROUP_ID));
        verify(questionnaireMigrationMapper).map(customFields);
        verify(questionnaireServiceFacade).createQuestionGroup(questionGroupDto);
    }

    private List<CustomFieldDefinitionEntity> getCustomFields() {
        CustomFieldDefinitionEntity customField1 = new CustomFieldDefinitionEntity("CustomField1", CustomerLevel.CLIENT.getValue(),
               CustomFieldType.ALPHA_NUMERIC, EntityType.CLIENT, "Def1", YesNoFlag.YES);
        CustomFieldDefinitionEntity customField2 = new CustomFieldDefinitionEntity("CustomField2", CustomerLevel.CLIENT.getValue(),
               CustomFieldType.DATE, EntityType.CLIENT, "Def2", YesNoFlag.YES);
        CustomFieldDefinitionEntity customField3 = new CustomFieldDefinitionEntity("CustomField3", CustomerLevel.CLIENT.getValue(),
               CustomFieldType.NUMERIC, EntityType.CLIENT, "Def3", YesNoFlag.YES);
        return asList(customField1, customField2, customField3);
    }
}