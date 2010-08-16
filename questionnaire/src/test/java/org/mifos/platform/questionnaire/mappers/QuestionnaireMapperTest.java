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
import org.mifos.platform.questionnaire.builders.ChoiceDetailBuilder;
import org.mifos.platform.questionnaire.builders.QuestionDtoBuilder;
import org.mifos.platform.questionnaire.builders.QuestionGroupDtoBuilder;
import org.mifos.platform.questionnaire.builders.SectionDtoBuilder;
import org.mifos.platform.questionnaire.domain.AnswerType;
import org.mifos.platform.questionnaire.domain.ChoiceTagEntity;
import org.mifos.platform.questionnaire.domain.EventEntity;
import org.mifos.platform.questionnaire.domain.EventSourceEntity;
import org.mifos.platform.questionnaire.domain.QuestionChoiceEntity;
import org.mifos.platform.questionnaire.domain.QuestionEntity;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.domain.QuestionGroupInstance;
import org.mifos.platform.questionnaire.domain.QuestionGroupResponse;
import org.mifos.platform.questionnaire.domain.QuestionGroupState;
import org.mifos.platform.questionnaire.domain.QuestionState;
import org.mifos.platform.questionnaire.domain.Section;
import org.mifos.platform.questionnaire.domain.SectionQuestion;
import org.mifos.platform.questionnaire.matchers.EventSourcesMatcher;
import org.mifos.platform.questionnaire.matchers.QuestionChoicesMatcher;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupInstanceDao;
import org.mifos.platform.questionnaire.persistence.SectionQuestionDao;
import org.mifos.platform.questionnaire.service.ChoiceDetail;
import org.mifos.platform.questionnaire.service.EventSource;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireMapperTest {
    private static final String TITLE = "Title";
    private QuestionnaireMapper questionnaireMapper;
    private static final String SECTION_NAME = "S1";
    private static final String SECTION = "section";

    @Mock
    private EventSourceDao eventSourceDao;

    @Mock
    private QuestionDao questionDao;
    @Mock
    private QuestionGroupDao questionGroupDao;
    @Mock
    private SectionQuestionDao sectionQuestionDao;
    @Mock
    private QuestionGroupInstanceDao questionGroupInstanceDao;

    @Before
    public void setUp() {
        questionnaireMapper = new QuestionnaireMapperImpl(eventSourceDao, questionDao, questionGroupDao, sectionQuestionDao, questionGroupInstanceDao);
    }

    @Test
    public void shouldMapQuestionDetailToQuestion() {
        QuestionDetail questionDefinition = new QuestionDetail(TITLE, QuestionType.FREETEXT);
        questionDefinition.setActive(false);
        QuestionEntity question = questionnaireMapper.mapToQuestion(questionDefinition);
        assertThat(question.getAnswerTypeAsEnum(), CoreMatchers.is(AnswerType.FREETEXT));
        assertThat(question.getQuestionText(), is(TITLE));
        assertThat(question.getQuestionStateAsEnum(), is(QuestionState.INACTIVE));
    }

    @Test
    public void shouldMapMultiSelectQuestionDetailToQuestion() {
        ChoiceDetail choice1 = new ChoiceDetail("choice1");
        ChoiceDetail choice2 = new ChoiceDetail("choice2");
        QuestionDetail questionDefinition = new QuestionDetail(TITLE, QuestionType.MULTI_SELECT);
        questionDefinition.setAnswerChoices(asList(choice1, choice2));
        QuestionEntity question = questionnaireMapper.mapToQuestion(questionDefinition);
        assertThat(question.getAnswerTypeAsEnum(), is(AnswerType.MULTISELECT));
        assertThat(question.getQuestionText(), is(TITLE));
        assertThat(question.getShortName(), is(TITLE));
        assertThat(question.getChoices(), new QuestionChoicesMatcher(asList(new QuestionChoiceEntity(choice1.getValue()), new QuestionChoiceEntity(choice2.getValue()))));
        assertThat(question.getQuestionStateAsEnum(), is(QuestionState.ACTIVE));
    }

    @Test
    public void shouldMapSmartSelectQuestionDetailToQuestion() {
        ChoiceDetail choice1 = new ChoiceDetail("choice1");
        choice1.setTags(asList("Tag1", "Tag2"));
        ChoiceDetail choice2 = new ChoiceDetail("choice2");
        choice2.setTags(asList("Tag3"));
        QuestionDetail questionDefinition = new QuestionDetail(TITLE, QuestionType.SMART_SELECT);
        questionDefinition.setActive(true);
        questionDefinition.setAnswerChoices(asList(choice1, choice2));
        QuestionEntity question = questionnaireMapper.mapToQuestion(questionDefinition);
        assertThat(question.getAnswerTypeAsEnum(), is(AnswerType.SMARTSELECT));
        assertThat(question.getQuestionText(), is(TITLE));
        assertThat(question.getShortName(), is(TITLE));
        assertThat(question.getChoices(), new QuestionChoicesMatcher(asList(getChoiceEntity("choice1", "Tag1", "Tag2"), getChoiceEntity("choice2", "Tag3"))));
        assertThat(question.getQuestionStateAsEnum(), is(QuestionState.ACTIVE));
    }

    private QuestionChoiceEntity getChoiceEntity(String choiceText, String... tagTexts) {
        QuestionChoiceEntity choiceEntity = new QuestionChoiceEntity(choiceText);
        Set<ChoiceTagEntity> tags = new HashSet<ChoiceTagEntity>();
        for (String tagText : tagTexts) {
            ChoiceTagEntity choiceTagEntity = new ChoiceTagEntity();
            choiceTagEntity.setTagText(tagText);
            tags.add(choiceTagEntity);
        }
        choiceEntity.setTags(tags);
        return choiceEntity;
    }

    @Test
    public void shouldMapSingleSelectQuestionDetailToQuestion() {
        ChoiceDetail choice1 = new ChoiceDetail("choice1");
        ChoiceDetail choice2 = new ChoiceDetail("choice2");
        QuestionDetail questionDefinition = new QuestionDetail(TITLE, QuestionType.SINGLE_SELECT);
        questionDefinition.setAnswerChoices(asList(choice1, choice2));
        QuestionEntity question = questionnaireMapper.mapToQuestion(questionDefinition);
        assertThat(question.getAnswerTypeAsEnum(), is(AnswerType.SINGLESELECT));
        assertThat(question.getQuestionText(), is(TITLE));
        assertThat(question.getShortName(), is(TITLE));
        assertThat(question.getChoices(), new QuestionChoicesMatcher(asList(new QuestionChoiceEntity(choice1.getValue()), new QuestionChoiceEntity(choice2.getValue()))));
    }

    @Test
    public void shouldMapNumericQuestionDetailToQuestion() {
        QuestionDetail questionDetail = new QuestionDetail(TITLE, QuestionType.NUMERIC);
        questionDetail.setNumericMin(10);
        questionDetail.setNumericMax(100);
        QuestionEntity questionEntity = questionnaireMapper.mapToQuestion(questionDetail);
        assertThat(questionEntity, is(notNullValue()));
        assertThat(questionEntity.getQuestionText(), is(TITLE));
        assertThat(questionEntity.getAnswerTypeAsEnum(), is(AnswerType.NUMBER));
        assertThat(questionEntity.getNumericMin(), is(10));
        assertThat(questionEntity.getNumericMax(), is(100));
    }

    @Test
    public void shouldMapQuestionToQuestionDetail() {
        QuestionEntity question = getQuestion(TITLE, AnswerType.FREETEXT);
        QuestionDetail questionDetail = questionnaireMapper.mapToQuestionDetail(question);
        assertQuestionDetail(questionDetail, TITLE, QuestionType.FREETEXT);

        question = getQuestion(TITLE, AnswerType.MULTISELECT, asList(new QuestionChoiceEntity("choice1"), new QuestionChoiceEntity("choice2")));
        questionDetail = questionnaireMapper.mapToQuestionDetail(question);
        assertQuestionDetail(questionDetail, TITLE, QuestionType.MULTI_SELECT, asList("choice1", "choice2"));

        question = getQuestion(TITLE, AnswerType.NUMBER);
        question.setNumericMin(10);
        question.setNumericMax(100);
        questionDetail = questionnaireMapper.mapToQuestionDetail(question);
        assertQuestionDetail(questionDetail, TITLE, QuestionType.NUMERIC);
        assertThat(questionDetail.getNumericMin(), is(10));
        assertThat(questionDetail.getNumericMax(), is(100));
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
        when(eventSourceDao.retrieveByEventAndSource(Matchers.anyString(), Matchers.anyString())).thenReturn(new ArrayList());
        when(questionDao.getDetails(12)).thenReturn(new QuestionEntity());
        EventSource eventSource = getEventSource("Create", "Client");
        List<SectionDetail> sectionDetails = asList(getSectionDefinition(SECTION_NAME));
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(0, TITLE, eventSource, sectionDetails, true);
        QuestionGroup questionGroup = questionnaireMapper.mapToQuestionGroup(questionGroupDetail);
        assertQuestionGroup(questionGroup);
        assertThat(questionGroup.isEditable(), is(true));
        verify(eventSourceDao, times(1)).retrieveByEventAndSource(Matchers.anyString(), Matchers.anyString());
        verify(questionDao, times(1)).getDetails(12);
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
        section.addQuestion(new SectionQuestionDetail(new QuestionDetail(12, null, null, QuestionType.INVALID, true), true));
        return section;
    }

    @Test
    public void shouldMapQuestionGroupToQuestionGroupDetail() {
        QuestionGroup questionGroup = getQuestionGroup("Create", "Client", "S1", "S2");
        QuestionGroupDetail questionGroupDetail = questionnaireMapper.mapToQuestionGroupDetail(questionGroup);
        assertThat(questionGroupDetail, is(notNullValue()));
        assertThat(questionGroupDetail.getTitle(), is(TITLE));
        assertThat(questionGroupDetail.isEditable(), is(true));
        assertThat(questionGroupDetail.isActive(), is(true));
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
        questionGroup.setEditable(true);
        questionGroup.setState(QuestionGroupState.ACTIVE);
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
        question.setChoices(new LinkedList<QuestionChoiceEntity>());
        sectionQuestion.setQuestion(question);
        section.setQuestions(asList(sectionQuestion));
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
    }

    @Test
    public void shouldMapQuestionGroupsToQuestionGroupDetails() {
        int countOfQuestions = 10;
        List<QuestionGroup> questionGroups = new ArrayList<QuestionGroup>();
        for (int i = 0; i < countOfQuestions; i++) {
            questionGroups.add(getQuestionGroup(1991, TITLE + i, getSection(SECTION + i), getSection(SECTION + (i + 1))));
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
        assertThat(eventSources, new EventSourcesMatcher(asList(getEventSource("Create", "Client", "Create Client"))));
    }

    @Test
    public void shouldMapToQuestionGroupInstances() {
        QuestionGroup questionGroup1 = new QuestionGroup();
        questionGroup1.setId(10);
        when(questionGroupDao.getDetails(10)).thenReturn(questionGroup1);

        QuestionGroup questionGroup2 = new QuestionGroup();
        questionGroup2.setId(11);
        when(questionGroupDao.getDetails(11)).thenReturn(questionGroup2);

        SectionQuestion sectionQuestion1 = new SectionQuestion();
        sectionQuestion1.setId(14);
        when(sectionQuestionDao.getDetails(14)).thenReturn(sectionQuestion1);
        
        SectionQuestion sectionQuestion2 = new SectionQuestion();
        sectionQuestion2.setId(15);
        when(sectionQuestionDao.getDetails(15)).thenReturn(sectionQuestion2);

        QuestionGroupInstance questionGroupInstance = new QuestionGroupInstance();
        questionGroupInstance.setVersionNum(3);
        when(questionGroupInstanceDao.retrieveLatestQuestionGroupInstanceByQuestionGroupAndEntity(201,10)).thenReturn(asList(questionGroupInstance));
        when(questionGroupInstanceDao.retrieveLatestQuestionGroupInstanceByQuestionGroupAndEntity(201,11)).thenReturn(asList(questionGroupInstance));

        List<QuestionDetail> questionDetails1 = asList(new QuestionDetail(12, "Question 1", "Question 1", QuestionType.FREETEXT, true));
        List<SectionDetail> sectionDetails1 = asList(getSectionDetailWithQuestions(14, "Sec1", questionDetails1, "value",null));
        QuestionGroupDetail questionGroupDetail1 = new QuestionGroupDetail(10, "QG1", new EventSource("Create", "Client", null), sectionDetails1, true);

        List<QuestionDetail> questionDetails2 = asList(new QuestionDetail(13, "Question 2", "Question 2", QuestionType.DATE, true));
        List<SectionDetail> sectionDetails2 = asList(getSectionDetailWithQuestions(15, "Sec2", questionDetails2, null,null));
        QuestionGroupDetail questionGroupDetail2 = new QuestionGroupDetail(11, "QG2", new EventSource("Create", "Client", null), sectionDetails2, true);

        QuestionDetail questionDetail = new QuestionDetail(13, "Question 2", "Question 2", QuestionType.MULTI_SELECT, true);
        questionDetail.setAnswerChoices(asList(new ChoiceDetail("a1"),new ChoiceDetail("a2"),new ChoiceDetail("a3")));
        List<QuestionDetail> questionDetails3 = asList(questionDetail);
        List<SectionDetail> sectionDetails3 = asList(getSectionDetailWithQuestions(15, "Sec2", questionDetails3, null, asList("a2","a3")));
        QuestionGroupDetail questionGroupDetail3 = new QuestionGroupDetail(11, "QG2", new EventSource("Create", "Client", null), sectionDetails3, true);

        List<QuestionGroupInstance> questionGroupInstances =
                questionnaireMapper.mapToQuestionGroupInstances(new QuestionGroupDetails(101, 201, asList(questionGroupDetail1, questionGroupDetail2,questionGroupDetail3)));
        assertThat(questionGroupInstances, is(notNullValue()));
        assertThat(questionGroupInstances.size(), is(3));
        QuestionGroupInstance questionGroupInstance1 = questionGroupInstances.get(0);
        assertThat(questionGroupInstance1.getQuestionGroup().getId(), is(10));
        assertThat(questionGroupInstance1.getCompletedStatus(), is(1));
        assertThat(questionGroupInstance1.getCreatorId(), is(101));
        assertThat(questionGroupInstance1.getDateConducted(), is(notNullValue()));
        assertThat(questionGroupInstance1.getEntityId(), is(201));
        assertThat(questionGroupInstance1.getVersionNum(), is(4));
        List<QuestionGroupResponse> questionGroupResponses1 = questionGroupInstance1.getQuestionGroupResponses();
        assertThat(questionGroupResponses1, is(notNullValue()));
        assertThat(questionGroupResponses1.size(), is(1));
        QuestionGroupResponse questionGroupResponse = questionGroupResponses1.get(0);
        assertThat(questionGroupResponse.getResponse(), is("value"));
        assertThat(questionGroupResponse.getSectionQuestion().getId(), is(14));

        QuestionGroupInstance questionGroupInstance2 = questionGroupInstances.get(1);
        assertThat(questionGroupInstance2.getQuestionGroup().getId(), is(11));
        assertThat(questionGroupInstance2.getCompletedStatus(), is(1));
        assertThat(questionGroupInstance2.getCreatorId(), is(101));
        assertThat(questionGroupInstance2.getDateConducted(), is(notNullValue()));
        assertThat(questionGroupInstance2.getEntityId(), is(201));
        assertThat(questionGroupInstance2.getVersionNum(), is(4));
        List<QuestionGroupResponse> questionGroupResponses2 = questionGroupInstance2.getQuestionGroupResponses();
        assertThat(questionGroupResponses2, is(notNullValue()));
        assertThat(questionGroupResponses2.size(), is(0));

        QuestionGroupInstance questionGroupInstance3 = questionGroupInstances.get(2);
        assertThat(questionGroupInstance3.getQuestionGroup().getId(), is(11));
        assertThat(questionGroupInstance3.getCompletedStatus(), is(1));
        assertThat(questionGroupInstance3.getCreatorId(), is(101));
        assertThat(questionGroupInstance3.getDateConducted(), is(notNullValue()));
        assertThat(questionGroupInstance3.getEntityId(), is(201));
        assertThat(questionGroupInstance3.getVersionNum(), is(4));
        List<QuestionGroupResponse> questionGroupResponses3 = questionGroupInstance3.getQuestionGroupResponses();
        assertThat(questionGroupInstance3, is(notNullValue()));
        assertThat(questionGroupResponses3.size(), is(2));
        assertThat(questionGroupResponses3.get(0).getResponse(), is("a2"));
        assertThat(questionGroupResponses3.get(1).getResponse(), is("a3"));

        verify(questionGroupInstanceDao, times(1)).retrieveLatestQuestionGroupInstanceByQuestionGroupAndEntity(201,10);
        verify(questionGroupInstanceDao, times(2)).retrieveLatestQuestionGroupInstanceByQuestionGroupAndEntity(201,11);
    }

    @Test
    public void shouldMapToQuestionGroupInstanceDetails() {
        QuestionGroupInstance questionGroupInstance1 = getQuestionGroupInstance("QG1", 2010, 7, 25);
        QuestionGroupInstance questionGroupInstance2 = getQuestionGroupInstance("QG3", 2009, 2, 12);
        QuestionGroup questionGroup = getQuestionGroup(1991, "QG5", getSectionWithOneMultiSelectQuestion(222, "Section3", "Question3", "Choice1", "Choice2", "Choice3", "Choice4"));
        QuestionGroupInstance questionGroupInstance3 = getQuestionGroupInstanceWithSingleMultiSelectQuestion(101, 3, questionGroup, "Choice1", "Choice3", "Choice4");
        List<QuestionGroupInstance> questionGroupInstances = asList(questionGroupInstance1, questionGroupInstance2, questionGroupInstance3);
        List<QuestionGroupInstanceDetail> questionGroupInstanceDetails = questionnaireMapper.mapToQuestionGroupInstanceDetails(questionGroupInstances);
        assertThat(questionGroupInstanceDetails, is(notNullValue()));
        assertThat(questionGroupInstanceDetails.size(), is(3));
        assertQuestionGroupInstanceDetail(questionGroupInstanceDetails.get(0), "QG1", 2010, 7, 25);
        assertQuestionGroupInstanceDetail(questionGroupInstanceDetails.get(1), "QG3", 2009, 2, 12);
        QuestionGroupInstanceDetail detail = questionGroupInstanceDetails.get(2);
        assertThat(detail.getQuestionGroupTitle(), is("QG5"));
        List<String> values = detail.getQuestionGroupDetail().getSectionDetail(0).getQuestionDetail(0).getValues();
        assertThat(values, is(notNullValue()));
        assertThat(values.size(), is(3));
        assertThat(values.get(0), is("Choice1"));
        assertThat(values.get(1), is("Choice3"));
        assertThat(values.get(2), is("Choice4"));
    }

    @Test
    public void shouldMapToEmptyQuestionGroupInstanceDetail() {
        QuestionGroupInstanceDetail detail = questionnaireMapper.mapToEmptyQuestionGroupInstanceDetail(getQuestionGroup(1991, "QG1", getSection("Section1")));
        assertThat(detail, is(notNullValue()));
        assertThat(detail.getQuestionGroupDetail().getId(), is(1991));
        assertThat(detail.getQuestionGroupTitle(), is("QG1"));
        assertThat(detail.getId(), is(0));
    }

    private QuestionGroupInstance getQuestionGroupInstanceWithSingleMultiSelectQuestion(int entityId, int version, QuestionGroup questionGroup, String... responses) {
        QuestionGroupInstance questionGroupInstance = new QuestionGroupInstance();
        questionGroupInstance.setQuestionGroup(questionGroup);
        questionGroupInstance.setCompletedStatus(1);
        questionGroupInstance.setCreatorId(122);
        questionGroupInstance.setDateConducted(Calendar.getInstance().getTime());
        questionGroupInstance.setEntityId(entityId);
        questionGroupInstance.setVersionNum(version);
        List<QuestionGroupResponse> groupResponses = new ArrayList<QuestionGroupResponse>();
        for (String response : responses) {
            groupResponses.add(getQuestionGroupResponse(response, questionGroupInstance, questionGroup.getSections().get(0).getQuestions().get(0)));
        }
        questionGroupInstance.setQuestionGroupResponses(groupResponses);
        return questionGroupInstance;
    }

    private QuestionGroupResponse getQuestionGroupResponse(String response, QuestionGroupInstance instance, SectionQuestion sectionQuestion) {
        QuestionGroupResponse questionGroupResponse = new QuestionGroupResponse();
        questionGroupResponse.setResponse(response);
        questionGroupResponse.setQuestionGroupInstance(instance);
        questionGroupResponse.setSectionQuestion(sectionQuestion);
        return questionGroupResponse;
    }

    private Section getSectionWithOneMultiSelectQuestion(int sectionQuestionId, String sectionName, String questionName, String... choices) {
        Section section = new Section(sectionName);
        List<SectionQuestion> sectionQuestions = new ArrayList<SectionQuestion>();
        SectionQuestion sectionQuestion = new SectionQuestion();
        sectionQuestion.setId(sectionQuestionId);
        sectionQuestion.setSection(section);
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setQuestionText(questionName);
        questionEntity.setShortName(questionName);
        questionEntity.setAnswerType(AnswerType.MULTISELECT);
        LinkedList<QuestionChoiceEntity> questionChoiceEntities = new LinkedList<QuestionChoiceEntity>();
        for (String choice : choices) {
            QuestionChoiceEntity questionChoiceEntity = new QuestionChoiceEntity();
            questionChoiceEntity.setChoiceText(choice);
            questionChoiceEntities.add(questionChoiceEntity);
        }
        questionEntity.setChoices(questionChoiceEntities);
        sectionQuestion.setQuestion(questionEntity);
        sectionQuestions.add(sectionQuestion);
        section.setQuestions(sectionQuestions);
        return section;
    }

    private void assertQuestionGroupInstanceDetail(QuestionGroupInstanceDetail questionGroupInstanceDetail, String questionGroupTitle, int year, int month, int day) {
        assertThat(questionGroupInstanceDetail.getQuestionGroupTitle(), is(questionGroupTitle));
        Date date = questionGroupInstanceDetail.getDateCompleted();
        assertDate(date, year, month, day);
    }

    private void assertDate(Date date, int year, int month, int day) {
        assertThat(date, is(notNullValue()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        assertThat(calendar.get(Calendar.YEAR), is(year));
        assertThat(calendar.get(Calendar.MONTH), is(month));
        assertThat(calendar.get(Calendar.DATE), is(day));
    }

    private QuestionGroupInstance getQuestionGroupInstance(String questionGroupTitle, int year, int month, int date) {
        QuestionGroupInstance questionGroupInstance = new QuestionGroupInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);
        questionGroupInstance.setDateConducted(calendar.getTime());
        questionGroupInstance.setQuestionGroup(getQuestionGroup(1991, questionGroupTitle));
        return questionGroupInstance;
    }

    private SectionDetail getSectionDetailWithQuestions(int id, String name, List<QuestionDetail> questionDetails, String answer, List<String> answers) {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName(name);
        List<SectionQuestionDetail> sectionQuestionDetails = new ArrayList<SectionQuestionDetail>();
        for (QuestionDetail questionDetail : questionDetails) {
            SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail(id,questionDetail, false,answer, answers);
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

    private QuestionGroup getQuestionGroup(int id, String title, Section... sections) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setId(id);
        questionGroup.setTitle(title);
        questionGroup.setSections(asList(sections));
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
        for (int i = 0, choicesSize = choices.size(); i < choicesSize; i++) {
            String choice = choices.get(i);
            Assert.assertEquals(choice, questionDetail.getAnswerChoices().get(i).getValue());
        }
    }

    private EventSource getEventSource(String event, String source) {
        return new EventSource(event, source, null);
    }

    private EventSource getEventSource(String event, String source, String description) {
        return new EventSource(event, source, description);
    }

    @Test
    public void shouldMapToQuestionGroupFromDto() {
        when(eventSourceDao.retrieveByEventAndSource("Create", "Client")).thenReturn(asList(getEventSourceEntity("Create", "Client")));
        QuestionDto question1 = new QuestionDtoBuilder().withTitle("Ques1").withMandatory(true).withType(QuestionType.FREETEXT).withOrder(1).build();
        ChoiceDetail choice1 = new ChoiceDetailBuilder().withValue("Ch1").withOrder(1).build();
        ChoiceDetail choice2 = new ChoiceDetailBuilder().withValue("Ch2").withOrder(2).build();
        ChoiceDetail choice3 = new ChoiceDetailBuilder().withValue("Ch3").withOrder(3).build();
        QuestionDto question2 = new QuestionDtoBuilder().withTitle("Ques2").withType(QuestionType.SINGLE_SELECT).addChoices(choice1, choice2, choice3).withOrder(2).build();
        SectionDto section1 = new SectionDtoBuilder().withName("Sec1").withOrder(1).addQuestions(question1, question2).build();
        QuestionGroupDto questionGroupDto = new QuestionGroupDtoBuilder().withTitle("QG1").withEventSource("Create", "Client").addSections(section1).build();
        assertQuestionGroupEntity(questionnaireMapper.mapToQuestionGroup(questionGroupDto));
        verify(eventSourceDao, times(1)).retrieveByEventAndSource(Matchers.anyString(), Matchers.anyString());
    }

    private void assertQuestionGroupEntity(QuestionGroup questionGroup) {
        assertThat(questionGroup, is(notNullValue()));
        assertThat(questionGroup.getTitle(), is("QG1"));
        Set<EventSourceEntity> eventSources = questionGroup.getEventSources();
        assertThat(eventSources, is(notNullValue()));
        assertThat(eventSources.size(), is(1));
        EventSourceEntity eventSourceEntity = eventSources.toArray(new EventSourceEntity[eventSources.size()])[0];
        assertThat(eventSourceEntity.getEvent().getName(), is("Create"));
        assertThat(eventSourceEntity.getSource().getEntityType(), is("Client"));
        assertThat(questionGroup.getState(), is(QuestionGroupState.ACTIVE));
        List<Section> sections = questionGroup.getSections();
        assertThat(sections, is(notNullValue()));
        assertThat(sections.size(), is(1));
        Section section = sections.get(0);
        assertThat(section.getName(), is("Sec1"));
        List<SectionQuestion> questions = section.getQuestions();
        assertThat(questions, is(notNullValue()));
        assertThat(questions.size(), is(2));

        SectionQuestion sectionQuestion1 = questions.get(0);
        assertThat(sectionQuestion1.getSequenceNumber(), is(1));
        assertThat(sectionQuestion1.getSection(), is(notNullValue()));
        assertThat(sectionQuestion1.getSection().getName(), is("Sec1"));
        assertThat(sectionQuestion1.getSection().getSequenceNumber(), is(1));
        assertThat(sectionQuestion1.getQuestion(), is(notNullValue()));
        assertThat(sectionQuestion1.getQuestion().getShortName(), is("Ques1"));
        assertThat(sectionQuestion1.getQuestion().getAnswerTypeAsEnum(), is(AnswerType.FREETEXT));

        SectionQuestion sectionQuestion2 = questions.get(1);
        assertThat(sectionQuestion2.getSequenceNumber(), is(2));
        assertThat(sectionQuestion2.getSection(), is(notNullValue()));
        assertThat(sectionQuestion2.getSection().getName(), is("Sec1"));
        assertThat(sectionQuestion2.getSection().getSequenceNumber(), is(1));
        assertThat(sectionQuestion2.getQuestion(), is(notNullValue()));
        assertThat(sectionQuestion2.getQuestion().getShortName(), is("Ques2"));
        assertThat(sectionQuestion2.getQuestion().getAnswerTypeAsEnum(), is(AnswerType.SINGLESELECT));
        assertThat(sectionQuestion2.getQuestion().getChoices(), is(notNullValue()));
        assertThat(sectionQuestion2.getQuestion().getChoices().size(), is(3));
        assertThat(sectionQuestion2.getQuestion().getChoices().get(0).getChoiceText(), is("Ch1"));
        assertThat(sectionQuestion2.getQuestion().getChoices().get(0).getChoiceOrder(), is(1));
        assertThat(sectionQuestion2.getQuestion().getChoices().get(1).getChoiceText(), is("Ch2"));
        assertThat(sectionQuestion2.getQuestion().getChoices().get(1).getChoiceOrder(), is(2));
        assertThat(sectionQuestion2.getQuestion().getChoices().get(2).getChoiceText(), is("Ch3"));
        assertThat(sectionQuestion2.getQuestion().getChoices().get(2).getChoiceOrder(), is(3));
    }

    private EventSourceEntity getEventSourceEntity(String event, String source) {
        EventSourceEntity eventSource = new EventSourceEntity();
        EventEntity eventEntity = new EventEntity();
        eventEntity.setName(event);
        eventSource.setEvent(eventEntity);
        EntityMaster entityMaster = new EntityMaster();
        entityMaster.setEntityType(source);
        eventSource.setSource(entityMaster);
        return eventSource;
    }
}

