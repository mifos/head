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
package org.mifos.platform.questionnaire.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.domain.QuestionnaireService;
import org.mifos.platform.questionnaire.matchers.EventSourceMatcher;
import org.mifos.platform.questionnaire.matchers.EventSourcesMatcher;
import org.mifos.platform.questionnaire.matchers.QuestionDetailMatcher;
import org.mifos.platform.questionnaire.matchers.QuestionGroupDetailMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;

import static org.hamcrest.CoreMatchers.is; //NOPMD
import static org.hamcrest.CoreMatchers.notNullValue;   //NOPMD
import static org.mockito.Matchers.argThat;    //NOPMD
import org.mockito.Mockito;  


@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD")
public class QuestionnaireServiceFacadeTest {

    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @Mock
    private QuestionnaireService questionnaireService;

    private static final String TITLE = "Title";

    @Before
    public void setUp() throws Exception {
        questionnaireServiceFacade = new QuestionnaireServiceFacadeImpl(questionnaireService);
    }

    @Test
    public void shouldCreateQuestionGroup() throws SystemException {
        QuestionGroupDetail questionGroupDetail = getQuestionGroupDetail(TITLE, "Create", "Client", Arrays.asList(getSectionDetailWithQuestions("S1", 123), getSectionDetailWithQuestions("S2", 123)));
        questionnaireServiceFacade.createQuestionGroup(questionGroupDetail);
        Mockito.verify(questionnaireService, Mockito.times(1)).defineQuestionGroup(argThat(
                new QuestionGroupDetailMatcher(questionGroupDetail)));
    }

    @Test
    public void testShouldCreateQuestions() throws SystemException {
        String title = TITLE + System.currentTimeMillis();
        String title1 = title + 1;
        String title2 = title + 2;
        questionnaireServiceFacade.createQuestions(Arrays.asList(getQuestionDetail(0, title1, title1, QuestionType.FREETEXT),
                getQuestionDetail(0, title2, title2, QuestionType.DATE),
                getQuestionDetail(0, title2, title2, QuestionType.MULTI_SELECT, Arrays.asList("choice1","choice2"))));
        Mockito.verify(questionnaireService, Mockito.times(1)).defineQuestion(argThat(new QuestionDetailMatcher(getQuestionDetail(0, title1, title1, QuestionType.FREETEXT))));
        Mockito.verify(questionnaireService, Mockito.times(1)).defineQuestion(argThat(new QuestionDetailMatcher(getQuestionDetail(0, title2, title2, QuestionType.DATE))));
        Mockito.verify(questionnaireService, Mockito.times(1)).defineQuestion(argThat(new QuestionDetailMatcher(getQuestionDetail(0, title2, title2, QuestionType.MULTI_SELECT, Arrays.asList("choice1","choice2")))));
    }

    @Test
    public void testShouldCheckDuplicates() {
        questionnaireServiceFacade.isDuplicateQuestion(TITLE);
        Mockito.verify(questionnaireService).isDuplicateQuestionTitle(Mockito.any(String.class));
    }

    @Test
    public void testGetAllQuestion() {
        Mockito.when(questionnaireService.getAllQuestions()).thenReturn(Arrays.asList(getQuestionDetail(1, "title", "title", QuestionType.NUMERIC)));
        List<QuestionDetail> questionDetailList = questionnaireServiceFacade.getAllQuestions();
        Assert.assertNotNull(questionDetailList);
        Assert.assertThat(questionDetailList.get(0).getTitle(), is("title"));
        Assert.assertThat(questionDetailList.get(0).getId(), is(1));
        Mockito.verify(questionnaireService).getAllQuestions();
    }

    @Test
    public void testGetAllQuestionGroups() {
        Mockito.when(questionnaireService.getAllQuestionGroups()).thenReturn(
                Arrays.asList(new QuestionGroupDetail(1, "title1", Arrays.asList(getSectionDefinition("S1"), getSectionDefinition("S2"))),
                        new QuestionGroupDetail(2, "title2", Arrays.asList(getSectionDefinition("S3")))));
        List<QuestionGroupDetail> questionGroupDetails = questionnaireServiceFacade.getAllQuestionGroups();
        Assert.assertNotNull(questionGroupDetails);

        QuestionGroupDetail questionGroupDetail1 = questionGroupDetails.get(0);
        Assert.assertThat(questionGroupDetail1.getId(), is(1));
        Assert.assertThat(questionGroupDetail1.getTitle(), is("title1"));

        QuestionGroupDetail questionGroupDetail2 = questionGroupDetails.get(1);
        Assert.assertThat(questionGroupDetail2.getId(), is(2));
        Assert.assertThat(questionGroupDetail2.getTitle(), is("title2"));

        Mockito.verify(questionnaireService).getAllQuestionGroups();
    }

    @Test
    public void testGetQuestionGroupById() throws SystemException {
        int questionGroupId = 1;
        List<SectionDetail> sections = Arrays.asList(getSectionDefinitionWithQuestions("S1", 121), getSectionDefinitionWithQuestions("S2", 122, 123));
        QuestionGroupDetail expectedQuestionGroupDetail = getQuestionGroupDetail(TITLE, "Create", "Client", sections);
        Mockito.when(questionnaireService.getQuestionGroup(questionGroupId)).thenReturn(expectedQuestionGroupDetail);
        QuestionGroupDetail questionGroupDetail = questionnaireServiceFacade.getQuestionGroupDetail(questionGroupId);
        Assert.assertThat(questionGroupDetail, new QuestionGroupDetailMatcher(expectedQuestionGroupDetail));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testGetQuestionGroupByIdFailure() throws SystemException {
        int questionGroupId = 1;
        Mockito.when(questionnaireService.getQuestionGroup(questionGroupId)).thenThrow(new SystemException(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        try {
            questionnaireServiceFacade.getQuestionGroupDetail(questionGroupId);
        } catch (SystemException e) {
            Mockito.verify(questionnaireService, Mockito.times(1)).getQuestionGroup(questionGroupId);
            Assert.assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        }
    }

    @Test
    public void testGetQuestionById() throws SystemException {
        int questionId = 1;
        String title = "Title";
        Mockito.when(questionnaireService.getQuestion(questionId)).thenReturn(new QuestionDetail(questionId, title, title, QuestionType.NUMERIC));
        QuestionDetail questionDetail = questionnaireServiceFacade.getQuestionDetail(questionId);
        Assert.assertNotNull("Question group should not be null", questionDetail);
        Assert.assertThat(questionDetail.getShortName(), is(title));
        Assert.assertThat(questionDetail.getType(), is(QuestionType.NUMERIC));
        Mockito.verify(questionnaireService).getQuestion(questionId);
    }

    @Test
    public void testGetQuestionWithAnswerChoicesById() throws SystemException {
        int questionId = 1;
        String title = "Title";
        Mockito.when(questionnaireService.getQuestion(questionId)).thenReturn(new QuestionDetail(questionId, title, title, QuestionType.MULTI_SELECT, Arrays.asList("choice1","choice2")));
        QuestionDetail questionDetail = questionnaireServiceFacade.getQuestionDetail(questionId);
        Assert.assertNotNull("Question group should not be null", questionDetail);
        Assert.assertThat(questionDetail, new QuestionDetailMatcher(new QuestionDetail(questionId, title, title, QuestionType.MULTI_SELECT, Arrays.asList("choice1","choice2"))));
        Mockito.verify(questionnaireService).getQuestion(questionId);
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testGetQuestionByIdFailure() throws SystemException {
        int questionId = 1;
        Mockito.when(questionnaireService.getQuestion(questionId)).thenThrow(new SystemException(QuestionnaireConstants.QUESTION_NOT_FOUND));
        try {
            questionnaireServiceFacade.getQuestionDetail(questionId);
        } catch (SystemException e) {
            Mockito.verify(questionnaireService, Mockito.times(1)).getQuestion(questionId);
            Assert.assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_NOT_FOUND));
        }
    }

    @Test
    public void testRetrieveEventSources() {
        EventSource event1 = makeEvent("Create", "Client", "Create Client");
        EventSource event2 = makeEvent("View", "Client", "View Client");
        List<EventSource> events = getEvents(event1, event2);
        Mockito.when(questionnaireService.getAllEventSources()).thenReturn(events);
        List<EventSource> eventSources = questionnaireServiceFacade.getAllEventSources();
        Assert.assertNotNull(eventSources);
        Assert.assertTrue(eventSources.size() == 2);
        Assert.assertThat(eventSources, new EventSourcesMatcher(Arrays.asList(event1, event2)));
        Mockito.verify(questionnaireService).getAllEventSources();
    }

    @Test
    public void shouldRetrieveQuestionGroupsByEventSource() throws SystemException {
        QuestionGroupDetail questionGroupDetail = getQuestionGroupDetail(TITLE + 1, "Create", "Client", Arrays.asList(getSectionDetailWithQuestions("Section1", 11, 22, 33)));
        Mockito.when(questionnaireService.getQuestionGroups(Mockito.any(EventSource.class))).thenReturn(Arrays.asList(questionGroupDetail));
        List<QuestionGroupDetail> questionGroupDetails = questionnaireServiceFacade.getQuestionGroups("Create", "Client");
        Assert.assertThat(questionGroupDetails, is(notNullValue()));
        Assert.assertThat(questionGroupDetails.size(), is(1));
        QuestionGroupDetail questionGroupDetail1 = questionGroupDetails.get(0);
        Assert.assertThat(questionGroupDetail1.getId(), is(1));
        Assert.assertThat(questionGroupDetail1.getTitle(), is(TITLE + 1));
        List<SectionDetail> sections = questionGroupDetail1.getSectionDetails();
        Assert.assertThat(sections, is(notNullValue()));
        Assert.assertThat(sections.size(), is(1));
        SectionDetail section1 = sections.get(0);
        Assert.assertThat(section1.getName(), is("Section1"));
        List<SectionQuestionDetail> questions1 = section1.getQuestions();
        Assert.assertThat(questions1, is(notNullValue()));
        Assert.assertThat(questions1.size(), is(3));
        SectionQuestionDetail question1 = questions1.get(0);
        Assert.assertThat(question1.getQuestionId(), is(11));
        Assert.assertThat(question1.getTitle(), is("Q11"));
        Assert.assertThat(question1.isMandatory(), is(false));
        Assert.assertThat(question1.getQuestionType(), is(QuestionType.DATE));
        Mockito.verify(questionnaireService, Mockito.times(1)).getQuestionGroups(argThat(new EventSourceMatcher("Create", "Client", "Create.Client")));
    }

    private SectionDetail getSectionDetailWithQuestions(String name, int... questionIds) {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName(name);
        List<SectionQuestionDetail> questions = new ArrayList<SectionQuestionDetail>();
        for (int quesId : questionIds) {
            String text = "Q" + quesId;
            questions.add(new SectionQuestionDetail(new QuestionDetail(quesId, text, text, QuestionType.DATE), false));
        }
        sectionDetail.setQuestionDetails(questions);
        return sectionDetail;
    }

    private QuestionGroupDetail getQuestionGroupDetail(String title, String event, String source, List<SectionDetail> sections) {
        return new QuestionGroupDetail(1, title, new EventSource(event, source, null), sections);
    }

    private SectionDetail getSectionDefinition(String name) {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName(name);
        sectionDetail.addQuestion(new SectionQuestionDetail(new QuestionDetail(123, "Q1", "Q1", QuestionType.FREETEXT), true));
        return sectionDetail;
    }

    private SectionDetail getSectionDefinitionWithQuestions(String name, int... questionIds) {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName(name);
        for (int quesId : questionIds) {
            String text = "Q" + quesId;
            sectionDetail.addQuestion(new SectionQuestionDetail(new QuestionDetail(quesId, text, text, QuestionType.FREETEXT), false));
        }
        return sectionDetail;
    }

    private List<EventSource> getEvents(EventSource... event) {
        return Arrays.asList(event);
    }

    private EventSource makeEvent(String event, String source, String description) {
        return new EventSource(event, source, description);
    }

    private QuestionDetail getQuestionDetail(int id, String text, String shortName, QuestionType questionType) {
        return new QuestionDetail(id, text, shortName, questionType);
    }


    private QuestionDetail getQuestionDetail(int id, String text, String shortName, QuestionType questionType, List<String> choices) {
        return new QuestionDetail(id, text, shortName, questionType, choices);
    }
}

