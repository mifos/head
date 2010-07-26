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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.business.EntityMaster;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.exceptions.ValidationException;
import org.mifos.platform.questionnaire.mappers.QuestionnaireMapper;
import org.mifos.platform.questionnaire.mappers.QuestionnaireMapperImpl;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupInstanceDao;
import org.mifos.platform.questionnaire.persistence.SectionQuestionDao;
import org.mifos.platform.questionnaire.service.EventSource;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.validators.QuestionnaireValidator;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.MANDATORY_QUESTION_HAS_NO_ANSWER;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private static final String QUESTION_TITLE = "Test QuestionDetail Title";
    private static final String QUESTION_GROUP_TITLE = "Question Group Title";
    public static final String EVENT_CREATE = "Create";
    public static final String SOURCE_CLIENT = "Client";

    @Before
    public void setUp() {
        QuestionnaireMapper questionnaireMapper = new QuestionnaireMapperImpl(eventSourceDao, questionDao, questionGroupDao, sectionQuestionDao);
        questionnaireService = new QuestionnaireServiceImpl(questionnaireValidator, questionDao, questionnaireMapper, questionGroupDao, eventSourceDao, questionGroupInstanceDao);
    }

    @Test
    public void shouldDefineQuestion() throws SystemException {
        QuestionDetail questionDefinition = new QuestionDetail(QUESTION_TITLE, QuestionType.FREETEXT);
        try {
            QuestionDetail questionDetail = questionnaireService.defineQuestion(questionDefinition);
            Mockito.verify(questionDao, times(1)).create(any(QuestionEntity.class));
            Assert.assertNotNull(questionDetail);
            Assert.assertEquals(QUESTION_TITLE, questionDetail.getText());
            Assert.assertEquals(QUESTION_TITLE, questionDetail.getShortName());
            Assert.assertEquals(QuestionType.FREETEXT, questionDetail.getType());
            Assert.assertEquals(questionDetail.getAnswerChoices(), Arrays.asList());
        } catch (SystemException e) {
            Assert.fail("Should not have thrown the validation exception");
        }
        Mockito.verify(questionnaireValidator).validateForDefineQuestion(questionDefinition);
        Mockito.verify(questionDao).create(any(QuestionEntity.class));
    }

    @Test
    public void shouldDefineQuestionWithAnswerChoices() throws SystemException {
        String choice1 = "choice1";
        String choice2 = "choice2";
        QuestionDetail questionDefinition = new QuestionDetail(QUESTION_TITLE, QuestionType.MULTI_SELECT, Arrays.asList(choice1, choice2));
        try {
            QuestionDetail questionDetail = questionnaireService.defineQuestion(questionDefinition);
            Mockito.verify(questionDao, times(1)).create(any(QuestionEntity.class));
            Assert.assertNotNull(questionDetail);
            Assert.assertEquals(QUESTION_TITLE, questionDetail.getText());
            Assert.assertEquals(QUESTION_TITLE, questionDetail.getShortName());
            Assert.assertEquals(QuestionType.MULTI_SELECT, questionDetail.getType());
            Assert.assertEquals(questionDetail.getAnswerChoices(), Arrays.asList(choice1, choice2));
        } catch (SystemException e) {
            Assert.fail("Should not have thrown the validation exception");
        }
        Mockito.verify(questionnaireValidator).validateForDefineQuestion(questionDefinition);
        Mockito.verify(questionDao).create(any(QuestionEntity.class));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test(expected = SystemException.class)
    public void shouldThrowValidationExceptionWhenQuestionTitleIsNull() throws SystemException {
        QuestionDetail questionDefinition = new QuestionDetail(null, QuestionType.INVALID);
        doThrow(new SystemException(QuestionnaireConstants.QUESTION_TITLE_NOT_PROVIDED)).when(questionnaireValidator).validateForDefineQuestion(questionDefinition);
        questionnaireService.defineQuestion(questionDefinition);
        verify(questionnaireValidator).validateForDefineQuestion(questionDefinition);
    }

    @Test
    public void shouldGetAllQuestions() {
        Mockito.when(questionDao.retrieveByState(1)).thenReturn(Arrays.asList(getQuestion(1, "q1", AnswerType.DATE), getQuestion(2, "q2", AnswerType.FREETEXT)));
        List<QuestionDetail> questionDetails = questionnaireService.getAllQuestions();
        Assert.assertNotNull("getAllQuestions should not return null", questionDetails);
        Mockito.verify(questionDao, times(1)).retrieveByState(1);

        assertThat(questionDetails.get(0).getText(), is("q1"));
        assertThat(questionDetails.get(0).getShortName(), is("q1"));
        assertThat(questionDetails.get(0).getId(), is(1));
        assertThat(questionDetails.get(0).getType(), is(QuestionType.DATE));

        assertThat(questionDetails.get(1).getText(), is("q2"));
        assertThat(questionDetails.get(1).getShortName(), is("q2"));
        assertThat(questionDetails.get(1).getId(), is(2));
        assertThat(questionDetails.get(1).getType(), is(QuestionType.FREETEXT));

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
        Mockito.when(questionDao.getDetails(anyInt())).thenReturn(getQuestion(11), getQuestion(12), getQuestion(11), getQuestion(12));
        try {
            QuestionGroupDetail questionGroupDetail = questionnaireService.defineQuestionGroup(questionGroupDefinition);
            assertQuestionGroupDetail(questionGroupDetail);
            Mockito.verify(questionnaireValidator).validateForDefineQuestionGroup(questionGroupDefinition);
            Mockito.verify(questionGroupDao, times(1)).create(any(QuestionGroup.class));
            Mockito.verify(eventSourceDao, times(1)).retrieveByEventAndSource(anyString(), anyString());
            Mockito.verify(questionDao, times(4)).getDetails(anyInt());
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
        assertThat(eventSource, notNullValue());
        assertThat(eventSource.getEvent(), is(EVENT_CREATE));
        assertThat(eventSource.getSource(), is(SOURCE_CLIENT));
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
        Mockito.when(eventSourceDao.retrieveByEventAndSource(anyString(), anyString())).thenReturn(Collections.singletonList(eventSourceEntity));
    }

    private QuestionGroupDetail getQuestionGroupDetail(String event, String source, String... sectionNames) {
        return new QuestionGroupDetail(0, QUESTION_GROUP_TITLE, getEventSource(event, source), getSectionDefinitions(sectionNames),false);
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
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(0, null, null, Arrays.asList(getSectionDefinition("S1")), false);
        doThrow(new SystemException(QuestionnaireConstants.QUESTION_GROUP_TITLE_NOT_PROVIDED)).when(questionnaireValidator).validateForDefineQuestionGroup(questionGroupDetail);
        questionnaireService.defineQuestionGroup(questionGroupDetail);
        verify(questionnaireValidator).validateForDefineQuestionGroup(questionGroupDetail);
    }

    @Test
    public void shouldGetAllQuestionGroups() {
        QuestionGroup questionGroup1 = getQuestionGroup(0, "QG0", getSections("S0_0"));
        QuestionGroup questionGroup2 = getQuestionGroup(1, "QG1", getSections("S1_0", "S1_1"));
        when(questionGroupDao.getDetailsAll()).thenReturn(Arrays.asList(questionGroup1, questionGroup2));
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
            sectionQuestion.setQuestion(new QuestionEntity());
            section.setQuestions(Arrays.asList(sectionQuestion));
            sectionList.add(section);
        }
        return sectionList;
    }

    private Section getSectionWithQuestions(int sectionQuestionId, String sectionName, String... questionNames) {
        Section section = new Section(sectionName);
        List<SectionQuestion> sectionQuestions = new ArrayList<SectionQuestion>();
        for (String questionName : questionNames) {
            SectionQuestion sectionQuestion = new SectionQuestion();
            sectionQuestion.setId(sectionQuestionId);
            sectionQuestion.setSection(section);
            QuestionEntity questionEntity = new QuestionEntity();
            questionEntity.setQuestionText(questionName);
            questionEntity.setShortName(questionName);
            sectionQuestion.setQuestion(questionEntity);
            sectionQuestions.add(sectionQuestion);
        }
        section.setQuestions(sectionQuestions);
        return section;
    }

    @Test
    public void shouldGetAllEventSources() {
        when(eventSourceDao.getDetailsAll()).thenReturn(Arrays.asList(getEventSourceEntity("Create", "Client")));
        List<EventSource> eventSources = questionnaireService.getAllEventSources();
        assertThat(eventSources, notNullValue());
        assertThat(eventSources.size(), is(1));
        assertThat(eventSources.get(0).getEvent(), is("Create"));
        assertThat(eventSources.get(0).getSource(), is("Client"));
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
        assertThat(questionDetail.getShortName(), is(title));
        assertThat(questionDetail.getText(), is(title));
        assertThat(questionDetail.getType(), is(QuestionType.DATE));
        Assert.assertEquals(questionDetail.getAnswerChoices(), Arrays.asList());
        verify(questionDao, times(1)).getDetails(questionId);
    }

    @Test
    public void testGetMultiSelectQuestionById() throws SystemException {
        int questionId = 1;
        String title = "Title";
        List<QuestionChoiceEntity> questionChoiceEntities = Arrays.asList(new QuestionChoiceEntity("choice1"), new QuestionChoiceEntity("choice2"));
        when(questionDao.getDetails(questionId)).thenReturn(getQuestion(questionId, title, AnswerType.MULTISELECT, questionChoiceEntities));
        QuestionDetail questionDetail = questionnaireService.getQuestion(questionId);
        Assert.assertNotNull(questionDetail);
        assertThat(questionDetail.getShortName(), is(title));
        assertThat(questionDetail.getText(), is(title));
        assertThat(questionDetail.getType(), is(QuestionType.MULTI_SELECT));
        Assert.assertEquals(questionDetail.getAnswerChoices(), Arrays.asList("choice1", "choice2"));
        verify(questionDao, times(1)).getDetails(questionId);
    }

    @Test
    public void testGetSingleSelectQuestionById() throws SystemException {
        int questionId = 1;
        String title = "Title";
        Mockito.when(questionDao.getDetails(questionId)).thenReturn(getQuestion(questionId, title, AnswerType.SINGLESELECT, Arrays.asList(new QuestionChoiceEntity("choice1"), new QuestionChoiceEntity("choice2"))));
        QuestionDetail questionDetail = questionnaireService.getQuestion(questionId);
        Assert.assertNotNull(questionDetail);
        Assert.assertThat(questionDetail.getShortName(), is(title));
        Assert.assertThat(questionDetail.getText(), is(title));
        Assert.assertThat(questionDetail.getType(), is(QuestionType.SINGLE_SELECT));
        Assert.assertEquals(questionDetail.getAnswerChoices(), Arrays.asList("choice1", "choice2"));
        Mockito.verify(questionDao, Mockito.times(1)).getDetails(questionId);
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
        when(questionDao.retrieveCountOfQuestionsWithTitle(QUESTION_TITLE)).thenReturn(Arrays.asList((long) 0)).thenReturn(Arrays.asList((long) 1));
        Assert.assertEquals(false, questionnaireService.isDuplicateQuestionTitle(questionDefinition.getTitle()));
        Assert.assertEquals(true, questionnaireService.isDuplicateQuestionTitle(questionDefinition.getTitle()));
        verify(questionDao, times(2)).retrieveCountOfQuestionsWithTitle(QUESTION_TITLE);
    }

    @Test
    public void shouldGetAllQuestionGroupsByEventSource() throws SystemException {
        List<QuestionGroup> questionGroups = Arrays.asList(getQuestionGroup(1, "Title1", getSections("Section1")), getQuestionGroup(2, "Title2", getSections("Section2")));
        when(questionGroupDao.retrieveQuestionGroupsByEventSource("Create", "Client")).thenReturn(questionGroups);
        List<QuestionGroupDetail> questionGroupDetails = questionnaireService.getQuestionGroups(null, new EventSource("Create", "Client", "Create.Client"));
        assertThat(questionGroupDetails, is(notNullValue()));
        assertThat(questionGroupDetails.size(), is(2));
        assertThat(questionGroupDetails.get(0).getTitle(), is("Title1"));
        assertThat(questionGroupDetails.get(1).getTitle(), is("Title2"));
        verify(questionnaireValidator, times(1)).validateForEventSource(any(EventSource.class));
        verify(questionGroupDao, times(1)).retrieveQuestionGroupsByEventSource("Create", "Client");
    }

    @Test
    public void shouldGetAllQuestionGroupsByEventSourceAndEntityId() {
        QuestionGroup questionGroup1 = getQuestionGroup(1, "Title1", Arrays.asList(getSectionWithQuestions(222, "Section1", "Question1")));
        QuestionGroup questionGroup2 = getQuestionGroup(2, "Title2", Arrays.asList(getSectionWithQuestions(222, "Section2", "Question2")));
        List<QuestionGroup> questionGroups = Arrays.asList(questionGroup1, questionGroup2);
        when(questionGroupDao.retrieveQuestionGroupsByEventSource("Create", "Client")).thenReturn(questionGroups);
        QuestionGroupInstance questionGroupInstance1 = getQuestionGroupInstance(101, 1, questionGroup1, "Hello World");
        QuestionGroupInstance questionGroupInstance2 = getQuestionGroupInstance(101, 2, questionGroup2, "Foo Bar");
        when(questionGroupInstanceDao.retrieveLatestQuestionGroupInstanceByQuestionGroupAndEntity(101, 1)).thenReturn(Arrays.asList(questionGroupInstance1));
        when(questionGroupInstanceDao.retrieveLatestQuestionGroupInstanceByQuestionGroupAndEntity(101, 2)).thenReturn(Arrays.asList(questionGroupInstance2));
        List<QuestionGroupDetail> questionGroupDetails = questionnaireService.getQuestionGroups(101, new EventSource("Create", "Client", "Create.Client"));
        assertThat(questionGroupDetails, is(notNullValue()));
        assertThat(questionGroupDetails.size(), is(2));
        assertThat(questionGroupDetails.get(0).getSectionDetail(0).getQuestionDetail(0).getValue(), is("Hello World"));
        assertThat(questionGroupDetails.get(1).getSectionDetail(0).getQuestionDetail(0).getValue(), is("Foo Bar"));
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
        for (int i=0; i<responses.length; i++) {
            groupResponses.add(getQuestionGroupResponse(responses[i], questionGroupInstance, questionGroup.getSections().get(i).getQuestions().get(0)));
        }
        questionGroupInstance.setQuestionGroupResponses(groupResponses);
        return questionGroupInstance;
    }

    private QuestionGroupInstance getQuestionGroupInstance(int entityId, int year, int month, int day, QuestionGroup questionGroup, String... responses) {
        QuestionGroupInstance questionGroupInstance = new QuestionGroupInstance();
        questionGroupInstance.setQuestionGroup(questionGroup);
        questionGroupInstance.setCompletedStatus(1);
        questionGroupInstance.setCreatorId(122);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        questionGroupInstance.setDateConducted(calendar.getTime());
        questionGroupInstance.setEntityId(entityId);
        questionGroupInstance.setVersionNum(1);
        List<QuestionGroupResponse> groupResponses = new ArrayList<QuestionGroupResponse>();
        for (int i=0; i<responses.length; i++) {
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
        List<QuestionDetail> questionDetails = Arrays.asList(new QuestionDetail(12, "Question 1", "Question 1", QuestionType.FREETEXT));
        List<SectionDetail> sectionDetails = Arrays.asList(getSectionDetailWithQuestions("Sec1", questionDetails, "value", false));
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(1, "QG1", new EventSource("Create", "Client", null), sectionDetails,true);
        questionnaireService.saveResponses(new QuestionGroupDetails(1, 1, Arrays.asList(questionGroupDetail)));
        verify(questionnaireValidator, times(1)).validateForQuestionGroupResponses(Arrays.asList(questionGroupDetail));
        verify(questionGroupInstanceDao, times(1)).saveOrUpdateAll(Matchers.<List<QuestionGroupInstance>>any()); // TODO: Verify the contents using a custom matcher
    }

    @Test
    public void testValidateResponse() {
        List<QuestionDetail> questionDetails = Arrays.asList(new QuestionDetail(12, "Question 1", "Question 1", QuestionType.FREETEXT));
        List<SectionDetail> sectionDetails = Arrays.asList(getSectionDetailWithQuestions("Sec1", questionDetails, null, true));
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(1, "QG1", new EventSource("Create", "Client", null), sectionDetails, true);
        try {
            Mockito.doThrow(new ValidationException(MANDATORY_QUESTION_HAS_NO_ANSWER, new SectionQuestionDetail())).
                    when(questionnaireValidator).validateForQuestionGroupResponses(Arrays.asList(questionGroupDetail));
            questionnaireService.validateResponses(Arrays.asList(questionGroupDetail));
            Assert.fail("Should not have thrown the validation exception");
        } catch (ValidationException e) {
            verify(questionnaireValidator, times(1)).validateForQuestionGroupResponses(Arrays.asList(questionGroupDetail));
        }
    }

    @Test
    public void shouldGetQuestionGroupInstances() {
        List<QuestionGroupInstance> questionGroupInstances = Arrays.asList(getQuestionGroupInstance(3031, 2010, 7, 24, getQuestionGroup(4041, "QG1", null)),
                                                                           getQuestionGroupInstance(3032, 2010, 7, 24, getQuestionGroup(4042, "QG2", null)),
                                                                           getQuestionGroupInstance(3033, 2010, 7, 24, getQuestionGroup(4043, "QG3", null)),
                                                                           getQuestionGroupInstance(3034, 2010, 7, 25, getQuestionGroup(4041, "QG1", null)),
                                                                           getQuestionGroupInstance(3035, 2010, 7, 25, getQuestionGroup(4042, "QG2", null)),
                                                                           getQuestionGroupInstance(3036, 2010, 7, 26, getQuestionGroup(4042, "QG2", null)),
                                                                           getQuestionGroupInstance(3037, 2010, 7, 26, getQuestionGroup(4043, "QG3", null)));
        when(questionGroupInstanceDao.retrieveQuestionGroupInstancesByEntityIdAndEventSourceId(101, 202)).thenReturn(questionGroupInstances);
        when(eventSourceDao.retrieveByEventAndSource("View", "Client")).thenReturn(Arrays.asList(getEventSourceEntity(202)));
        EventSource eventSource = new EventSource("View", "Client", "View.Client");
        List<QuestionGroupInstanceDetail> instances = questionnaireService.getQuestionGroupInstances(101, eventSource);
        assertThat(instances, is(notNullValue()));
        assertThat(instances.size(), is(7));
        assertQuestionGroupInstanceDetail(instances.get(0), "QG1", 2010, 7, 24);
        assertQuestionGroupInstanceDetail(instances.get(1), "QG2", 2010, 7, 24);
        assertQuestionGroupInstanceDetail(instances.get(2), "QG3", 2010, 7, 24);
        assertQuestionGroupInstanceDetail(instances.get(3), "QG1", 2010, 7, 25);
        assertQuestionGroupInstanceDetail(instances.get(4), "QG2", 2010, 7, 25);
        assertQuestionGroupInstanceDetail(instances.get(5), "QG2", 2010, 7, 26);
        assertQuestionGroupInstanceDetail(instances.get(6), "QG3", 2010, 7, 26);
        verify(questionGroupInstanceDao, times(1)).retrieveQuestionGroupInstancesByEntityIdAndEventSourceId(101, 202);
        verify(questionnaireValidator, times(1)).validateForEventSource(eventSource);
        verify(eventSourceDao, times(1)).retrieveByEventAndSource("View", "Client");
    }

    private EventSourceEntity getEventSourceEntity(int id) {
        EventSourceEntity eventSource = new EventSourceEntity();
        eventSource.setId(id);
        return eventSource;
    }

    private void assertQuestionGroupInstanceDetail(QuestionGroupInstanceDetail instanceDetail, String questionGroupTitle, int year, int month, int day) {
        assertThat(instanceDetail.getQuestionGroupTitle(), is(questionGroupTitle));
        Date date = instanceDetail.getDataCompleted();
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

    private QuestionGroup getQuestionGroup(int questionGroupId, String title, List<Section> sections) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setId(questionGroupId);
        questionGroup.setTitle(title);
        questionGroup.setSections(sections);
        return questionGroup;
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

}
