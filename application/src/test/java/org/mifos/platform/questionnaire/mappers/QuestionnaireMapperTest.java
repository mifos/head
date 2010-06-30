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
import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.framework.components.fieldConfiguration.business.EntityMaster;
import org.mifos.platform.questionnaire.contract.*;
import org.mifos.platform.questionnaire.domain.EventEntity;
import org.mifos.platform.questionnaire.domain.EventSourceEntity;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.domain.Section;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.test.matchers.HasThisKindOfEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mifos.customers.surveys.helpers.AnswerType.FREETEXT;
import static org.mifos.platform.questionnaire.domain.QuestionGroupState.ACTIVE;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireMapperTest {
    private static final String TITLE = "Title";
    private QuestionnaireMapper questionnaireMapper;
    private static final String SECTION_NAME = "S1";
    private String SECTION = "section";

    @Mock
    private EventSourceDao eventSourceDao;

    @Before
    public void setUp() {
        questionnaireMapper = new QuestionnaireMapperImpl(eventSourceDao);
    }

    @Test
    public void shouldMapQuestionDefinitionToQuestion() {
        QuestionDefinition questionDefinition = new QuestionDefinition(TITLE, QuestionType.FREETEXT);
        Question question = questionnaireMapper.mapToQuestion(questionDefinition);
        assertThat(question.getAnswerTypeAsEnum(), is(FREETEXT));
        assertThat(question.getQuestionText(), is(TITLE));
    }

    @Test
    public void shouldMapQuestionToQuestionDetail() {
        Question question = getQuestion(TITLE, AnswerType.FREETEXT);
        QuestionDetail questionDetail = questionnaireMapper.mapToQuestionDetail(question);
        assertQuestionDetail(questionDetail, TITLE, QuestionType.FREETEXT);
    }

    @Test
    public void shouldMapQuestionsToQuestionDetails() {
        int countOfQuestions = 10;
        List<Question> questions = new ArrayList<Question>();
        for (int i = 0; i < countOfQuestions; i++) {
            questions.add(getQuestion(TITLE + i, AnswerType.FREETEXT));
        }
        List<QuestionDetail> questionDetails = questionnaireMapper.mapToQuestionDetails(questions);
        for (int i = 0; i < countOfQuestions; i++) {
            assertQuestionDetail(questionDetails.get(i), TITLE + i, QuestionType.FREETEXT);
        }
    }

    @Test
    public void shouldMapQuestionDetailWithVariousAnswerTypes() {
        assertQuestionType(QuestionType.INVALID, AnswerType.INVALID);
        assertQuestionType(QuestionType.FREETEXT, AnswerType.FREETEXT);
        assertQuestionType(QuestionType.NUMERIC, AnswerType.NUMBER);
        assertQuestionType(QuestionType.DATE, AnswerType.DATE);
    }

    @Test
    public void shouldMapQuestionGroupDefinitionToQuestionGroup() {
        when(eventSourceDao.retrieveByEventAndSource(anyString(), anyString())).thenReturn(new ArrayList());
        EventSource eventSource = getEventSource("Create", "Client");
        List<SectionDefinition> sectionDefinitions = asList(getSection(SECTION_NAME));
        QuestionGroupDefinition questionGroupDefinition = new QuestionGroupDefinition(TITLE, eventSource, sectionDefinitions);
        QuestionGroup questionGroup = questionnaireMapper.mapToQuestionGroup(questionGroupDefinition);
        assertThat(questionGroup, is(not(nullValue())));
        assertThat(questionGroup.getTitle(), is(TITLE));
        assertThat(questionGroup.getState(), is(ACTIVE));
        List<Section> sections = questionGroup.getSections();
        assertNotNull(sections);
        assertThat(sections.size(), is(1));
        assertThat(sections.get(0).getName(), is(SECTION_NAME));
        verifyCreationDate(questionGroup);
        verify(eventSourceDao, times(1)).retrieveByEventAndSource(anyString(), anyString());
    }

    private EventSource getEventSource(String event, String source) {
        return new EventSource(event, source, null);
    }

    private SectionDefinition getSection(String name) {
        SectionDefinition section = new SectionDefinition();
        section.setName(name);
        return section;
    }

    @Test
    public void shouldMapQuestionGroupToQuestionGroupDetail() {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setTitle(TITLE);
        questionGroup.setSections(asList(new Section("S1"), new Section("S2")));
        QuestionGroupDetail questionGroupDetail = questionnaireMapper.mapToQuestionGroupDetail(questionGroup);
        assertThat(questionGroupDetail, is(not(nullValue())));
        assertThat(questionGroupDetail.getTitle(), is(TITLE));
        assertNotNull(questionGroupDetail.getSectionDefinitions());
        assertThat(questionGroupDetail.getSectionDefinitions().size(), is(2));
        assertThat(questionGroupDetail.getSectionDefinitions().get(0).getName(), is("S1"));
        assertThat(questionGroupDetail.getSectionDefinitions().get(1).getName(), is("S2"));
    }

    @Test
    public void shouldMapQuestionGroupsToQuestionGroupDetails() {
        int countOfQuestions = 10;
        List<QuestionGroup> questionGroups = new ArrayList<QuestionGroup>();
        for (int i = 0; i < countOfQuestions; i++) {
            questionGroups.add(getQuestionGroup(TITLE + i, new Section(SECTION + i), new Section(SECTION + (i + 1))));
        }
        List<QuestionGroupDetail> questionGroupDetails = questionnaireMapper.mapToQuestionGroupDetails(questionGroups);
        assertThat(questionGroupDetails, is(notNullValue()));
        for (int i = 0; i < countOfQuestions; i++) {
            assertThat(questionGroupDetails.get(i).getTitle(), is(TITLE + i));
            assertThat(questionGroupDetails.get(i).getSectionDefinitions().get(0).getName(), is(SECTION + i));
            assertThat(questionGroupDetails.get(i).getSectionDefinitions().get(1).getName(), is(SECTION + (i + 1)));
        }
    }

    @Test
    public void shouldMapToEventSources() {
        List<EventSourceEntity> events = getEventSourceEntities("Create", "Client", "Create Client");
        List<EventSource> eventSources = questionnaireMapper.mapToEventSources(events);
        assertThat(eventSources, is(not(nullValue())));
        assertThat(eventSources, new HasThisKindOfEvent("Create", "Client", "Create Client"));
    }

    private List<EventSourceEntity> getEventSourceEntities(String event, String source, String description) {
        List<EventSourceEntity> events = new ArrayList<EventSourceEntity>();
        EventSourceEntity eventSourceEntity = new EventSourceEntity();
        eventSourceEntity.setDescription(description);
        EventEntity eventEntity = new EventEntity();
        eventEntity.setName(event);
        eventSourceEntity.setEvent(eventEntity);
        EntityMaster entityMaster = new EntityMaster();
        entityMaster.setEntityType(source);
        eventSourceEntity.setSource(entityMaster);
        events.add(eventSourceEntity);
        return events;
    }

    private QuestionGroup getQuestionGroup(String title, Section... sections) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setTitle(title);
        questionGroup.setSections(asList(sections));
        return questionGroup;
    }

    private void assertQuestionType(QuestionType questionType, AnswerType answerType) {
        QuestionDetail questionDetail = questionnaireMapper.mapToQuestionDetail(getQuestion(TITLE, answerType));
        assertThat(questionDetail.getType(), is(questionType));
    }

    private Question getQuestion(String title, AnswerType answerType) {
        return new Question(title, title, answerType);
    }

    private void assertQuestionDetail(QuestionDetail questionDetail, String title, QuestionType questionType) {
        assertThat(questionDetail.getText(), is(title));
        assertThat(questionDetail.getType(), is(questionType));
    }

    private void verifyCreationDate(QuestionGroup questionGroup) {
        Calendar creationDate = Calendar.getInstance();
        creationDate.setTime(questionGroup.getDateOfCreation());
        Calendar currentDate = Calendar.getInstance();
        assertThat(creationDate.get(Calendar.DATE), is(currentDate.get(Calendar.DATE)));
        assertThat(creationDate.get(Calendar.MONTH), is(currentDate.get(Calendar.MONTH)));
        assertThat(creationDate.get(Calendar.YEAR), is(currentDate.get(Calendar.YEAR)));
    }
}
