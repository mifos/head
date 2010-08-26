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

package org.mifos.platform.questionnaire.mappers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.platform.questionnaire.migration.mappers.QuestionnaireMigrationMapper;
import org.mifos.platform.questionnaire.migration.mappers.QuestionnaireMigrationMapperImpl;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireMigrationMapperTest {

    private QuestionnaireMigrationMapper mapper;

    @Before
    public void setUp() {
        mapper = new QuestionnaireMigrationMapperImpl();
    }
    
    @Test
    public void shouldMapToQuestionDto() {
        CustomFieldDefinitionEntity customField = new CustomFieldDefinitionEntity("Favourite color", CustomerLevel.CLIENT.getValue(),
               CustomFieldType.ALPHA_NUMERIC, EntityType.CLIENT, "Red", YesNoFlag.YES);
        QuestionDto questionDto = mapper.map(customField, 0);
        assertThat(questionDto, is(notNullValue()));
        assertThat(questionDto.getTitle(), is("Favourite color"));
        assertThat(questionDto.getType(), is(QuestionType.FREETEXT));
        assertThat(questionDto.isMandatory(),is(true));
        assertThat(questionDto.getOrder(),is(0));
    }

    @Test
    public void shouldMapToQuestionGroupDto() {
        CustomFieldDefinitionEntity customField1 = new CustomFieldDefinitionEntity("CustomField1", CustomerLevel.CLIENT.getValue(),
               CustomFieldType.ALPHA_NUMERIC, EntityType.CLIENT, "Def1", YesNoFlag.YES);
        CustomFieldDefinitionEntity customField2 = new CustomFieldDefinitionEntity("CustomField2", CustomerLevel.CLIENT.getValue(),
               CustomFieldType.DATE, EntityType.CLIENT, "Def2", YesNoFlag.YES);
        CustomFieldDefinitionEntity customField3 = new CustomFieldDefinitionEntity("CustomField3", CustomerLevel.CLIENT.getValue(),
               CustomFieldType.NUMERIC, EntityType.CLIENT, "Def3", YesNoFlag.YES);
        QuestionGroupDto questionGroupDto = mapper.map(asList(customField1, customField2, customField3));
        assertThat(questionGroupDto, is(notNullValue()));
        assertThat(questionGroupDto.getTitle(), is("Additional Fields for Create Client"));
        EventSourceDto eventSourceDto = questionGroupDto.getEventSourceDto();
        assertThat(eventSourceDto, is(notNullValue()));
        assertThat(eventSourceDto.getEvent(), is("Create"));
        assertThat(eventSourceDto.getSource(), is("Client"));
        List<SectionDto> sections = questionGroupDto.getSections();
        assertThat(sections, is(notNullValue()));
        assertThat(sections.size(), is(1));
        SectionDto sectionDto = sections.get(0);
        assertThat(sectionDto.getName(), is("Misc"));
        assertThat(sectionDto.getOrder(), is(0));
        List<QuestionDto> questions = sectionDto.getQuestions();
        assertThat(questions, is(notNullValue()));
        assertQuestion(questions.get(0), "CustomField1", QuestionType.FREETEXT, 0);
        assertQuestion(questions.get(1), "CustomField2", QuestionType.DATE, 1);
        assertQuestion(questions.get(2), "CustomField3", QuestionType.NUMERIC, 2);
    }

    private void assertQuestion(QuestionDto questionDto, String title, QuestionType type, int order) {
        assertThat(questionDto.getTitle(), is(title));
        assertThat(questionDto.getType(), is(type));
        assertThat(questionDto.getOrder(), is(order));
    }
}
