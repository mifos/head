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

package org.mifos.platform.questionnaire.domain;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.business.EntityMaster;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.builders.ChoiceDetailBuilder;
import org.mifos.platform.questionnaire.builders.QuestionDtoBuilder;
import org.mifos.platform.questionnaire.builders.QuestionGroupDtoBuilder;
import org.mifos.platform.questionnaire.builders.QuestionGroupInstanceDtoBuilder;
import org.mifos.platform.questionnaire.builders.QuestionGroupResponseDtoBuilder;
import org.mifos.platform.questionnaire.builders.SectionDtoBuilder;
import org.mifos.platform.questionnaire.domain.ppi.PPISurveyLocator;
import org.mifos.platform.questionnaire.exceptions.MandatoryAnswerNotFoundException;
import org.mifos.platform.questionnaire.mappers.QuestionnaireMapper;
import org.mifos.platform.questionnaire.mappers.QuestionnaireMapperImpl;
import org.mifos.platform.questionnaire.parsers.QuestionGroupDefinitionParser;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupInstanceDao;
import org.mifos.platform.questionnaire.persistence.SectionDao;
import org.mifos.platform.questionnaire.persistence.SectionLinkDao;
import org.mifos.platform.questionnaire.persistence.SectionQuestionDao;
import org.mifos.platform.questionnaire.persistence.SectionQuestionLinkDao;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupResponseDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;
import org.mifos.platform.questionnaire.validators.QuestionnaireValidator;
import org.mifos.platform.validations.ValidationException;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireServiceTest {

    private QuestionnaireService questionnaireService;

    @Mock
    private QuestionnaireValidator questionnaireValidator;

    @Mock
    private QuestionDao questionDao;

    @Mock
    private QuestionGroupInstanceDao questionGroupInstanceDao;

    @Mock
    private QuestionGroupDao questionGroupDao;

    @Mock
    private SectionQuestionDao sectionQuestionDao;

    @Mock
    private EventSourceDao eventSourceDao;

    @Mock
    private PPISurveyLocator ppiSurveyLocator;

    @Mock
    private QuestionGroupDefinitionParser questionGroupDefinitionParser;
    
    @Mock
    private SectionDao sectionDao;
    
    @Mock
    private SectionLinkDao sectionLinkDao;
    
    @Mock
    private SectionQuestionLinkDao sectionQuestionLinkDao;
    
    private static final String QUESTION_TITLE = "Test QuestionDetail Title";
    private static final String QUESTION_GROUP_TITLE = "Question Group Title";
    public static final String EVENT_CREATE = "Create";
    public static final String SOURCE_CLIENT = "Client";
    private static final Random random = new Random();

    @Before
    public void setUp() {
        QuestionnaireMapper questionnaireMapper = new QuestionnaireMapperImpl(eventSourceDao, questionDao, questionGroupDao, sectionQuestionDao, questionGroupInstanceDao, sectionDao, sectionLinkDao, sectionQuestionLinkDao);
        questionnaireService = new QuestionnaireServiceImpl(questionnaireValidator, questionDao, questionnaireMapper,
                                            questionGroupDao, eventSourceDao, questionGroupInstanceDao,
                                            ppiSurveyLocator, questionGroupDefinitionParser, null);
    }
    
    @Test
    public void shouldDefineQuestion() throws SystemException {
        QuestionDetail questionDefinition = new QuestionDetail(QUESTION_TITLE, QuestionType.FREETEXT);
        try {
            QuestionDetail questionDetail = questionnaireService.defineQuestion(questionDefinition);
            verify(questionDao, times(1)).saveOrUpdate(any(QuestionEntity.class));
            Assert.assertNotNull(questionDetail);
            Assert.assertEquals(QUESTION_TITLE, questionDetail.getText());
            Assert.assertEquals(QuestionType.FREETEXT, questionDetail.getType());
        } catch (SystemException e) {
            Assert.fail("Should not have thrown the validation exception");
        }
        verify(questionnaireValidator).validateForDefineQuestion(questionDefinition);
        verify(questionDao).saveOrUpdate(any(QuestionEntity.class));
    }

    @Test
    public void shouldDefineQuestionWithAnswerChoices() throws SystemException {
        ChoiceDto choice1 = new ChoiceDto("choice1");
        ChoiceDto choice2 = new ChoiceDto("choice2");
        List<ChoiceDto> answerChoices = asList(choice1, choice2);
        QuestionDetail questionDefinition = new QuestionDetail(QUESTION_TITLE, QuestionType.MULTI_SELECT);
        questionDefinition.setAnswerChoices(answerChoices);
        try {
            QuestionDetail questionDetail = questionnaireService.defineQuestion(questionDefinition);
            verify(questionDao, times(1)).saveOrUpdate(any(QuestionEntity.class));
            Assert.assertNotNull(questionDetail);
            Assert.assertEquals(QUESTION_TITLE, questionDetail.getText());
            Assert.assertEquals(QuestionType.MULTI_SELECT, questionDetail.getType());
            Assert.assertEquals(choice1.getValue(), questionDetail.getAnswerChoices().get(0).getValue());
            Assert.assertEquals(choice2.getValue(), questionDetail.getAnswerChoices().get(1).getValue());
        } catch (SystemException e) {
            Assert.fail("Should not have thrown the validation exception");
        }
        verify(questionnaireValidator).validateForDefineQuestion(questionDefinition);
        verify(questionDao).saveOrUpdate(any(QuestionEntity.class));
    }

    @Test
    public void shouldDefineQuestionWithAnswerChoicesAndTags() throws SystemException {
        ChoiceDto choice1 = new ChoiceDto("choice1");
        choice1.setTags(asList("Tag1", "Tag2"));
        ChoiceDto choice2 = new ChoiceDto("choice2");
        choice2.setTags(asList("Tag3"));
        List<ChoiceDto> answerChoices = asList(choice1, choice2);
        QuestionDetail questionDefinition = new QuestionDetail(QUESTION_TITLE, QuestionType.MULTI_SELECT);
        questionDefinition.setAnswerChoices(answerChoices);
        try {
            QuestionDetail questionDetail = questionnaireService.defineQuestion(questionDefinition);
            verify(questionDao, times(1)).saveOrUpdate(any(QuestionEntity.class));
            Assert.assertNotNull(questionDetail);
            Assert.assertEquals(QUESTION_TITLE, questionDetail.getText());
            Assert.assertEquals(QuestionType.MULTI_SELECT, questionDetail.getType());
            Assert.assertEquals(choice1.getValue(), questionDetail.getAnswerChoices().get(0).getValue());
            Assert.assertEquals(choice1.getTags().get(0), questionDetail.getAnswerChoices().get(0).getTags().get(0));
            Assert.assertEquals(choice1.getTags().get(1), questionDetail.getAnswerChoices().get(0).getTags().get(1));
            Assert.assertEquals(choice2.getValue(), questionDetail.getAnswerChoices().get(1).getValue());
            Assert.assertEquals(choice2.getTags().get(0), questionDetail.getAnswerChoices().get(1).getTags().get(0));
        } catch (SystemException e) {
            Assert.fail("Should not have thrown the validation exception");
        }
        verify(questionnaireValidator).validateForDefineQuestion(questionDefinition);
        verify(questionDao).saveOrUpdate(any(QuestionEntity.class));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test(expected = SystemException.class)
    public void shouldThrowValidationExceptionWhenQuestionTitleIsNull() throws SystemException {
        QuestionDetail questionDefinition = new QuestionDetail(null, QuestionType.INVALID);
        doThrow(new SystemException(QuestionnaireConstants.QUESTION_TEXT_NOT_PROVIDED)).when(questionnaireValidator).validateForDefineQuestion(questionDefinition);
        questionnaireService.defineQuestion(questionDefinition);
        verify(questionnaireValidator).validateForDefineQuestion(questionDefinition);
    }

    @Test
    public void shouldGetAllActiveQuestions() {
        when(questionDao.retrieveByState(1)).thenReturn(asList(getQuestion(1, "q1", AnswerType.DATE), getQuestion(2, "q2", AnswerType.FREETEXT)));
        List<QuestionDetail> questionDetails = questionnaireService.getAllActiveQuestions(null);
        Assert.assertNotNull("getAllQuestions should not return null", questionDetails);
        verify(questionDao, times(1)).retrieveByState(1);

        assertThat(questionDetails.get(0).getText(), is("q1"));
        assertThat(questionDetails.get(0).getId(), is(1));
        assertThat(questionDetails.get(0).getType(), is(QuestionType.DATE));

        assertThat(questionDetails.get(1).getText(), is("q2"));
        assertThat(questionDetails.get(1).getId(), is(2));
        assertThat(questionDetails.get(1).getType(), is(QuestionType.FREETEXT));

    }

    @Test
    public void shouldGetAllActiveQuestionsWithoutExcludedQuestions() {
        List<Integer> excludedQuestions = asList(3);
        List<QuestionEntity> questionEntities = asList(getQuestion(1, "q1", AnswerType.DATE), getQuestion(2, "q2", AnswerType.FREETEXT));
        when(questionDao.retrieveByStateExcluding(excludedQuestions, 1)).thenReturn(questionEntities);
        List<QuestionDetail> questionDetails = questionnaireService.getAllActiveQuestions(excludedQuestions);
        Assert.assertNotNull("getAllQuestions should not return null", questionDetails);
        verify(questionDao, times(1)).retrieveByStateExcluding(excludedQuestions, 1);

        assertThat(questionDetails.get(0).getText(), is("q1"));
        assertThat(questionDetails.get(0).getId(), is(1));
        assertThat(questionDetails.get(0).getType(), is(QuestionType.DATE));

        assertThat(questionDetails.get(1).getText(), is("q2"));
        assertThat(questionDetails.get(1).getId(), is(2));
        assertThat(questionDetails.get(1).getType(), is(QuestionType.FREETEXT));

    }

    @Test
    public void shouldGetAllQuestions() {
        when(questionDao.retrieveAll()).thenReturn(asList(getQuestion(1, "q1", AnswerType.DATE), getQuestion(2, "q2", AnswerType.FREETEXT)));
        List<QuestionDetail> questionDetails = questionnaireService.getAllQuestions();
        Assert.assertNotNull("getAllQuestions should not return null", questionDetails);
        verify(questionDao, times(1)).retrieveAll();

        assertThat(questionDetails.get(0).getText(), is("q1"));
        assertThat(questionDetails.get(0).getId(), is(1));
        assertThat(questionDetails.get(0).getType(), is(QuestionType.DATE));

        assertThat(questionDetails.get(1).getText(), is("q2"));
        assertThat(questionDetails.get(1).getId(), is(2));
        assertThat(questionDetails.get(1).getType(), is(QuestionType.FREETEXT));

    }

    private QuestionEntity getQuestion(int id, String text, AnswerType type) {
        QuestionEntity question = new QuestionEntity();
        question.setQuestionId(id);
        question.setQuestionText(text);
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
    @Ignore
    public void shouldDefineQuestionGroup() throws SystemException {
        QuestionGroupDetail questionGroupDefinition = getQuestionGroupDetail(EVENT_CREATE, SOURCE_CLIENT, "S1", "S2");
        setUpEventSourceExpectations(EVENT_CREATE, SOURCE_CLIENT);
        when(questionDao.getDetails(anyInt())).thenReturn(getQuestion(11), getQuestion(12), getQuestion(11), getQuestion(12));
        try {
            QuestionGroupDetail questionGroupDetail = questionnaireService.defineQuestionGroup(questionGroupDefinition);
            assertQuestionGroupDetail(questionGroupDetail);
            verify(questionnaireValidator).validateForDefineQuestionGroup(questionGroupDefinition);
            verify(questionGroupDao, times(1)).create(any(QuestionGroup.class));
            verify(eventSourceDao, times(1)).retrieveByEventAndSource(anyString(), anyString());
            verify(questionDao, times(4)).getDetails(anyInt());
        } catch (SystemException e) {
            Assert.fail("Should not have thrown the validation exception");
        }
    }

    private QuestionEntity getQuestion(int questionId) {
        QuestionEntity question = new QuestionEntity();
        question.setQuestionId(questionId);
        question.setChoices(new LinkedList<QuestionChoiceEntity>());
        return question;
    }

    private void assertQuestionGroupDetail(QuestionGroupDetail questionGroupDetail) {
        Assert.assertNotNull(questionGroupDetail);
        Assert.assertEquals(QUESTION_GROUP_TITLE, questionGroupDetail.getTitle());
        assertSections(questionGroupDetail.getSectionDetails());
        assertEvent(questionGroupDetail.getEventSources().get(0));
    }

    private void assertEvent(EventSourceDto eventSourceDto) {
        assertThat(eventSourceDto, notNullValue());
        assertThat(eventSourceDto.getEvent(), is(EVENT_CREATE));
        assertThat(eventSourceDto.getSource(), is(SOURCE_CLIENT));
    }

    private void assertSections(List<SectionDetail> sectionDetails) {
        Assert.assertNotNull(sectionDetails);
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
        return new QuestionGroupDetail(0, QUESTION_GROUP_TITLE, Arrays.asList(getEventSource(event, source)), getSectionDefinitions(sectionNames), false);
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

    private EventSourceDto getEventSource(String event, String source) {
        return new EventSourceDto(event, source, null);
    }

    private SectionDetail getSectionDefinition(String name) {
        SectionDetail section = new SectionDetail();
        section.setName(name);
        section.addQuestion(new SectionQuestionDetail(new QuestionDetail(11, null, QuestionType.INVALID, true, true), true));
        section.addQuestion(new SectionQuestionDetail(new QuestionDetail(12, null, QuestionType.INVALID, true, true), false));
        return section;
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test(expected = SystemException.class)
    public void shouldThrowValidationExceptionWhenQuestionGroupTitleIsNull() throws SystemException {
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(0, null, null, asList(getSectionDefinition("S1")), false);
        doThrow(new SystemException(QuestionnaireConstants.QUESTION_GROUP_TITLE_NOT_PROVIDED)).when(questionnaireValidator).validateForDefineQuestionGroup(questionGroupDetail);
        questionnaireService.defineQuestionGroup(questionGroupDetail);
        verify(questionnaireValidator).validateForDefineQuestionGroup(questionGroupDetail);
    }

    @Test
    public void shouldGetAllQuestionGroups() {
        QuestionGroup questionGroup1 = getQuestionGroup(0, "QG0","View","Loan", QuestionGroupState.ACTIVE, true, getSections("S0_0"));
        QuestionGroup questionGroup2 = getQuestionGroup(1, "QG1","View","Loan", QuestionGroupState.ACTIVE, true, getSections("S1_0", "S1_1"));
        when(questionGroupDao.getDetailsAll()).thenReturn(asList(questionGroup1, questionGroup2));
        List<QuestionGroupDetail> questionGroupDetails = questionnaireService.getAllQuestionGroups();
        Assert.assertNotNull("getAllQuestionGroups should not return null", questionGroupDetails);
        for (int i = 0; i < questionGroupDetails.size(); i++) {
            assertThat(questionGroupDetails.get(i).getId(), is(i));
            assertThat(questionGroupDetails.get(i).getTitle(), is("QG" + i));
            List<SectionDetail> sectionDetails = questionGroupDetails.get(i).getSectionDetails();
            for (int j = 0; j < sectionDetails.size(); j++) {
                assertThat(sectionDetails.get(j).getName(), is("S" + i + "_" + j));
            }
        }
    }

    private List<Section> getSections(String... names) {
        List<Section> sectionList = new ArrayList<Section>();
        for (String name : names) {
            Section section = new Section(name);
            SectionQuestion sectionQuestion = new SectionQuestion();
            QuestionEntity questionEntity = new QuestionEntity();
            questionEntity.setChoices(new LinkedList<QuestionChoiceEntity>());
            sectionQuestion.setQuestion(questionEntity);
            section.setQuestions(asList(sectionQuestion));
            sectionList.add(section);
        }
        return sectionList;
    }

    private Section getSectionWithQuestions(int sectionQuestionId, String sectionName, String... questionNames) {
        Section section = new Section(sectionName);
        List<SectionQuestion> sectionQuestions = new ArrayList<SectionQuestion>();
        for (int i = 0; i < questionNames.length; i++) {
            SectionQuestion sectionQuestion = new SectionQuestion();
            sectionQuestion.setId(sectionQuestionId);
            sectionQuestion.setSection(section);
            QuestionEntity questionEntity = new QuestionEntity();
            questionEntity.setQuestionText(questionNames[i]);
            setEveryOtherStateActive(i, questionEntity);
            questionEntity.setChoices(new LinkedList<QuestionChoiceEntity>());
            sectionQuestion.setQuestion(questionEntity);
            sectionQuestions.add(sectionQuestion);
        }
        section.setQuestions(sectionQuestions);
        return section;
    }

    private void setEveryOtherStateActive(int i, QuestionEntity questionEntity) {
        if (i % 2 == 0) {
            questionEntity.setQuestionState(QuestionState.INACTIVE);
        } else {
            questionEntity.setQuestionState(QuestionState.ACTIVE);
        }
    }

    private Section getSectionWithOneMultiSelectQuestion(int sectionQuestionId, String sectionName, String questionName, String... choices) {
        Section section = new Section(sectionName);
        List<SectionQuestion> sectionQuestions = new ArrayList<SectionQuestion>();
        SectionQuestion sectionQuestion = new SectionQuestion();
        sectionQuestion.setId(sectionQuestionId);
        sectionQuestion.setSection(section);
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setQuestionText(questionName);
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

    @Test
    public void shouldGetAllEventSources() {
        when(eventSourceDao.retrieveAllEventSourcesOrdered()).thenReturn(asList(getEventSourceEntity("Create", "Client")));
        List<EventSourceDto> eventSourceDtos = questionnaireService.getAllEventSources();
        assertThat(eventSourceDtos, notNullValue());
        assertThat(eventSourceDtos.size(), is(1));
        assertThat(eventSourceDtos.get(0).getEvent(), is("Create"));
        assertThat(eventSourceDtos.get(0).getSource(), is("Client"));
    }

    @Test
    public void testGetQuestionGroupByIdSuccess() throws SystemException {
        int questionGroupId = 1;
        String title = "Title";
        when(questionGroupDao.getDetails(questionGroupId)).thenReturn(getQuestionGroup(questionGroupId, title, getSections("S1", "S2")));
        QuestionGroupDetail groupDetail = questionnaireService.getQuestionGroup(questionGroupId);
        Assert.assertNotNull(groupDetail);
        assertThat(groupDetail.getTitle(), is(title));
        verify(questionGroupDao, times(1)).getDetails(questionGroupId);
    }

    @Test
    public void testGetQuestionGroupByIdFailure() {
        int questionGroupId = 1;
        when(questionGroupDao.getDetails(questionGroupId)).thenReturn(null);
        try {
            questionnaireService.getQuestionGroup(questionGroupId);
            Assert.fail("Should raise application exception when question group is not present");
        } catch (SystemException e) {
            verify(questionGroupDao, times(1)).getDetails(questionGroupId);
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        }
    }

    @Test
    public void testGetQuestionByIdSuccess() throws SystemException {
        int questionId = 1;
        String title = "Title";
        when(questionDao.getDetails(questionId)).thenReturn(getQuestion(questionId, title, AnswerType.DATE));
        QuestionDetail questionDetail = questionnaireService.getQuestion(questionId);
        Assert.assertNotNull(questionDetail);
        assertThat(questionDetail.getText(), is(title));
        assertThat(questionDetail.getType(), is(QuestionType.DATE));
        verify(questionDao, times(1)).getDetails(questionId);
    }

    @Test
    public void testGetNumericQuestionByIdSuccess() throws SystemException {
        int questionId = 1;
        String title = "Title";
        QuestionEntity question = getQuestion(questionId, title, AnswerType.NUMBER);
        question.setNumericMin(10);
        question.setNumericMax(100);
        when(questionDao.getDetails(questionId)).thenReturn(question);
        QuestionDetail questionDetail = questionnaireService.getQuestion(questionId);
        Assert.assertNotNull(questionDetail);
        assertThat(questionDetail.getText(), is(title));
        assertThat(questionDetail.getType(), is(QuestionType.NUMERIC));
        assertThat(questionDetail.getNumericMin(), is(10));
        assertThat(questionDetail.getNumericMax(), is(100));
        verify(questionDao, times(1)).getDetails(questionId);
    }

    @Test
    public void testGetMultiSelectQuestionById() throws SystemException {
        int questionId = 1;
        String title = "Title";
        List<QuestionChoiceEntity> questionChoiceEntities = asList(new QuestionChoiceEntity("choice1"), new QuestionChoiceEntity("choice2"));
        when(questionDao.getDetails(questionId)).thenReturn(getQuestion(questionId, title, AnswerType.MULTISELECT, questionChoiceEntities));
        QuestionDetail questionDetail = questionnaireService.getQuestion(questionId);
        Assert.assertNotNull(questionDetail);
        assertThat(questionDetail.getText(), is(title));
        assertThat(questionDetail.getType(), is(QuestionType.MULTI_SELECT));
        Assert.assertEquals("choice1", questionDetail.getAnswerChoices().get(0).getValue());
        Assert.assertEquals("choice2", questionDetail.getAnswerChoices().get(1).getValue());
        verify(questionDao, times(1)).getDetails(questionId);
    }

    @Test
    public void testGetSingleSelectQuestionById() throws SystemException {
        int questionId = 1;
        String title = "Title";
        List<QuestionChoiceEntity> questionChoices = asList(new QuestionChoiceEntity("choice1"), new QuestionChoiceEntity("choice2"));
        when(questionDao.getDetails(questionId)).thenReturn(getQuestion(questionId, title, AnswerType.SINGLESELECT, questionChoices));
        QuestionDetail questionDetail = questionnaireService.getQuestion(questionId);
        Assert.assertNotNull(questionDetail);
        Assert.assertThat(questionDetail.getText(), is(title));
        Assert.assertThat(questionDetail.getType(), is(QuestionType.SINGLE_SELECT));
        Assert.assertEquals("choice1", questionDetail.getAnswerChoices().get(0).getValue());
        Assert.assertEquals("choice2", questionDetail.getAnswerChoices().get(1).getValue());
        verify(questionDao, times(1)).getDetails(questionId);
    }

    @Test
    public void testGetQuestionByIdFailure() {
        int questionId = 1;
        when(questionDao.getDetails(questionId)).thenReturn(null);
        try {
            questionnaireService.getQuestion(questionId);
            Assert.fail("Should raise application exception when question group is not present");
        } catch (SystemException e) {
            verify(questionDao, times(1)).getDetails(questionId);
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_NOT_FOUND));
        }
    }

    @Test
    public void shouldCheckDuplicates() {
        QuestionDefinition questionDefinition = new QuestionDefinition(QUESTION_TITLE, QuestionType.FREETEXT);
        when(questionDao.retrieveCountOfQuestionsWithText(QUESTION_TITLE)).thenReturn(asList((long) 0)).thenReturn(asList((long) 1));
        Assert.assertEquals(false, questionnaireService.isDuplicateQuestionText(questionDefinition.getTitle()));
        Assert.assertEquals(true, questionnaireService.isDuplicateQuestionText(questionDefinition.getTitle()));
        verify(questionDao, times(2)).retrieveCountOfQuestionsWithText(QUESTION_TITLE);
    }

    @Test
    public void shouldGetAllQuestionGroupsByEventSource() throws SystemException {
        List<QuestionGroup> questionGroups = asList(getQuestionGroup(1, "Title1","View","Loan", QuestionGroupState.ACTIVE, true, getSections("Section1")), getQuestionGroup(2, "Title2","View","Loan", QuestionGroupState.ACTIVE, true, getSections("Section2")));
        when(questionGroupDao.retrieveQuestionGroupsByEventSource("Create", "Client")).thenReturn(questionGroups);
        List<QuestionGroupDetail> questionGroupDetails = questionnaireService.getQuestionGroups(new EventSourceDto("Create", "Client", "Create.Client"));
        assertThat(questionGroupDetails, is(notNullValue()));
        assertThat(questionGroupDetails.size(), is(2));
        assertThat(questionGroupDetails.get(0).getTitle(), is("Title1"));
        assertThat(questionGroupDetails.get(1).getTitle(), is("Title2"));
        verify(questionnaireValidator, times(1)).validateForEventSource(any(EventSourceDto.class));
        verify(questionGroupDao, times(1)).retrieveQuestionGroupsByEventSource("Create", "Client");
    }

    @Test
    public void shouldGetAllQuestionGroupsByEventSourceAndEntityId() {
        QuestionGroup questionGroup1 = getQuestionGroup(1, "Title1","View","Loan", QuestionGroupState.ACTIVE, true, asList(getSectionWithQuestions(222, "Section1", "Question1", "Question2")));
        QuestionGroup questionGroup2 = getQuestionGroup(2, "Title2","View","Loan", QuestionGroupState.ACTIVE, true, asList(getSectionWithQuestions(222, "Section2", "Question2"),
                getSectionWithQuestions(222, "SectionN", "q1", "q2", "q3", "q4")));
        QuestionGroup questionGroup3 = getQuestionGroup(3, "Title3","View","Loan", QuestionGroupState.ACTIVE, true, asList(getSectionWithOneMultiSelectQuestion(222, "Section3", "Question3", "Choice1", "Choice2", "Choice3", "Choice4")));
        List<QuestionGroup> questionGroups = asList(questionGroup1, questionGroup2, questionGroup3);
        when(questionGroupDao.retrieveQuestionGroupsByEventSource("Create", "Client")).thenReturn(questionGroups);
        List<QuestionGroupDetail> questionGroupDetails = questionnaireService.getQuestionGroups(new EventSourceDto("Create", "Client", "Create.Client"));
        assertThat(questionGroupDetails, is(notNullValue()));
        assertThat(questionGroupDetails.size(), is(3));
        assertThat(questionGroupDetails.get(0).getSectionDetails().size(), is(1));
        assertThat(questionGroupDetails.get(1).getSectionDetails().size(), is(1));
        assertThat(questionGroupDetails.get(1).getSectionDetails().get(0).getName(), is("SectionN"));
        assertThat(questionGroupDetails.get(1).getSectionDetails().get(0).getQuestions().size(), is(2));
        assertThat(questionGroupDetails.get(1).getSectionDetails().get(0).getQuestions().get(0).getText(), is("q2"));
        assertThat(questionGroupDetails.get(1).getSectionDetails().get(0).getQuestions().get(1).getText(), is("q4"));
        assertThat(questionGroupDetails.get(2).getSectionDetails().size(), is(1));
    }

    private QuestionGroupInstance getQuestionGroupInstance(int entityId, int version, QuestionGroup questionGroup, String... responses) {
        QuestionGroupInstance questionGroupInstance = new QuestionGroupInstance();
        questionGroupInstance.setQuestionGroup(questionGroup);
        questionGroupInstance.setCompletedStatus(1);
        questionGroupInstance.setCreatorId(122);
        questionGroupInstance.setDateConducted(Calendar.getInstance().getTime());
        questionGroupInstance.setEntityId(entityId);
        questionGroupInstance.setVersionNum(version);
        List<QuestionGroupResponse> groupResponses = new ArrayList<QuestionGroupResponse>();
        for (int i = 0; i < responses.length; i++) {
            groupResponses.add(getQuestionGroupResponse(responses[i], questionGroupInstance, questionGroup.getSections().get(i).getQuestions().get(0)));
        }
        questionGroupInstance.setQuestionGroupResponses(groupResponses);
        return questionGroupInstance;
    }

    private QuestionGroupInstance getQuestionGroupInstance(int entityId, int year, int month, int day, QuestionGroup questionGroup, String... responses) {
        QuestionGroupInstance questionGroupInstance = new QuestionGroupInstance();
        questionGroupInstance.setId(random.nextInt());
        questionGroupInstance.setQuestionGroup(questionGroup);
        questionGroupInstance.setCompletedStatus(1);
        questionGroupInstance.setCreatorId(122);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        questionGroupInstance.setDateConducted(calendar.getTime());
        questionGroupInstance.setEntityId(entityId);
        questionGroupInstance.setVersionNum(1);
        List<QuestionGroupResponse> groupResponses = new ArrayList<QuestionGroupResponse>();
        for (int i = 0; i < responses.length; i++) {
            groupResponses.add(getQuestionGroupResponse(responses[i], questionGroupInstance, questionGroup.getSections().get(i).getQuestions().get(0)));
        }
        questionGroupInstance.setQuestionGroupResponses(groupResponses);
        return questionGroupInstance;
    }

    private QuestionGroupResponse getQuestionGroupResponse(String responses, QuestionGroupInstance questionGroupInstance, SectionQuestion sectionQuestion) {
        QuestionGroupResponse questionGroupResponse = new QuestionGroupResponse();
        questionGroupResponse.setResponse(responses);
        questionGroupResponse.setQuestionGroupInstance(questionGroupInstance);
        questionGroupResponse.setSectionQuestion(sectionQuestion);
        return questionGroupResponse;
    }

    @Test
    public void shouldSaveResponses() {
        List<QuestionDetail> questionDetails = asList(new QuestionDetail(12, "Question 1", QuestionType.FREETEXT, true, true));
        List<SectionDetail> sectionDetails = asList(getSectionDetailWithQuestions("Sec1", questionDetails, "value", false));
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(1, "QG1", Arrays.asList(new EventSourceDto("Create", "Client", null)), sectionDetails, true);
        QuestionGroupInstance questionGroupInstance = new QuestionGroupInstance();
        questionGroupInstance.setVersionNum(0);
        when(questionGroupInstanceDao.retrieveLatestQuestionGroupInstanceByQuestionGroupAndEntity(1, 1)).thenReturn(asList(questionGroupInstance));
        questionnaireService.saveResponses(new QuestionGroupDetails(1, 1, 1, asList(questionGroupDetail)));
        verify(questionGroupInstanceDao).retrieveLatestQuestionGroupInstanceByQuestionGroupAndEntity(1, 1);
        verify(questionnaireValidator, times(1)).validateForQuestionGroupResponses(asList(questionGroupDetail));
        verify(questionGroupInstanceDao, times(1)).saveOrUpdateAll(Matchers.<List<QuestionGroupInstance>>any()); // TODO: Verify the contents using a custom matcher
    }

    @Test
    public void testValidateResponse() {
        List<QuestionDetail> questionDetails = asList(new QuestionDetail(12, "Question 1", QuestionType.FREETEXT, true, true));
        List<SectionDetail> sectionDetails = asList(getSectionDetailWithQuestions("Sec1", questionDetails, null, true));
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(1, "QG1", Arrays.asList(new EventSourceDto("Create", "Client", null)), sectionDetails, true);
        try {
            doThrow(new MandatoryAnswerNotFoundException("Title")).
                    when(questionnaireValidator).validateForQuestionGroupResponses(asList(questionGroupDetail));
            questionnaireService.validateResponses(asList(questionGroupDetail));
            Assert.fail("Should not have thrown the validation exception");
        } catch (ValidationException e) {
            verify(questionnaireValidator, times(1)).validateForQuestionGroupResponses(asList(questionGroupDetail));
        }
    }

    @Test
    public void shouldGetQuestionGroupInstances() {
        List<Section> sections = new ArrayList<Section>();
        List<QuestionGroupInstance> questionGroupInstances = asList(getQuestionGroupInstance(3031, 2010, 7, 26, getQuestionGroup(4041, "QG1", sections)),
                getQuestionGroupInstance(3032, 2010, 7, 26, getQuestionGroup(4042, "QG2", sections)),
                getQuestionGroupInstance(3033, 2010, 7, 26, getQuestionGroup(4043, "QG3", sections)),
                getQuestionGroupInstance(3034, 2010, 7, 25, getQuestionGroup(4041, "QG1", sections)),
                getQuestionGroupInstance(3035, 2010, 7, 25, getQuestionGroup(4042, "QG2", sections)),
                getQuestionGroupInstance(3036, 2010, 7, 24, getQuestionGroup(4042, "QG2", sections)),
                getQuestionGroupInstance(3037, 2010, 7, 24, getQuestionGroup(4043, "QG3", sections)));
        when(questionGroupInstanceDao.retrieveQuestionGroupInstancesByEntityIdAndEventSourceId(101, 202)).thenReturn(questionGroupInstances);
        when(eventSourceDao.retrieveByEventAndSource("View", "Client")).thenReturn(asList(getEventSourceEntity(202)));
        EventSourceDto eventSourceDto = new EventSourceDto("View", "Client", "View.Client");
        List<QuestionGroupInstanceDetail> instances = questionnaireService.getQuestionGroupInstances(101, eventSourceDto, false, false);
        assertThat(instances, is(notNullValue()));
        assertThat(instances.size(), is(7));
        assertQuestionGroupInstanceDetail(instances.get(0), "QG1", 2010, 7, 26, questionGroupInstances.get(0).getId());
        assertQuestionGroupInstanceDetail(instances.get(1), "QG2", 2010, 7, 26, questionGroupInstances.get(1).getId());
        assertQuestionGroupInstanceDetail(instances.get(2), "QG3", 2010, 7, 26, questionGroupInstances.get(2).getId());
        assertQuestionGroupInstanceDetail(instances.get(3), "QG1", 2010, 7, 25, questionGroupInstances.get(3).getId());
        assertQuestionGroupInstanceDetail(instances.get(4), "QG2", 2010, 7, 25, questionGroupInstances.get(4).getId());
        assertQuestionGroupInstanceDetail(instances.get(5), "QG2", 2010, 7, 24, questionGroupInstances.get(5).getId());
        assertQuestionGroupInstanceDetail(instances.get(6), "QG3", 2010, 7, 24, questionGroupInstances.get(6).getId());
        verify(questionGroupInstanceDao, times(1)).retrieveQuestionGroupInstancesByEntityIdAndEventSourceId(101, 202);
        verify(questionnaireValidator, times(1)).validateForEventSource(eventSourceDto);
        verify(eventSourceDao, times(1)).retrieveByEventAndSource("View", "Client");
    }

    @Test
    public void shouldGetLatestQuestionGroupInstances() {
        List<Section> sections = new ArrayList<Section>();
        List<QuestionGroupInstance> questionGroupInstances = asList(
                getQuestionGroupInstance(3033, 2010, 7, 26, getQuestionGroup(4043, "QG2", sections)),
                getQuestionGroupInstance(3034, 2010, 7, 25, getQuestionGroup(4041, "QG1", sections)),
                getQuestionGroupInstance(3037, 2010, 7, 24, getQuestionGroup(4043, "QG3", sections)));
        when(questionGroupInstanceDao.retrieveLatestQuestionGroupInstancesByEntityIdAndEventSourceId(101, 202)).thenReturn(questionGroupInstances);
        when(eventSourceDao.retrieveByEventAndSource("View", "Client")).thenReturn(asList(getEventSourceEntity(202)));
        EventSourceDto eventSourceDto = new EventSourceDto("View", "Client", "View.Client");
        List<QuestionGroupInstanceDetail> instances = questionnaireService.getQuestionGroupInstances(101, eventSourceDto, false, true);
        assertThat(instances, is(notNullValue()));
        assertThat(instances.size(), is(3));
        assertQuestionGroupInstanceDetail(instances.get(0), "QG2", 2010, 7, 26, questionGroupInstances.get(0).getId());
        assertQuestionGroupInstanceDetail(instances.get(1), "QG1", 2010, 7, 25, questionGroupInstances.get(1).getId());
        assertQuestionGroupInstanceDetail(instances.get(2), "QG3", 2010, 7, 24, questionGroupInstances.get(2).getId());
        verify(questionGroupInstanceDao, times(1)).retrieveLatestQuestionGroupInstancesByEntityIdAndEventSourceId(101, 202);
        verify(questionnaireValidator, times(1)).validateForEventSource(eventSourceDto);
        verify(eventSourceDao, times(1)).retrieveByEventAndSource("View", "Client");
    }

    @Test
    public void shouldGetQuestionGroupInstancesIncludingUnansweredQuestionGroups() {
        List<Section> sections = new ArrayList<Section>();
        List<QuestionGroup> questionGroups = asList(getQuestionGroup(4041, "QG1", sections),
                getQuestionGroup(4042, "QG2", sections),
                getQuestionGroup(4043, "QG3", sections),
                getQuestionGroup(4044, "QG4", sections));
        List<QuestionGroupInstance> questionGroupInstances = asList(getQuestionGroupInstance(3031, 2010, 7, 26, questionGroups.get(0)),
                getQuestionGroupInstance(3032, 2010, 7, 26, questionGroups.get(1)),
                getQuestionGroupInstance(3033, 2010, 7, 26, questionGroups.get(2)),
                getQuestionGroupInstance(3034, 2010, 7, 25, questionGroups.get(0)),
                getQuestionGroupInstance(3035, 2010, 7, 25, questionGroups.get(1)),
                getQuestionGroupInstance(3036, 2010, 7, 24, questionGroups.get(1)),
                getQuestionGroupInstance(3037, 2010, 7, 24, questionGroups.get(2)));
        when(questionGroupDao.retrieveQuestionGroupsByEventSource("View", "Client")).thenReturn(questionGroups);
        when(questionGroupInstanceDao.retrieveQuestionGroupInstancesByEntityIdAndEventSourceId(101, 202)).thenReturn(questionGroupInstances);
        when(eventSourceDao.retrieveByEventAndSource("View", "Client")).thenReturn(asList(getEventSourceEntity(202)));
        EventSourceDto eventSourceDto = new EventSourceDto("View", "Client", "View.Client");
        List<QuestionGroupInstanceDetail> instances = questionnaireService.getQuestionGroupInstances(101, eventSourceDto, true, false);
        assertThat(instances, is(notNullValue()));
        assertThat(instances.size(), is(8));
        assertQuestionGroupInstanceDetail(instances.get(0), "QG1", 2010, 7, 26, questionGroupInstances.get(0).getId());
        assertQuestionGroupInstanceDetail(instances.get(1), "QG2", 2010, 7, 26, questionGroupInstances.get(1).getId());
        assertQuestionGroupInstanceDetail(instances.get(2), "QG3", 2010, 7, 26, questionGroupInstances.get(2).getId());
        assertQuestionGroupInstanceDetail(instances.get(3), "QG1", 2010, 7, 25, questionGroupInstances.get(3).getId());
        assertQuestionGroupInstanceDetail(instances.get(4), "QG2", 2010, 7, 25, questionGroupInstances.get(4).getId());
        assertQuestionGroupInstanceDetail(instances.get(5), "QG2", 2010, 7, 24, questionGroupInstances.get(5).getId());
        assertQuestionGroupInstanceDetail(instances.get(6), "QG3", 2010, 7, 24, questionGroupInstances.get(6).getId());
        verify(questionGroupInstanceDao, times(1)).retrieveQuestionGroupInstancesByEntityIdAndEventSourceId(101, 202);
        verify(questionnaireValidator, times(1)).validateForEventSource(eventSourceDto);
        verify(eventSourceDao, times(1)).retrieveByEventAndSource("View", "Client");
    }

    @Test
    public void shouldGetQuestionGroupInstance() {
        QuestionGroupInstance questionGroupInstance = getQuestionGroupInstance(12, 1, getQuestionGroup(54, "QG1", asList(getSectionWithQuestions(89, "Sec1", "Ques1"))), "Hello");
        when(questionGroupInstanceDao.getDetails(1212)).thenReturn(questionGroupInstance);
        QuestionGroupInstanceDetail questionGroupInstanceDetail = questionnaireService.getQuestionGroupInstance(1212);
        assertThat(questionGroupInstanceDetail, is(notNullValue()));
        assertThat(questionGroupInstanceDetail.getQuestionGroupTitle(), is("QG1"));
        verify(questionGroupInstanceDao, times(1)).getDetails(1212);
    }

    @Test
    public void shouldDefineQuestionGroupFromDto() {
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionnaireService.defineQuestionGroup(questionGroupDto);
        verify(questionnaireValidator).validateForDefineQuestionGroup(questionGroupDto, true);
        verify(questionGroupDao).create(any(QuestionGroup.class));
    }

    @Test
    public void shouldDefineQuestionGroupFromDto_WithExistingQuestions() {
        QuestionEntity questionEntity = new QuestionEntity("Ques2");
        questionEntity.setAnswerType(AnswerType.SINGLESELECT);
        questionEntity.setChoices(asList(new QuestionChoiceEntity("Choice1"), new QuestionChoiceEntity("Choice2")));
        when(questionDao.retrieveByText("Ques2")).thenReturn(asList(questionEntity));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionnaireService.defineQuestionGroup(questionGroupDto);
        verify(questionnaireValidator).validateForDefineQuestionGroup(questionGroupDto, true);
        verify(questionGroupDao).create(argThat(new QuestionGroupContainsQuestionMatcher(questionEntity)));
    }

    /*
     * If we pass try creating a question group with a question that has a unique nickname,
     * then a new question should be created even if the two questions are exactly the same.
     */
    @Test
    public void shouldDefineQuestionGroupFromDto_WithExistingQuestionTextMatchAndNicknameMismatch() {
        QuestionEntity newQuestionEntityFromDto = new QuestionEntity("Ques2");
        newQuestionEntityFromDto.setAnswerType(AnswerType.SINGLESELECT);
        newQuestionEntityFromDto.setNickname("Ques2_nick");
        newQuestionEntityFromDto.setChoices(asList(new QuestionChoiceEntity("Ch1"), new QuestionChoiceEntity("Ch2"), new QuestionChoiceEntity("Ch3")));
        QuestionEntity existingQuestionEntityWithMatchingName = new QuestionEntity("Ques2");
        existingQuestionEntityWithMatchingName.setAnswerType(AnswerType.SINGLESELECT);
        existingQuestionEntityWithMatchingName.setNickname("nick2");
        existingQuestionEntityWithMatchingName.setChoices(asList(new QuestionChoiceEntity("Ch1"), new QuestionChoiceEntity("Ch2"), new QuestionChoiceEntity("Ch3")));
        when(questionDao.retrieveByText("Ques2")).thenReturn(asList(existingQuestionEntityWithMatchingName));
        QuestionGroupDto questionGroupDto = getQuestionGroupDtoWithNicknames();
        questionnaireService.defineQuestionGroup(questionGroupDto);
        verify(questionnaireValidator).validateForDefineQuestionGroup(questionGroupDto, true);
        verify(questionGroupDao).create(argThat(new QuestionGroupContainsQuestionNicknameMatcher(newQuestionEntityFromDto)));
    }

    private QuestionGroupDto getQuestionGroupDto() {
        QuestionDto question1 = new QuestionDtoBuilder().withText("Ques1").withMandatory(true).withType(QuestionType.FREETEXT).build();
        ChoiceDto choice1 = new ChoiceDetailBuilder().withValue("Ch1").withOrder(1).build();
        ChoiceDto choice2 = new ChoiceDetailBuilder().withValue("Ch2").withOrder(2).build();
        ChoiceDto choice3 = new ChoiceDetailBuilder().withValue("Ch3").withOrder(3).build();
        QuestionDto question2 = new QuestionDtoBuilder().withText("Ques2").withType(QuestionType.SINGLE_SELECT).addChoices(choice1, choice2, choice3).build();
        SectionDto section = new SectionDtoBuilder().withName("Sec1").withOrder(1).addQuestions(question1, question2).build();
        return new QuestionGroupDtoBuilder().withTitle("QG1").withEventSource("Create", "Client").addSections(section).build();
    }

    private QuestionGroupDto getQuestionGroupDtoWithNicknames() {
        QuestionDto question1 = new QuestionDtoBuilder().withText("Ques1").withNickname("Ques1_nick").withMandatory(true).withType(QuestionType.FREETEXT).build();
        ChoiceDto choice1 = new ChoiceDetailBuilder().withValue("Ch1").withOrder(1).build();
        ChoiceDto choice2 = new ChoiceDetailBuilder().withValue("Ch2").withOrder(2).build();
        ChoiceDto choice3 = new ChoiceDetailBuilder().withValue("Ch3").withOrder(3).build();
        QuestionDto question2 = new QuestionDtoBuilder().withText("Ques2").withNickname("Ques2_nick").withType(QuestionType.SINGLE_SELECT).addChoices(choice1, choice2, choice3).build();
        SectionDto section = new SectionDtoBuilder().withName("Sec1").withOrder(1).addQuestions(question1, question2).build();
        return new QuestionGroupDtoBuilder().withTitle("QG1").withEventSource("Create", "Client").addSections(section).build();
    }
    @Test
    public void shouldGetAllCountriesForPPI() throws IOException {
        when(ppiSurveyLocator.getAllPPISurveyFiles()).thenReturn(asList("PPISurveyINDIA.xml", "PPISurveyCHINA.xml", "PPISurveyCANADA.xml"));
        List<String> countriesForPPI = questionnaireService.getAllCountriesForPPI();
        assertThat(countriesForPPI, is(notNullValue()));
        assertThat(countriesForPPI.size(), is(3));
        assertThat(countriesForPPI.get(0), is("INDIA"));
        assertThat(countriesForPPI.get(1), is("CHINA"));
        assertThat(countriesForPPI.get(2), is("CANADA"));
        verify(ppiSurveyLocator).getAllPPISurveyFiles();
    }

    @Test
    public void shouldUploadPPIQuestionGroup() throws IOException {
        String ppiXmlPath = "org/mifos/platform/questionnaire/PPISurveyINDIA.xml";
        String country = "India";
        when(ppiSurveyLocator.getPPIUploadFileForCountry(country)).thenReturn(ppiXmlPath);
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        when(questionGroupDefinitionParser.parse(ppiXmlPath)).thenReturn(questionGroupDto);
        questionnaireService.uploadPPIQuestionGroup(country);
        verify(ppiSurveyLocator).getPPIUploadFileForCountry(country);
        verify(questionGroupDefinitionParser).parse(ppiXmlPath);
        verify(questionnaireValidator).validateForDefineQuestionGroup(questionGroupDto, false);
        verify(questionGroupDao).create(any(QuestionGroup.class));
    }

    @Test
    public void shouldSaveQuestionGroupInstance() {
        QuestionGroupInstanceDtoBuilder instanceBuilder = new QuestionGroupInstanceDtoBuilder();
        QuestionGroupResponseDtoBuilder responseBuilder = new QuestionGroupResponseDtoBuilder();
        responseBuilder.withResponse("Answer1").withSectionQuestion(999);
        QuestionGroupResponseDto questionGroupResponseDto = responseBuilder.build();
        instanceBuilder.withQuestionGroup(123).withCompleted(true).withCreator(111).withEventSource(1).withEntity(12345).withVersion(1).addResponses(questionGroupResponseDto);
        QuestionGroupInstanceDto questionGroupInstanceDto = instanceBuilder.build();
        when(questionGroupInstanceDao.create(Matchers.<QuestionGroupInstance>any())).thenReturn(789);
        Integer qgInstanceId = questionnaireService.saveQuestionGroupInstance(questionGroupInstanceDto);
        assertThat(qgInstanceId, is(789));
        verify(questionGroupInstanceDao).create(any(QuestionGroupInstance.class));
    }

    @Test
    public void shouldSaveQuestionDto() {
        QuestionDtoBuilder questionDtoBuilder = new QuestionDtoBuilder();
        QuestionDto questionDto = questionDtoBuilder.withText("Ques1").withType(QuestionType.SMART_SELECT).addChoices(new ChoiceDto("Ch1"), new ChoiceDto("Ch2")).build();
        when(questionDao.create(any(QuestionEntity.class))).thenReturn(1234);
        Integer questionId = questionnaireService.defineQuestion(questionDto);
        assertThat(questionId, is(1234));
        verify(questionnaireValidator).validateForDefineQuestion(questionDto);
        verify(questionDao).create(any(QuestionEntity.class));
    }


    private EventSourceEntity getEventSourceEntity(int id) {
        EventSourceEntity eventSource = new EventSourceEntity();
        eventSource.setId(id);
        return eventSource;
    }

    private void assertQuestionGroupInstanceDetail(QuestionGroupInstanceDetail instanceDetail, String questionGroupTitle, int year, int month, int day, int id) {
        assertThat(instanceDetail.getId(), is(id));
        assertThat(instanceDetail.getQuestionGroupTitle(), is(questionGroupTitle));
        Date date = instanceDetail.getDateCompleted();
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

    private QuestionGroup getQuestionGroup(int id, String title, List<Section> sections) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setId(id);
        questionGroup.setTitle(title);
        questionGroup.setSections(sections);
        return questionGroup;
    }

    private QuestionGroup getQuestionGroup(int questionGroupId, String title, String event, String source, QuestionGroupState state, boolean editable, List<Section> sections) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setId(questionGroupId);
        questionGroup.setTitle(title);
        questionGroup.setSections(sections);
        questionGroup.setState(state);
        questionGroup.setEditable(editable);
        questionGroup.setEventSources(getEventSources(event, source));
        return questionGroup;
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

    private SectionDetail getSectionDetailWithQuestions(String name, List<QuestionDetail> questionDetails, String value, boolean mandatory) {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName(name);
        List<SectionQuestionDetail> sectionQuestionDetails = new ArrayList<SectionQuestionDetail>();
        for (QuestionDetail questionDetail : questionDetails) {
            SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail(questionDetail, mandatory);
            sectionQuestionDetail.setValue(value);
            sectionQuestionDetails.add(sectionQuestionDetail);
        }
        sectionDetail.setQuestionDetails(sectionQuestionDetails);
        return sectionDetail;
    }

    /*
     * Matches if a question entity with the same text and nickname is in the questino group passed in.
     */
    private class QuestionGroupContainsQuestionNicknameMatcher extends TypeSafeMatcher<QuestionGroup> {
        private final QuestionEntity questionEntity;

        public QuestionGroupContainsQuestionNicknameMatcher(QuestionEntity questionEntity) {
            this.questionEntity = questionEntity;
        }

        @Override
        public boolean matchesSafely(QuestionGroup questionGroup) {
            for (Section section : questionGroup.getSections()) {
                for (SectionQuestion sectionQuestion : section.getQuestions()) {
                    QuestionEntity questionEntity = sectionQuestion.getQuestion();
                    if (StringUtils.equals(this.questionEntity.getQuestionText(), questionEntity.getQuestionText()) &&
                    StringUtils.equals(this.questionEntity.getNickname(), questionEntity.getNickname())) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("QuestionGroup doesn't contain the given Question");
        }
    }

    private class QuestionGroupContainsQuestionMatcher extends TypeSafeMatcher<QuestionGroup> {
        private final QuestionEntity questionEntity;

        public QuestionGroupContainsQuestionMatcher(QuestionEntity questionEntity) {
            this.questionEntity = questionEntity;
        }

        @Override
        public boolean matchesSafely(QuestionGroup questionGroup) {
            for (Section section : questionGroup.getSections()) {
                for (SectionQuestion sectionQuestion : section.getQuestions()) {
                    QuestionEntity questionEntity = sectionQuestion.getQuestion();
                    if (StringUtils.equals(this.questionEntity.getQuestionText(), questionEntity.getQuestionText())) {
                        return this.questionEntity.getAnswerTypeAsEnum() == questionEntity.getAnswerTypeAsEnum()
                                && areCompatibleChoices(questionEntity.getChoices());
                    }
                }
            }
            return false;
        }

        private boolean areCompatibleChoices(List<QuestionChoiceEntity> choiceEntities) {
            boolean result = choiceEntities.size() == this.questionEntity.getChoices().size();
            if (result) {
                for (QuestionChoiceEntity questionChoiceEntity : this.questionEntity.getChoices()) {
                    boolean currentChoiceFound = false;
                    for (QuestionChoiceEntity choiceEntity : choiceEntities) {
                        if (StringUtils.equals(questionChoiceEntity.getChoiceText(), choiceEntity.getChoiceText())) {
                            currentChoiceFound = true;
                        }
                    }
                    if (!currentChoiceFound) {
                        result = false;
                        break;
                    }
                }
            }
            return result;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("QuestionGroup doesn't contain the given Question");
        }
    }
}
