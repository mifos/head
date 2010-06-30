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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.QuestionnaireServiceFacadeImpl;
import org.mifos.test.matchers.HasThisKindOfEvent;
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
        QuestionGroup questionGroup = getQuestionGroup(TITLE, "Create", "Client", asList(getSectionForm("S1"), getSectionForm("S2")));
        questionnaireServiceFacade.createQuestionGroup(questionGroup);
        verify(questionnaireService, times(1)).defineQuestionGroup(argThat(
                new QuestionGroupDefinitionMatcher(TITLE, "Create", "Client", asList(getSectionForm("S1"), getSectionForm("S2")))));
    }

    private SectionForm getSectionForm(String name) {
        SectionForm sectionForm = new SectionForm();
        sectionForm.setName(name);
        return sectionForm;
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
    public void testShouldCheckDuplicates(){
        questionnaireServiceFacade.isDuplicateQuestion(TITLE);
        verify(questionnaireService).isDuplicateQuestion(any(QuestionDefinition.class));
    }

    @Test
    public void testGetAllQuestion(){
        when(questionnaireService.getAllQuestions()).thenReturn(asList(new QuestionDetail(1, "title", "title", QuestionType.NUMERIC)));
        List<Question> questionDetailList = questionnaireServiceFacade.getAllQuestions();
        assertNotNull(questionDetailList);
        assertThat(questionDetailList.get(0).getTitle(), is("title"));
        assertThat(questionDetailList.get(0).getId(), is("1"));
        verify(questionnaireService).getAllQuestions();
    }

    @Test
    public void testGetAllQuestionGroups() {
        when(questionnaireService.getAllQuestionGroups()).thenReturn(asList(new QuestionGroupDetail(1,"title1"), new QuestionGroupDetail(2,"title2")));
        List<QuestionGroup> questionGroup = questionnaireServiceFacade.getAllQuestionGroups();
        assertNotNull(questionGroup);
        assertThat(questionGroup.get(0).getTitle(), is("title1"));
        assertThat(questionGroup.get(0).getId(), is("1"));
        assertThat(questionGroup.get(1).getTitle(), is("title2"));
        assertThat(questionGroup.get(1).getId(), is("2"));
        verify(questionnaireService).getAllQuestionGroups();
    }

    @Test
    public void testGetQuestionGroupById() throws ApplicationException {
        int questionGroupId = 1;
        String title = "Title";
        when(questionnaireService.getQuestionGroup(questionGroupId)).thenReturn(new QuestionGroupDetail(title));
        QuestionGroup questionGroup = questionnaireServiceFacade.getQuestionGroup(questionGroupId);
        assertNotNull("Question group should not be null", questionGroup);
        assertThat(questionGroup.getTitle(), is(title));
    }
    
    @Test
    public void testGetQuestionGroupByIdFailure() throws ApplicationException {
        int questionGroupId = 1;
        when(questionnaireService.getQuestionGroup(questionGroupId)).thenThrow(new ApplicationException(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        try {
             questionnaireServiceFacade.getQuestionGroup(questionGroupId);
        } catch (ApplicationException e) {
            verify(questionnaireService,times(1)).getQuestionGroup(questionGroupId);
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        }
    }

    @Test
    public void testGetQuestionById() throws ApplicationException {
        int questionId = 1;
        String title = "Title";
        when(questionnaireService.getQuestion(questionId)).thenReturn(new QuestionDetail(questionId, title, title, QuestionType.NUMERIC));
        Question question = questionnaireServiceFacade.getQuestion(questionId);
        assertNotNull("Question group should not be null",question);
        assertThat(question.getTitle(), is(title));
        assertThat(question.getType(), is("Number"));
        verify(questionnaireService).getQuestion(questionId);
    }

    @Test
    public void testGetQuestionByIdFailure() throws ApplicationException {
        int questionId = 1;
        when(questionnaireService.getQuestion(questionId)).thenThrow(new ApplicationException(QuestionnaireConstants.QUESTION_NOT_FOUND));
        try {
             questionnaireServiceFacade.getQuestion(questionId);
        } catch (ApplicationException e) {
            verify(questionnaireService,times(1)).getQuestion(questionId);
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_NOT_FOUND));
        }
    }

    @Test
    public void testRetrieveEventSources() {
        List<EventSource> events = getEvents(makeEvent("Create", "Client", "Create Client"), makeEvent("View", "Client", "View Client"));
        when(questionnaireService.getAllEventSources()).thenReturn(events);
        List<EventSource> eventSources = questionnaireServiceFacade.getAllEventSources();
        assertNotNull(eventSources);
        assertTrue(eventSources.size() == 2);
        assertThat(eventSources, new HasThisKindOfEvent("Create", "Client", "Create Client"));
        assertThat(eventSources, new HasThisKindOfEvent("View", "Client", "View Client"));
        verify(questionnaireService).getAllEventSources();
    }

    private List<EventSource> getEvents(EventSource... event) {
        return Arrays.asList(event);
    }

    private EventSource makeEvent(String event, String source, String description) {
        return new EventSource(source, event, description);
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
        questionGroup.setEventSource(new EventSource(source, event, null));
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

    private class QuestionGroupDefinitionMatcher extends ArgumentMatcher<QuestionGroupDefinition> {
        private String title;
        private List<SectionForm> sectionForms;
        private String event;
        private String source;

        public QuestionGroupDefinitionMatcher(String title, String event, String source, List<SectionForm> sectionForms) {
            this.title = title;
            this.event = event;
            this.source = source;
            this.sectionForms = sectionForms;
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof QuestionGroupDefinition) {
                QuestionGroupDefinition questionGroupDefinition = (QuestionGroupDefinition) argument;
                List<SectionDefinition> sectionDefinitions = questionGroupDefinition.getSectionDefinitions();
                for (int i=0; i<sectionDefinitions.size();i++){
                    sectionForms.get(i).getName().equals(sectionDefinitions.get(i).getName());
                }
                return StringUtils.equals(questionGroupDefinition.getTitle(), title) &&
                        StringUtils.equals(questionGroupDefinition.getEventSource().getEvent(), event) &&
                        StringUtils.equals(questionGroupDefinition.getEventSource().getSource(), source);
            }
            return false;
        }
    }

}
