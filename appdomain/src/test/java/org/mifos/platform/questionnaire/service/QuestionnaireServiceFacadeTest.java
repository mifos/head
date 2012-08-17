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
package org.mifos.platform.questionnaire.service;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.admin.servicefacade.RolesPermissionServiceFacade;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.AuditLogService;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.builders.QuestionDtoBuilder;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.domain.QuestionGroupState;
import org.mifos.platform.questionnaire.domain.QuestionnaireService;
import org.mifos.platform.questionnaire.exceptions.MandatoryAnswerNotFoundException;
import org.mifos.platform.questionnaire.matchers.EventSourceMatcher;
import org.mifos.platform.questionnaire.matchers.EventSourcesMatcher;
import org.mifos.platform.questionnaire.matchers.QuestionDetailMatcher;
import org.mifos.platform.questionnaire.matchers.QuestionGroupDetailMatcher;
import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.mifos.platform.validations.ValidationException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireServiceFacadeTest {

    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @Mock
    private QuestionnaireService questionnaireService;

    @Mock
    private AuditLogService auditLogService;
    
    @Mock
    private RolesPermissionServiceFacade rolesPermissionServiceFacade;
    
    private static final String TITLE = "Title";

    @Before
    public void setUp() throws Exception {
        questionnaireServiceFacade = new QuestionnaireServiceFacadeImpl(questionnaireService, rolesPermissionServiceFacade);
    }

    @Test
    public void doNothing() {

    }
    
    @Mock
    private QuestionGroup questionGroup;
    
    @Test
    public void shouldCreateQuestionGroup() throws Exception {
        QuestionGroupDetail questionGroupDetail = getQuestionGroupDetail(TITLE, "Create", "Client", asList(getSectionDetailWithQuestionIds("S1", 123), getSectionDetailWithQuestionIds("S2", 123)));
        questionGroupDetail.setActivityId((short) 0);
        
        when(questionnaireService.getQuestionGroupById(1)).thenReturn(questionGroup);
        when(questionGroup.getActivityId()).thenReturn((short)-1);
        when(rolesPermissionServiceFacade.createActivityForQuestionGroup((short)240, "" + "")).thenReturn(-1);
        when(questionnaireService.defineQuestionGroup(questionGroupDetail)).thenReturn(questionGroupDetail);
        questionnaireServiceFacade.createQuestionGroup(questionGroupDetail);
       
        Mockito.verify(questionnaireService, times(1)).defineQuestionGroup(argThat(
                new QuestionGroupDetailMatcher(questionGroupDetail)));
    }
    
    @Test
    public void testShouldCreateQuestions() throws SystemException {
        String title = TITLE + System.currentTimeMillis();
        String title1 = title + 1;
        String title2 = title + 2;
        questionnaireServiceFacade.createQuestions(asList(getQuestionDetail(0, title1, QuestionType.FREETEXT),
                getQuestionDetail(0, title2, QuestionType.DATE),
                getQuestionDetail(0, title2, QuestionType.MULTI_SELECT, asList("choice1", "choice2"))));
        Mockito.verify(questionnaireService, times(1)).defineQuestion(argThat(new QuestionDetailMatcher(getQuestionDetail(0, title1, QuestionType.FREETEXT))));
        Mockito.verify(questionnaireService, times(1)).defineQuestion(argThat(new QuestionDetailMatcher(getQuestionDetail(0, title2, QuestionType.DATE))));
        Mockito.verify(questionnaireService, times(1)).defineQuestion(argThat(new QuestionDetailMatcher(getQuestionDetail(0, title2, QuestionType.MULTI_SELECT, asList("choice1", "choice2")))));
    }

    @Test
    public void testShouldCheckDuplicates() {
        questionnaireServiceFacade.isDuplicateQuestion(TITLE);
        Mockito.verify(questionnaireService).isDuplicateQuestionText(Mockito.any(String.class));
    }

    @Test
    public void testGetAllActiveQuestion() {
        when(questionnaireService.getAllActiveQuestions(null)).thenReturn(asList(getQuestionDetail(1, "title", QuestionType.NUMERIC)));
        List<QuestionDetail> questionDetailList = questionnaireServiceFacade.getAllActiveQuestions();
        Assert.assertNotNull(questionDetailList);
        assertThat(questionDetailList.get(0).getText(), is("title"));
        assertThat(questionDetailList.get(0).getId(), is(1));
        Mockito.verify(questionnaireService).getAllActiveQuestions(null);
    }

    @Test
    public void testGetAllActiveQuestionWithoutExcludedQuestions() {
        List<Integer> excludedQuestions = asList(2);
        when(questionnaireService.getAllActiveQuestions(excludedQuestions)).thenReturn(asList(getQuestionDetail(1, "title", QuestionType.NUMERIC)));
        List<QuestionDetail> questionDetailList = questionnaireServiceFacade.getAllActiveQuestions(excludedQuestions);
        Assert.assertNotNull(questionDetailList);
        assertThat(questionDetailList.get(0).getText(), is("title"));
        assertThat(questionDetailList.get(0).getId(), is(1));
        Mockito.verify(questionnaireService).getAllActiveQuestions(excludedQuestions);
    }

    @Test
    public void testGetAllQuestion() {
        when(questionnaireService.getAllQuestions()).thenReturn(asList(getQuestionDetail(1, "title", QuestionType.NUMERIC)));
        List<QuestionDetail> questionDetailList = questionnaireServiceFacade.getAllQuestions();
        Assert.assertNotNull(questionDetailList);
        assertThat(questionDetailList.get(0).getText(), is("title"));
        assertThat(questionDetailList.get(0).getId(), is(1));
        Mockito.verify(questionnaireService).getAllQuestions();
    }

    @Test
    public void testGetAllQuestionGroups() {
        when(questionnaireService.getAllQuestionGroups()).thenReturn(
                asList(new QuestionGroupDetail(1, "title1", asList(getSectionDetail("S1"), getSectionDetail("S2"))),
                        new QuestionGroupDetail(2, "title2", asList(getSectionDetail("S3")))));
        List<QuestionGroupDetail> questionGroupDetails = questionnaireServiceFacade.getAllQuestionGroups();
        Assert.assertNotNull(questionGroupDetails);

        QuestionGroupDetail questionGroupDetail1 = questionGroupDetails.get(0);
        assertThat(questionGroupDetail1.getId(), is(1));
        assertThat(questionGroupDetail1.getTitle(), is("title1"));

        QuestionGroupDetail questionGroupDetail2 = questionGroupDetails.get(1);
        assertThat(questionGroupDetail2.getId(), is(2));
        assertThat(questionGroupDetail2.getTitle(), is("title2"));

        Mockito.verify(questionnaireService).getAllQuestionGroups();
    }

    @Test
    public void testGetQuestionGroupById() throws Exception {
        int questionGroupId = 1;
        List<SectionDetail> sections = asList(getSectionDetailWithQuestionIds("S1", 121), getSectionDetailWithQuestionIds("S2", 122, 123));
        QuestionGroupDetail expectedQuestionGroupDetail = getQuestionGroupDetail(TITLE, "Create", "Client", sections);
        when(questionnaireService.getQuestionGroupById(questionGroupId)).thenReturn(questionGroup);
        when(questionGroup.getActivityId()).thenReturn((short)-1);
        when(rolesPermissionServiceFacade.hasUserAccessForActivity(questionGroup.getActivityId())).thenReturn(true);
        when(questionnaireService.getQuestionGroup(questionGroupId)).thenReturn(expectedQuestionGroupDetail);
        QuestionGroupDetail questionGroupDetail = questionnaireServiceFacade.getQuestionGroupDetail(questionGroupId);
        assertThat(questionGroupDetail, new QuestionGroupDetailMatcher(expectedQuestionGroupDetail));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testGetQuestionGroupByIdFailure() throws Exception {
        int questionGroupId = 1;
        when(questionnaireService.getQuestionGroup(questionGroupId)).thenThrow(new SystemException(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        when(questionnaireService.getQuestionGroupById(questionGroupId)).thenReturn(questionGroup);
        when(questionGroup.getActivityId()).thenReturn((short)-1);
        when(rolesPermissionServiceFacade.hasUserAccessForActivity(questionGroup.getActivityId())).thenReturn(true);
        try {
            questionnaireServiceFacade.getQuestionGroupDetail(questionGroupId);
        } catch (SystemException e) {
            Mockito.verify(questionnaireService, times(1)).getQuestionGroup(questionGroupId);
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        }
    }

    @Test
    public void testGetQuestionById() throws SystemException {
        int questionId = 1;
        String title = "Title";
        QuestionDetail question = new QuestionDetail(questionId, title, QuestionType.NUMERIC, true, true);
        question.setNumericMin(10);
        question.setNumericMax(100);
        when(questionnaireService.getQuestion(questionId)).thenReturn(question);
        QuestionDetail questionDetail = questionnaireServiceFacade.getQuestionDetail(questionId);
        Assert.assertNotNull("Question group should not be null", questionDetail);
        assertThat(questionDetail.getText(), is(title));
        assertThat(questionDetail.getType(), is(QuestionType.NUMERIC));
        assertThat(questionDetail.getNumericMin(), is(10));
        assertThat(questionDetail.getNumericMax(), is(100));
        Mockito.verify(questionnaireService).getQuestion(questionId);
    }

    @Test
    public void testGetQuestionWithAnswerChoicesById() throws SystemException {
        int questionId = 1;
        String title = "Title";
        List<ChoiceDto> answerChoices = asList(new ChoiceDto("choice1"), new ChoiceDto("choice2"));
        QuestionDetail expectedQuestionDetail = new QuestionDetail(questionId, title, QuestionType.MULTI_SELECT, true, true);
        expectedQuestionDetail.setAnswerChoices(answerChoices);
        when(questionnaireService.getQuestion(questionId)).thenReturn(expectedQuestionDetail);
        QuestionDetail questionDetail = questionnaireServiceFacade.getQuestionDetail(questionId);
        Assert.assertNotNull("Question group should not be null", questionDetail);
        assertThat(questionDetail, new QuestionDetailMatcher(expectedQuestionDetail));
        Mockito.verify(questionnaireService).getQuestion(questionId);
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testGetQuestionByIdFailure() throws SystemException {
        int questionId = 1;
        when(questionnaireService.getQuestion(questionId)).thenThrow(new SystemException(QuestionnaireConstants.QUESTION_NOT_FOUND));
        try {
            questionnaireServiceFacade.getQuestionDetail(questionId);
        } catch (SystemException e) {
            Mockito.verify(questionnaireService, times(1)).getQuestion(questionId);
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_NOT_FOUND));
        }
    }

    @Test
    public void testRetrieveEventSources() {
        EventSourceDto event1 = makeEvent("Create", "Client", "Create Client");
        EventSourceDto event2 = makeEvent("View", "Client", "View Client");
        List<EventSourceDto> events = getEvents(event1, event2);
        when(questionnaireService.getAllEventSources()).thenReturn(events);
        List<EventSourceDto> eventSourceDtos = questionnaireServiceFacade.getAllEventSources();
        Assert.assertNotNull(eventSourceDtos);
        Assert.assertTrue(eventSourceDtos.size() == 2);
        assertThat(eventSourceDtos, new EventSourcesMatcher(asList(event1, event2)));
        Mockito.verify(questionnaireService).getAllEventSources();
    }

    @Test
    public void shouldRetrieveQuestionGroupsByEventSource() throws SystemException {
        QuestionGroupDetail questionGroupDetail = getQuestionGroupDetail(TITLE + 1, "Create", "Client", asList(getSectionDetailWithQuestionIds("Section1", 11, 22, 33)));
        when(questionnaireService.getQuestionGroups(any(EventSourceDto.class))).thenReturn(asList(questionGroupDetail));
        List<QuestionGroupDetail> questionGroupDetails = questionnaireServiceFacade.getQuestionGroups("Create", "Client");
        assertQuestionGroupDetails(questionGroupDetails);
        Mockito.verify(questionnaireService, times(1)).getQuestionGroups(argThat(new EventSourceMatcher("Create", "Client", "Create.Client")));
    }

    private void assertQuestionGroupDetails(List<QuestionGroupDetail> questionGroupDetails) {
        assertThat(questionGroupDetails, is(notNullValue()));
        assertThat(questionGroupDetails.size(), is(1));
        QuestionGroupDetail questionGroupDetail1 = questionGroupDetails.get(0);
        assertThat(questionGroupDetail1.getId(), is(1));
        assertThat(questionGroupDetail1.getTitle(), is(TITLE + 1));
        List<SectionDetail> sections = questionGroupDetail1.getSectionDetails();
        assertThat(sections, is(notNullValue()));
        assertThat(sections.size(), is(1));
        SectionDetail section1 = sections.get(0);
        assertThat(section1.getName(), is("Section1"));
        List<SectionQuestionDetail> questions1 = section1.getQuestions();
        assertThat(questions1, is(notNullValue()));
        assertThat(questions1.size(), is(3));
        SectionQuestionDetail question1 = questions1.get(0);
        assertThat(question1.getQuestionId(), is(11));
        assertThat(question1.getText(), is("Q11"));
        assertThat(question1.isMandatory(), is(false));
        assertThat(question1.getQuestionType(), is(QuestionType.DATE));
    }

    @Test
    public void shouldSaveQuestionGroupDetail() {
        List<QuestionDetail> questionDetails = asList(new QuestionDetail(12, "Question 1", QuestionType.FREETEXT, true, true));
        List<SectionDetail> sectionDetails = asList(getSectionDetailWithQuestions("Sec1", questionDetails, "value", false));
        QuestionGroupDetails questionGroupDetails = new QuestionGroupDetails(1, 1, 1,
                asList(getQuestionGroupDetail("QG1", "Create", "Client", sectionDetails)));
        questionnaireServiceFacade.saveResponses(questionGroupDetails);
        verify(questionnaireService, times(1)).saveResponses(questionGroupDetails);
    }

    @Test
    public void testValidateResponse() {
        List<QuestionDetail> questionDetails = asList(new QuestionDetail(12, "Question 1", QuestionType.FREETEXT, true, true));
        List<SectionDetail> sectionDetails = asList(getSectionDetailWithQuestions("Sec1", questionDetails, null, true));
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(1, "QG1", Arrays.asList(new EventSourceDto("Create", "Client", null)), sectionDetails, true);
        try {
            Mockito.doThrow(new MandatoryAnswerNotFoundException("Title")).
                    when(questionnaireService).validateResponses(asList(questionGroupDetail));
            questionnaireServiceFacade.validateResponses(asList(questionGroupDetail));
            Assert.fail("Should not have thrown the validation exception");
        } catch (ValidationException e) {
            verify(questionnaireService, times(1)).validateResponses(asList(questionGroupDetail));
        }
    }

    @Test
    public void testGetQuestionGroupInstances() {
        when(questionnaireService.getQuestionGroupInstances(101, new EventSourceDto("View", "Client", "View.Client"), false, false)).thenReturn(new ArrayList<QuestionGroupInstanceDetail>());
        assertThat(questionnaireServiceFacade.getQuestionGroupInstances(101, "View", "Client"), is(notNullValue()));
        verify(questionnaireService).getQuestionGroupInstances(eq(101), any(EventSourceDto.class), eq(false), eq(false));
    }

    @Test
    public void testGetQuestionGroupInstance() {
        int questionGroupInstanceId = 1212;
        when(questionnaireService.getQuestionGroupInstance(questionGroupInstanceId)).thenReturn(getQuestionGroupInstanceDetail());
        assertThat(questionnaireServiceFacade.getQuestionGroupInstance(questionGroupInstanceId), is(notNullValue()));
        verify(questionnaireService, times(1)).getQuestionGroupInstance(questionGroupInstanceId);
    }

    @Test
    public void testGetQuestionGroupInstancesIncludingNoResponses() {
        when(questionnaireService.getQuestionGroupInstances(101, new EventSourceDto("Create", "Client", "Create.Client"), true, true)).thenReturn(new ArrayList<QuestionGroupInstanceDetail>());
        questionnaireServiceFacade.getQuestionGroupInstancesWithUnansweredQuestionGroups(101, "Create", "Client");
        verify(questionnaireService).getQuestionGroupInstances(eq(101), any(EventSourceDto.class), eq(true), eq(true));
    }

    @Test
    public void testGetQuestionGroupInstancesIncludingNoResponsesFilterInactiveQuestions() {
        QuestionGroupInstanceDetail detail = getQuestionGroupInstanceDetail();
        SectionDetail sectionDetail = getSectionDetailWithQuestionIds("Misc", 1, 2, 3);
        sectionDetail.getQuestionDetail(1).getQuestionDetail().setActive(false);
        detail.getQuestionGroupDetail().setSectionDetails(asList(sectionDetail));
        when(questionnaireService.getQuestionGroupInstances(eq(101), any(EventSourceDto.class), eq(true), eq(true))).thenReturn(asList(detail));
        List<QuestionGroupInstanceDetail> details = questionnaireServiceFacade.getQuestionGroupInstancesWithUnansweredQuestionGroups(101, "Create", "Client");
        assertThat(details, is(not(nullValue())));
        List<Integer> questionIds = details.get(0).getQuestionGroupDetail().getAllQuestionIds();
        assertThat(questionIds.size(), is(2));
        assertThat(questionIds.get(0), is(1));
        assertThat(questionIds.get(1), is(3));
        verify(questionnaireService).getQuestionGroupInstances(eq(101), any(EventSourceDto.class), eq(true), eq(true));
    }

    @Test
    public void testCreateQuestionGroupUsingDTO() {
        QuestionGroupDto questionGroupDto = new QuestionGroupDto();
        questionnaireServiceFacade.createQuestionGroup(questionGroupDto);
        verify(questionnaireService).defineQuestionGroup(questionGroupDto);
    }

    @Test
    public void testGetAllCountriesForPPI() {
        List<String> countries = asList("India", "China", "Canada");
        when(questionnaireService.getAllCountriesForPPI()).thenReturn(countries);
        assertThat(questionnaireServiceFacade.getAllCountriesForPPI(), is(countries));
        verify(questionnaireService).getAllCountriesForPPI();
    }

    @Test
    public void testUploadPPIQuestionGroup() {
        questionnaireServiceFacade.uploadPPIQuestionGroup("India");
        verify(questionnaireService).uploadPPIQuestionGroup("India");
    }

    @Test
    public void testSaveQuestionGroupInstance() {
        QuestionGroupInstanceDto questionGroupInstanceDto = new QuestionGroupInstanceDto();
        when(questionnaireService.saveQuestionGroupInstance(questionGroupInstanceDto)).thenReturn(1234);
        Integer qgInstanceId = questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto);
        assertThat(qgInstanceId, is(1234));
        verify(questionnaireService).saveQuestionGroupInstance(questionGroupInstanceDto);
    }

    @Test
    public void testSaveQuestionDto() {
        QuestionDtoBuilder questionDtoBuilder = new QuestionDtoBuilder();
        QuestionDto questionDto = questionDtoBuilder.withText("Ques1").withType(QuestionType.FREETEXT).build();
        when(questionnaireService.defineQuestion(questionDto)).thenReturn(1234);
        Integer questionId = questionnaireServiceFacade.createQuestion(questionDto);
        assertThat(questionId, is(1234));
        verify(questionnaireService).defineQuestion(questionDto);
    }

    @Test
    public void testSaveResponsesTwoQuestionGroups() {

        questionnaireServiceFacade = new QuestionnaireServiceFacadeImpl(questionnaireService, auditLogService, null);

        // setup test data
        Integer creatorId = new Integer(1);
        Integer entityId = new Integer(2);
        Integer eventSourceId = new Integer(3);
        EventSourceDto eventSourceDto = new EventSourceDto("Create", "Savings", "Test");
        
        // new responses. these will be saved when saveResponses() is called
        List<SectionDetail> sectionDetails = new ArrayList<SectionDetail>();
        List<QuestionGroupDetail> questionGroups = new ArrayList<QuestionGroupDetail>();
        questionGroups.add(new QuestionGroupDetail(1, "Title 1", sectionDetails));
        questionGroups.add(new QuestionGroupDetail(2, "Title 2", sectionDetails));
        QuestionGroupDetails newQuestionGroupDetails = new QuestionGroupDetails(creatorId, entityId, eventSourceId, questionGroups);

        // persisted responses. when saveResponse() is called, it will store responses in the database. this is why
        // question persistedQuestionGroupInstanceDetailsX uses responses from above.
        QuestionGroupInstanceDetail persistedQuestionGroupInstanceDetail1 = new QuestionGroupInstanceDetail();
        persistedQuestionGroupInstanceDetail1.setId(111);
        persistedQuestionGroupInstanceDetail1.setQuestionGroupDetail(questionGroups.get(0));
        
        QuestionGroupInstanceDetail persistedQuestionGroupInstanceDetail2 = new QuestionGroupInstanceDetail();
        persistedQuestionGroupInstanceDetail2.setId(222);
        persistedQuestionGroupInstanceDetail2.setQuestionGroupDetail(questionGroups.get(1));
        
        List<QuestionGroupInstanceDetail> persistedQuestionGroupInstanceDetails = new ArrayList<QuestionGroupInstanceDetail>();
        persistedQuestionGroupInstanceDetails.add(persistedQuestionGroupInstanceDetail1);
        persistedQuestionGroupInstanceDetails.add(persistedQuestionGroupInstanceDetail2);
        
        when(questionnaireService.getEventSource(eventSourceId)).thenReturn(eventSourceDto);
        when(questionnaireService.getQuestionGroupInstances(entityId, eventSourceDto, false, false)).thenReturn(persistedQuestionGroupInstanceDetails);
        when(questionnaireService.getQuestionGroupInstance(111)).thenReturn(persistedQuestionGroupInstanceDetail1);
        when(questionnaireService.getQuestionGroupInstance(222)).thenReturn(persistedQuestionGroupInstanceDetail2);
        
        // test
        questionnaireServiceFacade.saveResponses(newQuestionGroupDetails);
        
        // verify expectations. the key thing to note is second parameter is expected to be NULL
        verify(auditLogService).addAuditLogRegistry(questionGroups.get(0), null, creatorId, entityId, eventSourceDto.getSource(), eventSourceDto.getEvent());
        verify(auditLogService).addAuditLogRegistry(questionGroups.get(1), null, creatorId, entityId, eventSourceDto.getSource(), eventSourceDto.getEvent());
    }
    
    private QuestionGroupInstanceDetail getQuestionGroupInstanceDetail() {
        QuestionGroupInstanceDetail groupInstanceDetail = new QuestionGroupInstanceDetail();
        groupInstanceDetail.setQuestionGroupDetail(new QuestionGroupDetail());
        return groupInstanceDetail;
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

    private QuestionGroupDetail getQuestionGroupDetail(String title, String event, String source, List<SectionDetail> sections) {
        return new QuestionGroupDetail(1, title, Arrays.asList(new EventSourceDto(event, source, null)), sections, false);
    }

    private SectionDetail getSectionDetailWithQuestionIds(String name, int... questionIds) {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName(name);
        List<SectionQuestionDetail> questions = new ArrayList<SectionQuestionDetail>();
        for (int quesId : questionIds) {
            String text = "Q" + quesId;
            questions.add(new SectionQuestionDetail(new QuestionDetail(quesId, text, QuestionType.DATE, true, true), false));
        }
        sectionDetail.setQuestionDetails(questions);
        return sectionDetail;
    }

    private SectionDetail getSectionDetail(String name) {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName(name);
        sectionDetail.addQuestion(new SectionQuestionDetail(new QuestionDetail(123, "Q1", QuestionType.FREETEXT, true, true), true));
        return sectionDetail;
    }

    private List<EventSourceDto> getEvents(EventSourceDto... event) {
        return asList(event);
    }

    private EventSourceDto makeEvent(String event, String source, String description) {
        return new EventSourceDto(event, source, description);
    }

    private QuestionDetail getQuestionDetail(int id, String text, QuestionType questionType) {
        return new QuestionDetail(id, text, questionType, true, true);
    }


    private QuestionDetail getQuestionDetail(int id, String text, QuestionType questionType, List<String> choices) {
        QuestionDetail questionDetail = new QuestionDetail(id, text, questionType, true, true);
        List<ChoiceDto> choiceDtos = new ArrayList<ChoiceDto>();
        for (String choice : choices) {
            choiceDtos.add(new ChoiceDto(choice));
        }
        questionDetail.setAnswerChoices(choiceDtos);
        return questionDetail;
    }
}