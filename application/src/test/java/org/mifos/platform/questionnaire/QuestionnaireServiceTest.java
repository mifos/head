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

package org.mifos.platform.questionnaire;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.business.QuestionChoice;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.framework.components.fieldConfiguration.business.EntityMaster;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.contract.*;
import org.mifos.platform.questionnaire.domain.*;
import org.mifos.platform.questionnaire.mappers.QuestionnaireMapperImpl;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.mifos.platform.questionnaire.validators.QuestionnaireValidator;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.QUESTION_GROUP_TITLE_NOT_PROVIDED;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.QUESTION_TITLE_NOT_PROVIDED;
import static org.mifos.platform.questionnaire.contract.QuestionType.FREETEXT;
import static org.mifos.platform.questionnaire.contract.QuestionType.INVALID;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireServiceTest {

    private QuestionnaireService questionnaireService;

    @Mock
    private QuestionnaireValidator questionnaireValidator;

    @Mock
    private QuestionDao questionDao;

    @Mock
    private QuestionGroupDao questionGroupDao;

    @Mock
    private EventSourceDao eventSourceDao;

    private static final String QUESTION_TITLE = "Test QuestionDetail Title";
    private static final String QUESTION_GROUP_TITLE = "Question Group Title";
    public static final String EVENT_CREATE = "Create";
    public static final String SOURCE_CLIENT = "Client";

    @Before
    public void setUp() {
        QuestionnaireMapperImpl questionnaireMapper = new QuestionnaireMapperImpl(eventSourceDao, questionDao);
        questionnaireService = new QuestionnaireServiceImpl(questionnaireValidator, questionDao, questionnaireMapper, questionGroupDao, eventSourceDao);
    }

    @Test
    public void shouldDefineQuestion() throws ApplicationException {
        QuestionDetail questionDefinition = new QuestionDetail(QUESTION_TITLE, FREETEXT);
        try {
            QuestionDetail questionDetail = questionnaireService.defineQuestion(questionDefinition);
            verify(questionDao, times(1)).create(any(Question.class));
            assertNotNull(questionDetail);
            assertEquals(QUESTION_TITLE, questionDetail.getText());
            assertEquals(QUESTION_TITLE, questionDetail.getShortName());
            assertEquals(FREETEXT, questionDetail.getType());
            org.testng.Assert.assertEquals(questionDetail.getAnswerChoices(),asList());
        } catch (ApplicationException e) {
            fail("Should not have thrown the validation exception");
        }
        verify(questionnaireValidator).validate(questionDefinition);
        verify(questionDao).create(any(org.mifos.customers.surveys.business.Question.class));
    }

    @Test
    public void shouldDefineQuestionWithAnswerChoices() throws ApplicationException {
        String choice1 = "choice1";
        String choice2 = "choice2";
        QuestionDetail questionDefinition = new QuestionDetail(QUESTION_TITLE, QuestionType.MULTIPLE_CHOICE, asList(choice1, choice2));
        try {
            QuestionDetail questionDetail = questionnaireService.defineQuestion(questionDefinition);
            verify(questionDao, times(1)).create(any(Question.class));
            assertNotNull(questionDetail);
            assertEquals(QUESTION_TITLE, questionDetail.getText());
            assertEquals(QUESTION_TITLE, questionDetail.getShortName());
            assertEquals(QuestionType.MULTIPLE_CHOICE, questionDetail.getType());
            org.testng.Assert.assertEquals(questionDetail.getAnswerChoices(),asList(choice1, choice2));
        } catch (ApplicationException e) {
            fail("Should not have thrown the validation exception");
        }
        verify(questionnaireValidator).validate(questionDefinition);
        verify(questionDao).create(any(org.mifos.customers.surveys.business.Question.class));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test(expected = ApplicationException.class)
    public void shouldThrowValidationExceptionWhenQuestionTitleIsNull() throws ApplicationException {
        QuestionDetail questionDefinition = new QuestionDetail(null, INVALID);
        doThrow(new ApplicationException(QUESTION_TITLE_NOT_PROVIDED)).when(questionnaireValidator).validate(questionDefinition);
        questionnaireService.defineQuestion(questionDefinition);
        verify(questionnaireValidator).validate(questionDefinition);
    }

    @Test
    public void shouldGetAllQuestions() {
        when(questionDao.retrieveByState(1)).thenReturn(asList(getQuestion(1, "q1", AnswerType.DATE), getQuestion(2, "q2", AnswerType.FREETEXT)));
        List<QuestionDetail> questionDetails = questionnaireService.getAllQuestions();
        assertNotNull("getAllQuestions should not return null", questionDetails);
        verify(questionDao, times(1)).retrieveByState(1);

        assertThat(questionDetails.get(0).getText(), is("q1"));
        assertThat(questionDetails.get(0).getShortName(), is("q1"));
        assertThat(questionDetails.get(0).getId(), is(1));
        assertThat(questionDetails.get(0).getType(), is(QuestionType.DATE));

        assertThat(questionDetails.get(1).getText(), is("q2"));
        assertThat(questionDetails.get(1).getShortName(), is("q2"));
        assertThat(questionDetails.get(1).getId(), is(2));
        assertThat(questionDetails.get(1).getType(), is(QuestionType.FREETEXT));

    }

    private Question getQuestion(int id, String text, AnswerType type) {
        Question question = new Question();
        question.setQuestionId(id);
        question.setQuestionText(text);
        question.setShortName(text);
        question.setAnswerType(type);
        question.setChoices(new LinkedList<QuestionChoice>());
        return question;
    }

    private Question getQuestion(int id, String text, AnswerType type, List<QuestionChoice> choices) {
        Question question = getQuestion(id, text, type);
        question.setChoices(choices);
        return question;
    }

    @Test
    public void shouldDefineQuestionGroup() throws ApplicationException {
        QuestionGroupDetail questionGroupDefinition = getQuestionGroupDetail(EVENT_CREATE, SOURCE_CLIENT, "S1", "S2");
        setUpEventSourceExpectations(EVENT_CREATE, SOURCE_CLIENT);
        when(questionDao.getDetails(anyInt())).thenReturn(getQuestion(11), getQuestion(12), getQuestion(11), getQuestion(12));
        try {
            QuestionGroupDetail questionGroupDetail = questionnaireService.defineQuestionGroup(questionGroupDefinition);
            assertQuestionGroupDetail(questionGroupDetail);
            verify(questionnaireValidator).validate(questionGroupDefinition);
            verify(questionGroupDao, times(1)).create(any(QuestionGroup.class));
            verify(eventSourceDao, times(1)).retrieveByEventAndSource(anyString(), anyString());
            verify(questionDao, times(4)).getDetails(anyInt());
        } catch (ApplicationException e) {
            fail("Should not have thrown the validation exception");
        }
    }

    private Question getQuestion(int questionId) {
        Question question = new Question();
        question.setQuestionId(questionId);
        return question;
    }

    private void assertQuestionGroupDetail(QuestionGroupDetail questionGroupDetail) {
        assertNotNull(questionGroupDetail);
        assertEquals(QUESTION_GROUP_TITLE, questionGroupDetail.getTitle());
        assertSections(questionGroupDetail.getSectionDetails());
        assertEvent(questionGroupDetail.getEventSource());
    }

    private void assertEvent(EventSource eventSource) {
        assertThat(eventSource, notNullValue());
        assertThat(eventSource.getEvent(), is(EVENT_CREATE));
        assertThat(eventSource.getSource(), is(SOURCE_CLIENT));
    }

    private void assertSections(List<SectionDetail> sectionDetails) {
        assertNotNull(sectionDetails);
        assertThat(sectionDetails.size(), is(2));
        assertThat(sectionDetails.get(0).getName(), is("S1"));
        assertThat(sectionDetails.get(1).getName(), is("S2"));
        assertSectionQuestions(sectionDetails.get(0).getQuestions());
    }

    private void assertSectionQuestions(List<SectionQuestionDetail> sectionQuestionDetails) {
        assertThat(sectionQuestionDetails, notNullValue());
        assertThat(sectionQuestionDetails.size(), is(2));
        assertThat(sectionQuestionDetails.get(0).getQuestionId(), is(11));
        assertThat(sectionQuestionDetails.get(0).isMandatory(), is(true));
        assertThat(sectionQuestionDetails.get(1).getQuestionId(), is(12));
        assertThat(sectionQuestionDetails.get(1).isMandatory(), is(false));
    }

    private void setUpEventSourceExpectations(String event, String source) {
        EventSourceEntity eventSourceEntity = getEventSourceEntity(event, source);
        when(eventSourceDao.retrieveByEventAndSource(anyString(), anyString())).thenReturn(Collections.singletonList(eventSourceEntity));
    }

    private QuestionGroupDetail getQuestionGroupDetail(String event, String source, String... sectionNames) {
        return new QuestionGroupDetail(0, QUESTION_GROUP_TITLE, getEventSource(event, source), getSectionDefinitions(sectionNames));
    }

    private List<SectionDetail> getSectionDefinitions(String... sectionNames) {
        List<SectionDetail> sectionDetailList = new ArrayList<SectionDetail>();
        for (String sectionName : sectionNames) {
            sectionDetailList.add(getSectionDefinition(sectionName));
        }
        return sectionDetailList;
    }

    private EventSourceEntity getEventSourceEntity(String event, String source) {
        EventSourceEntity eventSourceEntity = new EventSourceEntity();
        EventEntity eventEntity = new EventEntity();
        eventEntity.setName(event);
        eventSourceEntity.setEvent(eventEntity);
        EntityMaster entityMaster = new EntityMaster();
        entityMaster.setEntityType(source);
        eventSourceEntity.setSource(entityMaster);
        return eventSourceEntity;
    }

    private EventSource getEventSource(String event, String source) {
        return new EventSource(event, source, null);
    }

    private SectionDetail getSectionDefinition(String name) {
        SectionDetail section = new SectionDetail();
        section.setName(name);
        section.addQuestion(new SectionQuestionDetail(11, true));
        section.addQuestion(new SectionQuestionDetail(12, false));
        return section;
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test(expected = ApplicationException.class)
    public void shouldThrowValidationExceptionWhenQuestionGroupTitleIsNull() throws ApplicationException {
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(0, null, null, asList(getSectionDefinition("S1")));
        doThrow(new ApplicationException(QUESTION_GROUP_TITLE_NOT_PROVIDED)).when(questionnaireValidator).validate(questionGroupDetail);
        questionnaireService.defineQuestionGroup(questionGroupDetail);
        verify(questionnaireValidator).validate(questionGroupDetail);
    }

    @Test
    public void shouldGetAllQuestionGroups() {
        QuestionGroup questionGroup1 = getQuestionGroup(0, "QG0", getSections("S0_0"));
        QuestionGroup questionGroup2 = getQuestionGroup(1, "QG1", getSections("S1_0", "S1_1"));
        when(questionGroupDao.getDetailsAll()).thenReturn(asList(questionGroup1, questionGroup2));
        List<QuestionGroupDetail> questionGroupDetails = questionnaireService.getAllQuestionGroups();
        assertNotNull("getAllQuestionGroups should not return null", questionGroupDetails);
        for (int i = 0; i < questionGroupDetails.size(); i++) {
            assertThat(questionGroupDetails.get(i).getId(), is(i));
            assertThat(questionGroupDetails.get(i).getTitle(), is("QG"+i));
            List<SectionDetail> sectionDetails = questionGroupDetails.get(i).getSectionDetails();
            for(int j=0;j< sectionDetails.size();j++) {
                assertThat(sectionDetails.get(j).getName(), is("S"+i+"_"+j));
            }
        }
    }

    private List<Section> getSections(String... names) {
        List<Section> sectionList = new ArrayList<Section>();
        for (String name : names) {
            Section section = new Section(name);
            SectionQuestion sectionQuestion = new SectionQuestion();
            sectionQuestion.setQuestion(new Question());
            section.setQuestions(asList(sectionQuestion));
            sectionList.add(section);
        }
        return sectionList;
    }

    @Test
    public void shouldGetAllEventSources() {
        when(eventSourceDao.getDetailsAll()).thenReturn(asList(getEventSourceEntity("Create", "Client")));
        List<EventSource> eventSources = questionnaireService.getAllEventSources();
        assertThat(eventSources, notNullValue());
        assertThat(eventSources.size(), is(1));
        assertThat(eventSources.get(0).getEvent(), is("Create"));
        assertThat(eventSources.get(0).getSource(), is("Client"));
    }

    @Test
    public void testGetQuestionGroupByIdSuccess() throws ApplicationException {
        int questionGroupId = 1;
        String title = "Title";
        when(questionGroupDao.getDetails(questionGroupId)).thenReturn(getQuestionGroup(questionGroupId, title, getSections("S1", "S2")));
        QuestionGroupDetail groupDetail = questionnaireService.getQuestionGroup(questionGroupId);
        assertNotNull(groupDetail);
        assertThat(groupDetail.getTitle(), is(title));
        verify(questionGroupDao, times(1)).getDetails(questionGroupId);
    }

    @Test
    public void testGetQuestionGroupByIdFailure() {
        int questionGroupId = 1;
        when(questionGroupDao.getDetails(questionGroupId)).thenReturn(null);
        try {
            questionnaireService.getQuestionGroup(questionGroupId);
            fail("Should raise application exception when question group is not present");
        } catch (ApplicationException e) {
            verify(questionGroupDao, times(1)).getDetails(questionGroupId);
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        }
    }

    @Test
    public void testGetQuestionByIdSuccess() throws ApplicationException {
        int questionId = 1;
        String title = "Title";
        when(questionDao.getDetails(questionId)).thenReturn(getQuestion(questionId, title, AnswerType.DATE));
        QuestionDetail questionDetail = questionnaireService.getQuestion(questionId);
        assertNotNull(questionDetail);
        assertThat(questionDetail.getShortName(), is(title));
        assertThat(questionDetail.getText(), is(title));
        assertThat(questionDetail.getType(), is(QuestionType.DATE));
        org.testng.Assert.assertEquals(questionDetail.getAnswerChoices(),asList());
        verify(questionDao, times(1)).getDetails(questionId);
    }
    
    @Test
    public void testGetQuestionWithAnswerChoicesById() throws ApplicationException {
        int questionId = 1;
        String title = "Title";
        when(questionDao.getDetails(questionId)).thenReturn(getQuestion(questionId, title, AnswerType.MULTIPLE_CHOICE, asList(new QuestionChoice("choice1"), new QuestionChoice("choice2"))));
        QuestionDetail questionDetail = questionnaireService.getQuestion(questionId);
        assertNotNull(questionDetail);
        assertThat(questionDetail.getShortName(), is(title));
        assertThat(questionDetail.getText(), is(title));
        assertThat(questionDetail.getType(), is(QuestionType.MULTIPLE_CHOICE));
        org.testng.Assert.assertEquals(questionDetail.getAnswerChoices(),asList("choice1","choice2"));
        verify(questionDao, times(1)).getDetails(questionId);
    }

    @Test
    public void testGetQuestionByIdFailure() {
        int questionId = 1;
        when(questionDao.getDetails(questionId)).thenReturn(null);
        try {
            questionnaireService.getQuestion(questionId);
            fail("Should raise application exception when question group is not present");
        } catch (ApplicationException e) {
            verify(questionDao, times(1)).getDetails(questionId);
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_NOT_FOUND));
        }
    }

    private QuestionGroup getQuestionGroup(int questionGroupId, String title, List<Section> sections) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setId(questionGroupId);
        questionGroup.setTitle(title);
        questionGroup.setSections(sections);
        return questionGroup;
    }

    @Test
    public void shouldCheckDuplicates() {
        QuestionDefinition questionDefinition = new QuestionDefinition(QUESTION_TITLE, FREETEXT);
        when(questionDao.retrieveCountOfQuestionsWithTitle(QUESTION_TITLE)).thenReturn(asList((long) 0)).thenReturn(asList((long) 1));
        assertEquals(false, questionnaireService.isDuplicateQuestion(questionDefinition));
        assertEquals(true, questionnaireService.isDuplicateQuestion(questionDefinition));
        verify(questionDao, times(2)).retrieveCountOfQuestionsWithTitle(QUESTION_TITLE);
    }

    @Test
    public void shouldGetAllQuestionGroupsByEventSource() throws ApplicationException {
        List<QuestionGroup> questionGroups = asList(getQuestionGroup(1, "Title1", getSections("Section1")), getQuestionGroup(2, "Title2", getSections("Section2")));
        when(questionGroupDao.retrieveQuestionGroupsByEventSource("Create", "Client")).thenReturn(questionGroups);
        List<QuestionGroupDetail> questionGroupDetails = questionnaireService.getQuestionGroups(new EventSource("Create", "Client", "Create.Client"));
        assertThat(questionGroupDetails, is(notNullValue()));
        assertThat(questionGroupDetails.size(), is(2));
        assertThat(questionGroupDetails.get(0).getTitle(), is("Title1"));
        assertThat(questionGroupDetails.get(1).getTitle(), is("Title2"));
        verify(questionnaireValidator, times(1)).validate(any(EventSource.class));
        verify(questionGroupDao, times(1)).retrieveQuestionGroupsByEventSource("Create", "Client");
    }
}
