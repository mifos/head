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

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.business.EntityMaster;
import org.mifos.platform.questionnaire.domain.AnswerType;
import org.mifos.platform.questionnaire.domain.EventEntity;
import org.mifos.platform.questionnaire.domain.EventSourceEntity;
import org.mifos.platform.questionnaire.domain.QuestionChoiceEntity;
import org.mifos.platform.questionnaire.domain.QuestionEntity;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.domain.QuestionGroupInstance;
import org.mifos.platform.questionnaire.domain.QuestionGroupResponse;
import org.mifos.platform.questionnaire.domain.QuestionGroupState;
import org.mifos.platform.questionnaire.domain.Section;
import org.mifos.platform.questionnaire.domain.SectionQuestion;
import org.mifos.platform.questionnaire.matchers.EventSourcesMatcher;
import org.mifos.platform.questionnaire.matchers.QuestionChoicesMatcher;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.mifos.platform.questionnaire.persistence.SectionQuestionDao;
import org.mifos.platform.questionnaire.service.EventSource;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD")
public class QuestionnaireMapperTest {
    private static final String TITLE = "Title";
    private QuestionnaireMapper questionnaireMapper;
    private static final String SECTION_NAME = "S1";
    private String SECTION = "section";

    @Mock
    private EventSourceDao eventSourceDao;

    @Mock
    private QuestionDao questionDao;
    @Mock
    private QuestionGroupDao questionGroupDao;
    @Mock
    private SectionQuestionDao sectionQuestionDao;

    @Before
    public void setUp() {
        questionnaireMapper = new QuestionnaireMapperImpl(eventSourceDao, questionDao, questionGroupDao, sectionQuestionDao);
    }

    @Test
    public void shouldMapQuestionDetailToQuestion() {
        QuestionDetail questionDefinition = new QuestionDetail(TITLE, QuestionType.FREETEXT);
        QuestionEntity question = questionnaireMapper.mapToQuestion(questionDefinition);
        assertThat(question.getAnswerTypeAsEnum(), CoreMatchers.is(AnswerType.FREETEXT));
        assertThat(question.getQuestionText(), is(TITLE));
    }

    @Test
    public void shouldMapMultiSelectQuestionDetailToQuestion() {
        String choice1 = "choice1";
        String choice2 = "choice2";
        QuestionDetail questionDefinition = new QuestionDetail(TITLE, QuestionType.MULTI_SELECT, Arrays.asList(choice1, choice2));
        QuestionEntity question = questionnaireMapper.mapToQuestion(questionDefinition);
        assertThat(question.getAnswerTypeAsEnum(), is(AnswerType.MULTISELECT));
        assertThat(question.getQuestionText(), is(TITLE));
        assertThat(question.getShortName(), is(TITLE));
        assertThat(question.getChoices(), new QuestionChoicesMatcher(Arrays.asList(new QuestionChoiceEntity(choice1), new QuestionChoiceEntity(choice2))));
    }

    @Test
    public void shouldMapSingleSelectQuestionDetailToQuestion() {
        String choice1 = "choice1";
        String choice2 = "choice2";
        QuestionDetail questionDefinition = new QuestionDetail(TITLE, QuestionType.SINGLE_SELECT, Arrays.asList(choice1, choice2));
        QuestionEntity question = questionnaireMapper.mapToQuestion(questionDefinition);
        assertThat(question.getAnswerTypeAsEnum(), is(AnswerType.SINGLESELECT));
        assertThat(question.getQuestionText(), is(TITLE));
        assertThat(question.getShortName(), is(TITLE));
        assertThat(question.getChoices(), new QuestionChoicesMatcher(Arrays.asList(new QuestionChoiceEntity(choice1), new QuestionChoiceEntity(choice2))));
    }

    @Test
    public void shouldMapQuestionToQuestionDetail() {
        QuestionEntity question = getQuestion(TITLE, AnswerType.FREETEXT);
        QuestionDetail questionDetail = questionnaireMapper.mapToQuestionDetail(question);
        assertQuestionDetail(questionDetail, TITLE, QuestionType.FREETEXT);

        question = getQuestion(TITLE, AnswerType.MULTISELECT, Arrays.asList(new QuestionChoiceEntity("choice1"), new QuestionChoiceEntity("choice2")));
        questionDetail = questionnaireMapper.mapToQuestionDetail(question);
        assertQuestionDetail(questionDetail, TITLE, QuestionType.MULTI_SELECT, Arrays.asList("choice1", "choice2"));
    }

    @Test
    public void shouldMapQuestionsToQuestionDetails() {
        int countOfQuestions = 10;
        List<QuestionEntity> questions = new ArrayList<QuestionEntity>();
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
        Mockito.when(eventSourceDao.retrieveByEventAndSource(Matchers.anyString(), Matchers.anyString())).thenReturn(new ArrayList());
        Mockito.when(questionDao.getDetails(12)).thenReturn(new QuestionEntity());
        EventSource eventSource = getEventSource("Create", "Client");
        List<SectionDetail> sectionDetails = Arrays.asList(getSectionDefinition(SECTION_NAME));
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(0, TITLE, eventSource, sectionDetails, true);
        QuestionGroup questionGroup = questionnaireMapper.mapToQuestionGroup(questionGroupDetail);
        assertQuestionGroup(questionGroup);
        assertThat(questionGroup.isEditable(), is(true));
        Mockito.verify(eventSourceDao, Mockito.times(1)).retrieveByEventAndSource(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(questionDao, Mockito.times(1)).getDetails(12);
    }

    private void assertQuestionGroup(QuestionGroup questionGroup) {
        assertThat(questionGroup, notNullValue());
        assertThat(questionGroup.getTitle(), is(TITLE));
        assertThat(questionGroup.getState(), is(QuestionGroupState.ACTIVE));
        assertSections(questionGroup.getSections());
        assertCreationDate(questionGroup.getDateOfCreation());
    }

    private void assertSections(List<Section> sections) {
        assertThat(sections, notNullValue());
        assertThat(sections.size(), is(1));
        Section section = sections.get(0);
        assertThat(section.getName(), is(SECTION_NAME));
        assertSectionQuestions(section.getQuestions());
    }

    private void assertSectionQuestions(List<SectionQuestion> sectionQuestions) {
        assertThat(sectionQuestions, notNullValue());
        assertThat(sectionQuestions.size(), is(1));
        SectionQuestion sectionQuestion = sectionQuestions.get(0);
        assertThat(sectionQuestion.getQuestion(), notNullValue());
        assertThat(sectionQuestion.getSection(), notNullValue());
        assertThat(sectionQuestion.isRequired(), is(true));
        assertThat(sectionQuestion.getSequenceNumber(), is(0));
    }

    private SectionDetail getSectionDefinition(String name) {
        SectionDetail section = new SectionDetail();
        section.setName(name);
        section.addQuestion(new SectionQuestionDetail(new QuestionDetail(12, null, null, QuestionType.INVALID), true));
        return section;
    }

    @Test
    public void shouldMapQuestionGroupToQuestionGroupDetail() {
        QuestionGroup questionGroup = getQuestionGroup("Create", "Client", "S1", "S2");
        QuestionGroupDetail questionGroupDetail = questionnaireMapper.mapToQuestionGroupDetail(questionGroup);
        assertThat(questionGroupDetail, is(notNullValue()));
        assertThat(questionGroupDetail.getTitle(), is(TITLE));
        List<SectionDetail> sectionDetails = questionGroupDetail.getSectionDetails();
        assertThat(sectionDetails, is(notNullValue()));
        assertThat(questionGroupDetail.getSectionDetails().size(), is(2));
        assertThat(questionGroupDetail.getSectionDetails().get(0).getName(), is("S1"));
        assertThat(questionGroupDetail.getSectionDetails().get(1).getName(), is("S2"));
        EventSource eventSource = questionGroupDetail.getEventSource();
        assertThat(eventSource, is(notNullValue()));
        assertThat(eventSource.getEvent(), is("Create"));
        assertThat(eventSource.getSource(), is("Client"));
    }

    private QuestionGroup getQuestionGroup(String event, String source, String... sectionNames) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setTitle(TITLE);
        questionGroup.setSections(getSections(sectionNames));
        questionGroup.setEventSources(getEventSources(event, source));
        return questionGroup;
    }

    private List<Section> getSections(String[] sectionNames) {
        List<Section> sections = new ArrayList<Section>();
        for (String sectionName : sectionNames) {
            sections.add(getSection(sectionName));
        }
        return sections;
    }

    private Section getSection(String sectionName) {
        Section section = new Section(sectionName);
        SectionQuestion sectionQuestion = new SectionQuestion();
        QuestionEntity question = new QuestionEntity();
        question.setShortName(sectionName);
        question.setAnswerType(AnswerType.DATE);
        sectionQuestion.setQuestion(question);
        section.setQuestions(Arrays.asList(sectionQuestion));
        return section;
    }

    private HashSet<EventSourceEntity> getEventSources(String event, String source) {
        EventSourceEntity eventSourceEntity = new EventSourceEntity();
        EventEntity eventEntity = new EventEntity();
        eventEntity.setName(event);
        eventSourceEntity.setEvent(eventEntity);
        EntityMaster entityMaster = new EntityMaster();
        entityMaster.setEntityType(source);
        eventSourceEntity.setSource(entityMaster);

        HashSet<EventSourceEntity> eventSources = new HashSet<EventSourceEntity>();
        eventSources.add(eventSourceEntity);
        return eventSources;
        //return Collections.singleton(eventSourceEntity);
    }

    @Test
    public void shouldMapQuestionGroupsToQuestionGroupDetails() {
        int countOfQuestions = 10;
        List<QuestionGroup> questionGroups = new ArrayList<QuestionGroup>();
        for (int i = 0; i < countOfQuestions; i++) {
            questionGroups.add(getQuestionGroup(TITLE + i, getSection(SECTION + i), getSection(SECTION + (i + 1))));
        }
        List<QuestionGroupDetail> questionGroupDetails = questionnaireMapper.mapToQuestionGroupDetails(questionGroups);
        assertThat(questionGroupDetails, is(notNullValue()));
        for (int i = 0; i < countOfQuestions; i++) {
            QuestionGroupDetail questionGroupDetail = questionGroupDetails.get(i);
            assertThat(questionGroupDetail.getTitle(), is(TITLE + i));
            SectionDetail sectionDefinition1 = questionGroupDetail.getSectionDetails().get(0);
            assertThat(sectionDefinition1.getName(), is(SECTION + i));
            List<SectionQuestionDetail> questionDetails1 = sectionDefinition1.getQuestions();
            assertThat(questionDetails1.size(), is(1));
            assertThat(questionDetails1.get(0).getTitle(), is(SECTION + i));
            assertThat(questionDetails1.get(0).getQuestionType(), is(QuestionType.DATE));
            SectionDetail sectionDefinition2 = questionGroupDetail.getSectionDetails().get(1);
            assertThat(sectionDefinition2.getName(), is(SECTION + (i + 1)));
            List<SectionQuestionDetail> questionDetails2 = sectionDefinition2.getQuestions();
            assertThat(questionDetails2.size(), is(1));
            assertThat(questionDetails2.get(0).getTitle(), is(SECTION + (i + 1)));
            assertThat(questionDetails2.get(0).getQuestionType(), is(QuestionType.DATE));
        }
    }

    @Test
    public void shouldMapToEventSources() {
        List<EventSourceEntity> events = getEventSourceEntities("Create", "Client", "Create Client");
        List<EventSource> eventSources = questionnaireMapper.mapToEventSources(events);
        assertThat(eventSources, is(notNullValue()));
        assertThat(eventSources, new EventSourcesMatcher(Arrays.asList(getEventSource("Create", "Client", "Create Client"))));
    }

    @Test
    public void shouldMapToQuestionGroupInstances() {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setId(10);
        Mockito.when(questionGroupDao.getDetails(10)).thenReturn(questionGroup);

        SectionQuestion sectionQuestion = new SectionQuestion();
        sectionQuestion.setId(14);
        Mockito.when(sectionQuestionDao.getDetails(14)).thenReturn(sectionQuestion);
        
        List<QuestionDetail> questionDetails = Arrays.asList(new QuestionDetail(12, "Question 1", "Question 1", QuestionType.FREETEXT));
        List<SectionDetail> sectionDetails = Arrays.asList(getSectionDetailWithQuestions("Sec1", questionDetails));
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(10, "QG1", new EventSource("Create", "Client", null), sectionDetails, true);
        List<QuestionGroupInstance> questionGroupInstances =
                questionnaireMapper.mapToQuestionGroupInstances(new QuestionGroupDetails(101, 201, Arrays.asList(questionGroupDetail)));
        assertThat(questionGroupInstances, is(notNullValue()));
        assertThat(questionGroupInstances.size(), is(1));
        QuestionGroupInstance questionGroupInstance = questionGroupInstances.get(0);
        assertThat(questionGroupInstance.getQuestionGroup().getId(), is(10));
        assertThat(questionGroupInstance.getCompletedStatus(), is(1));
        assertThat(questionGroupInstance.getCreatorId(), is(101));
        assertThat(questionGroupInstance.getDateConducted(), is(notNullValue()));
        assertThat(questionGroupInstance.getEntityId(), is(201));
        assertThat(questionGroupInstance.getVersionNum(), is(1));
        List<QuestionGroupResponse> questionGroupResponses = questionGroupInstance.getQuestionGroupResponses();
        assertThat(questionGroupResponses, is(notNullValue()));
        assertThat(questionGroupResponses.size(), is(1));
        QuestionGroupResponse questionGroupResponse = questionGroupResponses.get(0);
        assertThat(questionGroupResponse.getResponse(), is("value"));
        assertThat(questionGroupResponse.getSectionQuestion().getId(), is(14));
    }

    private SectionDetail getSectionDetailWithQuestions(String name, List<QuestionDetail> questionDetails) {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName(name);
        List<SectionQuestionDetail> sectionQuestionDetails = new ArrayList<SectionQuestionDetail>();
        for (QuestionDetail questionDetail : questionDetails) {
            SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail(questionDetail, false);
            sectionQuestionDetail.setValue("value");
            sectionQuestionDetail.setId(14);
            sectionQuestionDetails.add(sectionQuestionDetail);
        }
        sectionDetail.setQuestionDetails(sectionQuestionDetails);
        return sectionDetail;
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
        questionGroup.setSections(Arrays.asList(sections));
        return questionGroup;
    }

    private void assertQuestionType(QuestionType questionType, AnswerType answerType) {
        QuestionDetail questionDetail = questionnaireMapper.mapToQuestionDetail(getQuestion(TITLE, answerType));
        assertThat(questionDetail.getType(), is(questionType));
    }

    private void assertCreationDate(Date dateOfCreation) {
        Calendar creationDate = Calendar.getInstance();
        creationDate.setTime(dateOfCreation);
        Calendar currentDate = Calendar.getInstance();
        assertThat(creationDate.get(Calendar.DATE), is(currentDate.get(Calendar.DATE)));
        assertThat(creationDate.get(Calendar.MONTH), is(currentDate.get(Calendar.MONTH)));
        assertThat(creationDate.get(Calendar.YEAR), is(currentDate.get(Calendar.YEAR)));
    }

    private QuestionEntity getQuestion(String title, AnswerType answerType) {
        return getQuestion(title, answerType, new LinkedList<QuestionChoiceEntity>());
    }

    private QuestionEntity getQuestion(String title, AnswerType answerType, List<QuestionChoiceEntity> questionChoices) {
        return new QuestionEntity(title, title, answerType, questionChoices);
    }

    private void assertQuestionDetail(QuestionDetail questionDetail, String title, QuestionType questionType) {
        assertThat(questionDetail.getText(), is(title));
        assertThat(questionDetail.getType(), is(questionType));
    }

    private void assertQuestionDetail(QuestionDetail questionDetail, String title, QuestionType questionType, List<String> choices) {
        assertQuestionDetail(questionDetail, title, questionType);
        Assert.assertEquals(choices, questionDetail.getAnswerChoices());
    }

    private EventSource getEventSource(String event, String source) {
        return new EventSource(event, source, null);
    }

    private EventSource getEventSource(String event, String source, String description) {
        return new EventSource(event, source, description);
    }

}

