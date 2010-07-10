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
package org.mifos.platform.questionnaire.contract;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.QuestionnaireServiceFacadeImpl;
import org.mifos.platform.questionnaire.matchers.QuestionGroupDetailMatcher;
import org.mifos.ui.core.controller.Question;
import org.mifos.ui.core.controller.QuestionGroup;
import org.mifos.ui.core.controller.SectionForm;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
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
    public void shouldCreateQuestionGroup() throws ApplicationException {
        QuestionGroup questionGroup = getQuestionGroup(TITLE, "Create", "Client", asList(getSectionFormWithQuestions("S1", 123), getSectionFormWithQuestions("S2", 123)));
        questionnaireServiceFacade.createQuestionGroup(questionGroup);
        verify(questionnaireService, times(1)).defineQuestionGroup(argThat(
                new QuestionGroupDefinitionMatcher(TITLE, "Create", "Client", asList(getSectionDefinitionWithQuestions("S1", 123), getSectionDefinitionWithQuestions("S2", 123)))));
    }

    @Test
    public void testShouldCreateQuestions() throws ApplicationException {
        String title = TITLE + System.currentTimeMillis();
        String title1 = title + 1;
        String title2 = title + 2;
        questionnaireServiceFacade.createQuestions(asList(getQuestion(title1, "Free text"), getQuestion(title2, "Date")));
        verify(questionnaireService, times(2)).defineQuestion(argThat(new QuestionDefinitionMatcher(QuestionType.FREETEXT, QuestionType.DATE)));
    }

    @Test
    public void testShouldCheckDuplicates() {
        questionnaireServiceFacade.isDuplicateQuestion(TITLE);
        verify(questionnaireService).isDuplicateQuestion(any(QuestionDefinition.class));
    }

    @Test
    public void testGetAllQuestion() {
        when(questionnaireService.getAllQuestions()).thenReturn(asList(new QuestionDetail(1, "title", "title", QuestionType.NUMERIC)));
        List<Question> questionDetailList = questionnaireServiceFacade.getAllQuestions();
        assertNotNull(questionDetailList);
        assertThat(questionDetailList.get(0).getTitle(), is("title"));
        assertThat(questionDetailList.get(0).getId(), is("1"));
        verify(questionnaireService).getAllQuestions();
    }

    @Test
    public void testGetAllQuestionGroups() {
        when(questionnaireService.getAllQuestionGroups()).thenReturn(
                asList(new QuestionGroupDetail(1, "title1", asList(getSectionDefinition("S1"), getSectionDefinition("S2"))),
                        new QuestionGroupDetail(2, "title2", asList(getSectionDefinition("S3")))));
        List<QuestionGroup> questionGroup = questionnaireServiceFacade.getAllQuestionGroups();
        assertNotNull(questionGroup);

        QuestionGroup questionGroup1 = questionGroup.get(0);
        assertThat(questionGroup1.getId(), is("1"));
        assertThat(questionGroup1.getTitle(), is("title1"));
        List<SectionForm> sectionsOfQuestionGroup1 = questionGroup1.getSections();
        assertThat(sectionsOfQuestionGroup1.size(), is(2));
        assertThat(sectionsOfQuestionGroup1.get(0).getName(), is("S1"));
        assertThat(sectionsOfQuestionGroup1.get(1).getName(), is("S2"));

        QuestionGroup groupGroup2 = questionGroup.get(1);
        assertThat(groupGroup2.getId(), is("2"));
        assertThat(groupGroup2.getTitle(), is("title2"));
        List<SectionForm> sectionsOfQuestionGroup2 = groupGroup2.getSections();
        assertThat(sectionsOfQuestionGroup2.size(), is(1));
        assertThat(sectionsOfQuestionGroup2.get(0).getName(), is("S3"));

        verify(questionnaireService).getAllQuestionGroups();
    }

    @Test
    public void testGetQuestionGroupById() throws ApplicationException {
        int questionGroupId = 1;
        List<SectionDefinition> sections = asList(getSectionDefinitionWithQuestions("S1", 121), getSectionDefinitionWithQuestions("S2", 122, 123));
        QuestionGroupDetail expectedQuestionGroupDetail = getQuestionGroupDetail(TITLE, "Create", "Client", sections);
        when(questionnaireService.getQuestionGroup(questionGroupId)).thenReturn(expectedQuestionGroupDetail);
        QuestionGroupDetail questionGroupDetail = questionnaireServiceFacade.getQuestionGroupDetail(questionGroupId);
        assertThat(questionGroupDetail, new QuestionGroupDetailMatcher(expectedQuestionGroupDetail));
    }

    @Test
    public void testGetQuestionGroupByIdFailure() throws ApplicationException {
        int questionGroupId = 1;
        when(questionnaireService.getQuestionGroup(questionGroupId)).thenThrow(new ApplicationException(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        try {
            questionnaireServiceFacade.getQuestionGroupDetail(questionGroupId);
        } catch (ApplicationException e) {
            verify(questionnaireService, times(1)).getQuestionGroup(questionGroupId);
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        }
    }

    @Test
    public void testGetQuestionById() throws ApplicationException {
        int questionId = 1;
        String title = "Title";
        when(questionnaireService.getQuestion(questionId)).thenReturn(new QuestionDetail(questionId, title, title, QuestionType.NUMERIC));
        QuestionDetail questionDetail = questionnaireServiceFacade.getQuestionDetail(questionId);
        assertNotNull("Question group should not be null", questionDetail);
        assertThat(questionDetail.getShortName(), is(title));
        assertThat(questionDetail.getType(), is(QuestionType.NUMERIC));
        verify(questionnaireService).getQuestion(questionId);
    }

    @Test
    public void testGetQuestionByIdFailure() throws ApplicationException {
        int questionId = 1;
        when(questionnaireService.getQuestion(questionId)).thenThrow(new ApplicationException(QuestionnaireConstants.QUESTION_NOT_FOUND));
        try {
            questionnaireServiceFacade.getQuestionDetail(questionId);
        } catch (ApplicationException e) {
            verify(questionnaireService, times(1)).getQuestion(questionId);
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_NOT_FOUND));
        }
    }

    @Test
    public void testRetrieveEventSources() {
        EventSource event1 = makeEvent("Create", "Client", "Create Client");
        EventSource event2 = makeEvent("View", "Client", "View Client");
        List<EventSource> events = getEvents(event1, event2);
        when(questionnaireService.getAllEventSources()).thenReturn(events);
        List<EventSource> eventSources = questionnaireServiceFacade.getAllEventSources();
        assertNotNull(eventSources);
        assertTrue(eventSources.size() == 2);
        assertThat(eventSources, new EventSourcesMatcher(asList(event1, event2)));
        verify(questionnaireService).getAllEventSources();
    }

    private SectionForm getSectionForm(String name) {
        SectionForm sectionForm = new SectionForm();
        sectionForm.setName(name);
        return sectionForm;
    }

    private SectionForm getSectionFormWithQuestions(String name, int... questionIds) {
        SectionForm sectionForm = new SectionForm();
        sectionForm.setName(name);
        List<Question> questions = new ArrayList<Question>();
        for (int quesId : questionIds) {
            Question question = new Question();
            question.setTitle("Q" + quesId);
            question.setId(String.valueOf(quesId));
            question.setRequired(false);
            questions.add(question);
        }
        sectionForm.setQuestions(questions);
        return sectionForm;
    }

    private QuestionGroupDetail getQuestionGroupDetail(String title, String event, String source, List<SectionDefinition> sections) {
        return new QuestionGroupDetail(1, title, new EventSource(event, source, null), sections);
    }

    private List<SectionDefinition> getSections(String... sectionNames) {
        List<SectionDefinition> sectionDefinitions = new ArrayList<SectionDefinition>();
        for (String sectionName : sectionNames) {
            sectionDefinitions.add(getSectionDefinition(sectionName));
        }
        return sectionDefinitions;
    }

    private SectionDefinition getSectionDefinition(String name) {
        SectionDefinition sectionDefinition = new SectionDefinition();
        sectionDefinition.setName(name);
        sectionDefinition.addQuestion(new SectionQuestionDetail(123, "Q1", true));
        return sectionDefinition;
    }

    private SectionDefinition getSectionDefinitionWithQuestions(String name, int... questionIds) {
        SectionDefinition sectionDefinition = new SectionDefinition();
        sectionDefinition.setName(name);
        for (int quesId : questionIds) {
            sectionDefinition.addQuestion(new SectionQuestionDetail(quesId, "Q" + quesId, false));
        }
        return sectionDefinition;
    }

    private List<EventSource> getEvents(EventSource... event) {
        return Arrays.asList(event);
    }

    private EventSource makeEvent(String event, String source, String description) {
        return new EventSource(event, source, description);
    }

    private Question getQuestion(String title, String type) {
        Question question = new Question();
        question.setTitle(title);
        question.setType(type);
        return question;
    }

    private QuestionGroup getQuestionGroup(String title, String event, String source, List<SectionForm> sections) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setTitle(title);
        questionGroup.setSections(sections);
        questionGroup.setEventSourceId(String.format("%s.%s", event, source));
        return questionGroup;
    }

    private class QuestionDefinitionMatcher extends ArgumentMatcher<QuestionDefinition> {
        private List<QuestionType> questionType;

        public QuestionDefinitionMatcher(QuestionType... questionType) {
            this.questionType = new ArrayList<QuestionType>();
            this.questionType.addAll(Arrays.asList(questionType));
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof QuestionDefinition) {
                QuestionDefinition questionDefinition = (QuestionDefinition) argument;
                if (questionType.get(0) == questionDefinition.getType()) {
                    questionType.remove(questionDefinition.getType());
                    questionType.add(questionDefinition.getType());
                    return true;
                }
            }
            return false;
        }
    }

    private class QuestionGroupDefinitionMatcher extends TypeSafeMatcher<QuestionGroupDefinition> {

        private QuestionGroupDefinition questionGroupDefinition;

        public QuestionGroupDefinitionMatcher(QuestionGroupDefinition questionGroupDefinition) {
            this.questionGroupDefinition = questionGroupDefinition;
        }

        public QuestionGroupDefinitionMatcher(String title, String event, String source, List<SectionDefinition> sectionDefinitions) {
            this(new QuestionGroupDefinition(title, new EventSource(event, source, event + "." + source), sectionDefinitions));
        }

        @Override
        public boolean matchesSafely(QuestionGroupDefinition questionGroupDefinition) {
            if (StringUtils.equalsIgnoreCase(this.questionGroupDefinition.getTitle(), questionGroupDefinition.getTitle())) {
                assertThat(this.questionGroupDefinition.getEventSource(), new EventSourceMatcher(questionGroupDefinition.getEventSource()));
                assertThat(this.questionGroupDefinition.getSectionDefinitions(), new SectionDefinitionListMatcher(questionGroupDefinition.getSectionDefinitions()));
            } else {
                return false;
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Question group definitions do not match");
        }
    }

    private class EventSourceMatcher extends TypeSafeMatcher<EventSource> {

        private EventSource eventSource;

        public EventSourceMatcher(EventSource eventSource) {
            this.eventSource = eventSource;
        }

        @Override
        public boolean matchesSafely(EventSource eventSource) {
            return StringUtils.endsWithIgnoreCase(this.eventSource.getDesciption(), eventSource.getDesciption()) &&
                    StringUtils.endsWithIgnoreCase(this.eventSource.getSource(), eventSource.getSource()) &&
                    StringUtils.endsWithIgnoreCase(this.eventSource.getEvent(), eventSource.getEvent());
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Event sources do not match");
        }
    }

    private class SectionDefinitionListMatcher extends TypeSafeMatcher<List<SectionDefinition>> {
        private List<SectionDefinition> sectionDefinitions;

        public SectionDefinitionListMatcher(List<SectionDefinition> sectionDefinitions) {
            this.sectionDefinitions = sectionDefinitions;
        }


        @Override
        public boolean matchesSafely(List<SectionDefinition> sectionDefinitions) {
            if (this.sectionDefinitions.size() == sectionDefinitions.size()) {
                for (SectionDefinition sectionDefinition : this.sectionDefinitions) {
                    assertThat(sectionDefinitions, hasItem(new SectionDefinitionMatcher(sectionDefinition)));
                }
            } else {
                return false;
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Section definition lists do not match");
        }
    }

    private class SectionDefinitionMatcher extends TypeSafeMatcher<SectionDefinition> {
        private SectionDefinition sectionDefinition;

        public SectionDefinitionMatcher(SectionDefinition sectionDefinition) {
            this.sectionDefinition = sectionDefinition;
        }

        @Override
        public boolean matchesSafely(SectionDefinition sectionDefinition) {
            if (StringUtils.equalsIgnoreCase(this.sectionDefinition.getName(), sectionDefinition.getName())
                    && this.sectionDefinition.getQuestions().size() == sectionDefinition.getQuestions().size()) {
                for (SectionQuestionDetail sectionQuestionDetail : this.sectionDefinition.getQuestions()) {
                    assertThat(sectionDefinition.getQuestions(), hasItem(new SectionQuestionDetailMatcher(sectionQuestionDetail)));
                }
                return true;
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Section definitions do not match");
        }

    }

    private class SectionQuestionDetailMatcher extends TypeSafeMatcher<SectionQuestionDetail> {
        private SectionQuestionDetail sectionQuestionDetail;

        public SectionQuestionDetailMatcher(SectionQuestionDetail sectionQuestionDetail) {
            this.sectionQuestionDetail = sectionQuestionDetail;
        }

        @Override
        public boolean matchesSafely(SectionQuestionDetail sectionQuestionDetail) {
            return this.sectionQuestionDetail.getQuestionId() == sectionQuestionDetail.getQuestionId()
                    & StringUtils.equalsIgnoreCase(this.sectionQuestionDetail.getTitle(), sectionQuestionDetail.getTitle());
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Section definition questions do not match");
        }
    }

    private class EventSourcesMatcher extends TypeSafeMatcher<List<EventSource>> {
        private List<EventSource> eventSources;

        public EventSourcesMatcher(List<EventSource> eventSources) {
            this.eventSources = eventSources;
        }

        @Override
        public boolean matchesSafely(List<EventSource> eventSources) {
            if (eventSources.size() == this.eventSources.size()) {
                for (EventSource eventSource : this.eventSources) {
                    assertThat(eventSources, hasItem(new EventSourceMatcher(eventSource)));
                }
                return true;
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("EventSources did not match");
        }
    }
}
