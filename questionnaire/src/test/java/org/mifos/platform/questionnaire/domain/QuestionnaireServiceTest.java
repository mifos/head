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

package org.mifos.platform.questionnaire.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.business.EntityMaster;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.mappers.QuestionnaireMapperImpl;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.mifos.platform.questionnaire.service.*; //NOPMD
import org.mifos.platform.questionnaire.validators.QuestionnaireValidator;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import org.junit.Assert;

import static org.hamcrest.CoreMatchers.is;   //NOPMD
import static org.hamcrest.CoreMatchers.notNullValue;  //NOPMD
import static org.mockito.Matchers.any;  //NOPMD


@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD")
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
    public void shouldDefineQuestion() throws SystemException {
        QuestionDetail questionDefinition = new QuestionDetail(QUESTION_TITLE, QuestionType.FREETEXT);
        try {
            QuestionDetail questionDetail = questionnaireService.defineQuestion(questionDefinition);
            Mockito.verify(questionDao, Mockito.times(1)).create(any(QuestionEntity.class));
            Assert.assertNotNull(questionDetail);
            Assert.assertEquals(QUESTION_TITLE, questionDetail.getText());
            Assert.assertEquals(QUESTION_TITLE, questionDetail.getShortName());
            Assert.assertEquals(QuestionType.FREETEXT, questionDetail.getType());
            Assert.assertEquals(questionDetail.getAnswerChoices(), Arrays.asList());
        } catch (SystemException e) {
            Assert.fail("Should not have thrown the validation exception");
        }
        Mockito.verify(questionnaireValidator).validate(questionDefinition);
        Mockito.verify(questionDao).create(any(QuestionEntity.class));
    }

    @Test
    public void shouldDefineQuestionWithAnswerChoices() throws SystemException {
        String choice1 = "choice1";
        String choice2 = "choice2";
        QuestionDetail questionDefinition = new QuestionDetail(QUESTION_TITLE, QuestionType.MULTI_SELECT, Arrays.asList(choice1, choice2));
        try {
            QuestionDetail questionDetail = questionnaireService.defineQuestion(questionDefinition);
            Mockito.verify(questionDao, Mockito.times(1)).create(any(QuestionEntity.class));
            Assert.assertNotNull(questionDetail);
            Assert.assertEquals(QUESTION_TITLE, questionDetail.getText());
            Assert.assertEquals(QUESTION_TITLE, questionDetail.getShortName());
            Assert.assertEquals(QuestionType.MULTI_SELECT, questionDetail.getType());
            Assert.assertEquals(questionDetail.getAnswerChoices(), Arrays.asList(choice1, choice2));
        } catch (SystemException e) {
            Assert.fail("Should not have thrown the validation exception");
        }
        Mockito.verify(questionnaireValidator).validate(questionDefinition);
        Mockito.verify(questionDao).create(any(QuestionEntity.class));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test(expected = SystemException.class)
    public void shouldThrowValidationExceptionWhenQuestionTitleIsNull() throws SystemException {
        QuestionDetail questionDefinition = new QuestionDetail(null, QuestionType.INVALID);
        Mockito.doThrow(new SystemException(QuestionnaireConstants.QUESTION_TITLE_NOT_PROVIDED)).when(questionnaireValidator).validate(questionDefinition);
        questionnaireService.defineQuestion(questionDefinition);
        Mockito.verify(questionnaireValidator).validate(questionDefinition);
    }

    @Test
    public void shouldGetAllQuestions() {
        Mockito.when(questionDao.retrieveByState(1)).thenReturn(Arrays.asList(getQuestion(1, "q1", AnswerType.DATE), getQuestion(2, "q2", AnswerType.FREETEXT)));
        List<QuestionDetail> questionDetails = questionnaireService.getAllQuestions();
        Assert.assertNotNull("getAllQuestions should not return null", questionDetails);
        Mockito.verify(questionDao, Mockito.times(1)).retrieveByState(1);

        Assert.assertThat(questionDetails.get(0).getText(), is("q1"));
        Assert.assertThat(questionDetails.get(0).getShortName(), is("q1"));
        Assert.assertThat(questionDetails.get(0).getId(), is(1));
        Assert.assertThat(questionDetails.get(0).getType(), is(QuestionType.DATE));

        Assert.assertThat(questionDetails.get(1).getText(), is("q2"));
        Assert.assertThat(questionDetails.get(1).getShortName(), is("q2"));
        Assert.assertThat(questionDetails.get(1).getId(), is(2));
        Assert.assertThat(questionDetails.get(1).getType(), is(QuestionType.FREETEXT));

    }

    private QuestionEntity getQuestion(int id, String text, AnswerType type) {
        QuestionEntity question = new QuestionEntity();
        question.setQuestionId(id);
        question.setQuestionText(text);
        question.setShortName(text);
        question.setAnswerType(type);
        question.setChoices(new LinkedList<QuestionChoiceEntity>());
        return question;
    }

    private QuestionEntity getQuestion(int id, String text, AnswerType type, List<QuestionChoiceEntity> choices) {
        QuestionEntity question = getQuestion(id, text, type);
        question.setChoices(choices);
        return question;
    }

    @Test
    public void shouldDefineQuestionGroup() throws SystemException {
        QuestionGroupDetail questionGroupDefinition = getQuestionGroupDetail(EVENT_CREATE, SOURCE_CLIENT, "S1", "S2");
        setUpEventSourceExpectations(EVENT_CREATE, SOURCE_CLIENT);
        Mockito.when(questionDao.getDetails(Mockito.anyInt())).thenReturn(getQuestion(11), getQuestion(12), getQuestion(11), getQuestion(12));
        try {
            QuestionGroupDetail questionGroupDetail = questionnaireService.defineQuestionGroup(questionGroupDefinition);
            assertQuestionGroupDetail(questionGroupDetail);
            Mockito.verify(questionnaireValidator).validate(questionGroupDefinition);
            Mockito.verify(questionGroupDao, Mockito.times(1)).create(any(QuestionGroup.class));
            Mockito.verify(eventSourceDao, Mockito.times(1)).retrieveByEventAndSource(Mockito.anyString(), Mockito.anyString());
            Mockito.verify(questionDao, Mockito.times(4)).getDetails(Mockito.anyInt());
        } catch (SystemException e) {
            Assert.fail("Should not have thrown the validation exception");
        }
    }

    private QuestionEntity getQuestion(int questionId) {
        QuestionEntity question = new QuestionEntity();
        question.setQuestionId(questionId);
        return question;
    }

    private void assertQuestionGroupDetail(QuestionGroupDetail questionGroupDetail) {
        Assert.assertNotNull(questionGroupDetail);
        Assert.assertEquals(QUESTION_GROUP_TITLE, questionGroupDetail.getTitle());
        assertSections(questionGroupDetail.getSectionDetails());
        assertEvent(questionGroupDetail.getEventSource());
    }

    private void assertEvent(EventSource eventSource) {
        Assert.assertThat(eventSource, notNullValue());
        Assert.assertThat(eventSource.getEvent(), is(EVENT_CREATE));
        Assert.assertThat(eventSource.getSource(), is(SOURCE_CLIENT));
    }

    private void assertSections(List<SectionDetail> sectionDetails) {
        Assert.assertNotNull(sectionDetails);
        Assert.assertThat(sectionDetails.size(), is(2));
        Assert.assertThat(sectionDetails.get(0).getName(), is("S1"));
        Assert.assertThat(sectionDetails.get(1).getName(), is("S2"));
        assertSectionQuestions(sectionDetails.get(0).getQuestions());
    }

    private void assertSectionQuestions(List<SectionQuestionDetail> sectionQuestionDetails) {
        Assert.assertThat(sectionQuestionDetails, notNullValue());
        Assert.assertThat(sectionQuestionDetails.size(), is(2));
        Assert.assertThat(sectionQuestionDetails.get(0).getQuestionId(), is(11));
        Assert.assertThat(sectionQuestionDetails.get(0).isMandatory(), is(true));
        Assert.assertThat(sectionQuestionDetails.get(1).getQuestionId(), is(12));
        Assert.assertThat(sectionQuestionDetails.get(1).isMandatory(), is(false));
    }

    private void setUpEventSourceExpectations(String event, String source) {
        EventSourceEntity eventSourceEntity = getEventSourceEntity(event, source);
        Mockito.when(eventSourceDao.retrieveByEventAndSource(Mockito.anyString(), Mockito.anyString())).thenReturn(Collections.singletonList(eventSourceEntity));
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
        section.addQuestion(new SectionQuestionDetail(new QuestionDetail(11, null, null, QuestionType.INVALID), true));
        section.addQuestion(new SectionQuestionDetail(new QuestionDetail(12, null, null, QuestionType.INVALID), false));
        return section;
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test(expected = SystemException.class)
    public void shouldThrowValidationExceptionWhenQuestionGroupTitleIsNull() throws SystemException {
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(0, null, null, Arrays.asList(getSectionDefinition("S1")));
        Mockito.doThrow(new SystemException(QuestionnaireConstants.QUESTION_GROUP_TITLE_NOT_PROVIDED)).when(questionnaireValidator).validate(questionGroupDetail);
        questionnaireService.defineQuestionGroup(questionGroupDetail);
        Mockito.verify(questionnaireValidator).validate(questionGroupDetail);
    }

    @Test
    public void shouldGetAllQuestionGroups() {
        QuestionGroup questionGroup1 = getQuestionGroup(0, "QG0", getSections("S0_0"));
        QuestionGroup questionGroup2 = getQuestionGroup(1, "QG1", getSections("S1_0", "S1_1"));
        Mockito.when(questionGroupDao.getDetailsAll()).thenReturn(Arrays.asList(questionGroup1, questionGroup2));
        List<QuestionGroupDetail> questionGroupDetails = questionnaireService.getAllQuestionGroups();
        Assert.assertNotNull("getAllQuestionGroups should not return null", questionGroupDetails);
        for (int i = 0; i < questionGroupDetails.size(); i++) {
            Assert.assertThat(questionGroupDetails.get(i).getId(), is(i));
            Assert.assertThat(questionGroupDetails.get(i).getTitle(), is("QG"+i));
            List<SectionDetail> sectionDetails = questionGroupDetails.get(i).getSectionDetails();
            for(int j=0;j< sectionDetails.size();j++) {
                Assert.assertThat(sectionDetails.get(j).getName(), is("S"+i+"_"+j));
            }
        }
    }

    private List<Section> getSections(String... names) {
        List<Section> sectionList = new ArrayList<Section>();
        for (String name : names) {
            Section section = new Section(name);
            SectionQuestion sectionQuestion = new SectionQuestion();
            sectionQuestion.setQuestion(new QuestionEntity());
            section.setQuestions(Arrays.asList(sectionQuestion));
            sectionList.add(section);
        }
        return sectionList;
    }

    @Test
    public void shouldGetAllEventSources() {
        Mockito.when(eventSourceDao.getDetailsAll()).thenReturn(Arrays.asList(getEventSourceEntity("Create", "Client")));
        List<EventSource> eventSources = questionnaireService.getAllEventSources();
        Assert.assertThat(eventSources, notNullValue());
        Assert.assertThat(eventSources.size(), is(1));
        Assert.assertThat(eventSources.get(0).getEvent(), is("Create"));
        Assert.assertThat(eventSources.get(0).getSource(), is("Client"));
    }

    @Test
    public void testGetQuestionGroupByIdSuccess() throws SystemException {
        int questionGroupId = 1;
        String title = "Title";
        Mockito.when(questionGroupDao.getDetails(questionGroupId)).thenReturn(getQuestionGroup(questionGroupId, title, getSections("S1", "S2")));
        QuestionGroupDetail groupDetail = questionnaireService.getQuestionGroup(questionGroupId);
        Assert.assertNotNull(groupDetail);
        Assert.assertThat(groupDetail.getTitle(), is(title));
        Mockito.verify(questionGroupDao, Mockito.times(1)).getDetails(questionGroupId);
    }

    @Test
    public void testGetQuestionGroupByIdFailure() {
        int questionGroupId = 1;
        Mockito.when(questionGroupDao.getDetails(questionGroupId)).thenReturn(null);
        try {
            questionnaireService.getQuestionGroup(questionGroupId);
            Assert.fail("Should raise application exception when question group is not present");
        } catch (SystemException e) {
            Mockito.verify(questionGroupDao, Mockito.times(1)).getDetails(questionGroupId);
            Assert.assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        }
    }

    @Test
    public void testGetQuestionByIdSuccess() throws SystemException {
        int questionId = 1;
        String title = "Title";
        Mockito.when(questionDao.getDetails(questionId)).thenReturn(getQuestion(questionId, title, AnswerType.DATE));
        QuestionDetail questionDetail = questionnaireService.getQuestion(questionId);
        Assert.assertNotNull(questionDetail);
        Assert.assertThat(questionDetail.getShortName(), is(title));
        Assert.assertThat(questionDetail.getText(), is(title));
        Assert.assertThat(questionDetail.getType(), is(QuestionType.DATE));
        Assert.assertEquals(questionDetail.getAnswerChoices(),Arrays.asList());
        Mockito.verify(questionDao, Mockito.times(1)).getDetails(questionId);
    }

    @Test
    public void testGetQuestionWithAnswerChoicesById() throws SystemException {
        int questionId = 1;
        String title = "Title";
        Mockito.when(questionDao.getDetails(questionId)).thenReturn(getQuestion(questionId, title, AnswerType.MULTISELECT, Arrays.asList(new QuestionChoiceEntity("choice1"), new QuestionChoiceEntity("choice2"))));
        QuestionDetail questionDetail = questionnaireService.getQuestion(questionId);
        Assert.assertNotNull(questionDetail);
        Assert.assertThat(questionDetail.getShortName(), is(title));
        Assert.assertThat(questionDetail.getText(), is(title));
        Assert.assertThat(questionDetail.getType(), is(QuestionType.MULTI_SELECT));
        Assert.assertEquals(questionDetail.getAnswerChoices(),Arrays.asList("choice1","choice2"));
        Mockito.verify(questionDao, Mockito.times(1)).getDetails(questionId);
    }

    @Test
    public void testGetQuestionByIdFailure() {
        int questionId = 1;
        Mockito.when(questionDao.getDetails(questionId)).thenReturn(null);
        try {
            questionnaireService.getQuestion(questionId);
            Assert.fail("Should raise application exception when question group is not present");
        } catch (SystemException e) {
            Mockito.verify(questionDao, Mockito.times(1)).getDetails(questionId);
            Assert.assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_NOT_FOUND));
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
        QuestionDefinition questionDefinition = new QuestionDefinition(QUESTION_TITLE, QuestionType.FREETEXT);
        Mockito.when(questionDao.retrieveCountOfQuestionsWithTitle(QUESTION_TITLE)).thenReturn(Arrays.asList((long) 0)).thenReturn(Arrays.asList((long) 1));
        Assert.assertEquals(false, questionnaireService.isDuplicateQuestionTitle(questionDefinition.getTitle()));
        Assert.assertEquals(true, questionnaireService.isDuplicateQuestionTitle(questionDefinition.getTitle()));
        Mockito.verify(questionDao, Mockito.times(2)).retrieveCountOfQuestionsWithTitle(QUESTION_TITLE);
    }

    @Test
    public void shouldGetAllQuestionGroupsByEventSource() throws SystemException {
        List<QuestionGroup> questionGroups = Arrays.asList(getQuestionGroup(1, "Title1", getSections("Section1")), getQuestionGroup(2, "Title2", getSections("Section2")));
        Mockito.when(questionGroupDao.retrieveQuestionGroupsByEventSource("Create", "Client")).thenReturn(questionGroups);
        List<QuestionGroupDetail> questionGroupDetails = questionnaireService.getQuestionGroups(new EventSource("Create", "Client", "Create.Client"));
        Assert.assertThat(questionGroupDetails, is(notNullValue()));
        Assert.assertThat(questionGroupDetails.size(), is(2));
        Assert.assertThat(questionGroupDetails.get(0).getTitle(), is("Title1"));
        Assert.assertThat(questionGroupDetails.get(1).getTitle(), is("Title2"));
        Mockito.verify(questionnaireValidator, Mockito.times(1)).validate(any(EventSource.class));
        Mockito.verify(questionGroupDao, Mockito.times(1)).retrieveQuestionGroupsByEventSource("Create", "Client");
    }
}
