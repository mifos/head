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
package org.mifos.ui.core.controller;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.contract.QuestionnaireServiceFacade;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.binding.message.DefaultMessageResolver;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.ui.ModelMap;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireControllerTest {

    private QuestionnaireController questionnaireController;

    @Mock
    private RequestContext requestContext;

    @Mock
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    private static final String TITLE = "Title";
    @Mock
    private MessageContext messageContext;

    @Mock
    private ModelMap model;
    
    @Mock
    private HttpServletRequest httpServletRequest;

    @Before
    public void setUp() throws Exception {
        questionnaireController = new QuestionnaireController(questionnaireServiceFacade);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAddQuestionForSuccess() throws Exception {
        QuestionForm questionForm = getQuestionForm(TITLE, "Numeric");
        when(questionnaireServiceFacade.isDuplicateQuestion(TITLE)).thenReturn(false);
        String result = questionnaireController.addQuestion(questionForm, requestContext);
        List<Question> questions = questionForm.getQuestions();
        verify(questionnaireServiceFacade).isDuplicateQuestion(TITLE);
        assertThat(questions, is(notNullValue()));
        assertThat(questions.size(), is(1));
        assertThat(result, is("success"));
    }

    @Test
    public void testAddQuestionForFailureWhenQuestionTitleNotProvided() throws Exception {
        QuestionForm questionForm = new QuestionForm();
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionnaireController.addQuestion(questionForm, requestContext);
        assertThat(questionForm.getQuestions().size(), is(0));
        assertThat(result, is(notNullValue()));
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.emptytitle")));
    }

    @Test
    public void testAddQuestionForFailureWhenQuestionTitleProvidedWithAllBlanks() throws Exception {
        QuestionForm questionForm = new QuestionForm();
        questionForm.setTitle("      ");
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionnaireController.addQuestion(questionForm, requestContext);
        assertThat(questionForm.getQuestions().size(), is(0));
        assertThat(result, is(notNullValue()));
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.emptytitle")));
    }

    @Test
    public void testAddQuestionForFailureWhenQuestionTitleIsDuplicateInDB() throws Exception {
        QuestionForm questionForm = new QuestionForm();
        questionForm.setTitle(TITLE);
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        when(questionnaireServiceFacade.isDuplicateQuestion(TITLE)).thenReturn(true);
        String result = questionnaireController.addQuestion(questionForm, requestContext);
        assertThat(questionForm.getQuestions().size(), is(0));
        assertThat(result, is(notNullValue()));
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.duplicate.question.title")));
    }

    @Test
    public void testAddQuestionForFailureWhenQuestionTitleIsDuplicateInForm() throws Exception {
        QuestionForm questionForm = new QuestionForm();
        questionForm.setTitle("  " + TITLE + "    ");
        questionForm.setQuestions(asList(getQuestion(TITLE)));
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionnaireController.addQuestion(questionForm, requestContext);
        assertThat(questionForm.getQuestions().size(), is(1));
        assertThat(result, is(notNullValue()));
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.duplicate.question.title")));
    }

    @Test
    public void testCreateQuestionFailure() throws Exception {
        QuestionForm questionForm = getQuestionForm(TITLE, "Numeric");
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        doThrow(new ApplicationException("DB Write Failure")).when(questionnaireServiceFacade).createQuestions(Matchers.<List<Question>>anyObject());
        String result = questionnaireController.createQuestions(questionForm, requestContext);
        assertThat(result, is("failure"));
        verify(questionnaireServiceFacade).createQuestions(Matchers.<List<Question>>anyObject());
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.serivce.failure")));
    }

    @Test
    public void testCreateQuestionSuccess() throws Exception {
        QuestionForm questionForm = getQuestionForm(TITLE, "Date");
        String result = questionnaireController.createQuestions(questionForm, requestContext);
        assertThat(result, is("success"));
        verify(questionnaireServiceFacade).createQuestions(questionForm.getQuestions());
    }

    @Test
    public void testCreateQuestionGroupSuccess() throws Exception {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm("   " + TITLE + " ");
        String result = questionnaireController.defineQuestionGroup(questionGroupForm, requestContext);
        assertThat(result, is("success"));
        verify(questionnaireServiceFacade).createQuestionGroup(argThat(new QuestionGroupFormMatcher(TITLE)));
    }

    @Test
    public void testCreateQuestionGroupForFailureWhenQuestionGroupTitleNotProvided() throws Exception {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionnaireController.defineQuestionGroup(questionGroupForm, requestContext);
        assertThat(result, is(notNullValue()));
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.emptytitle")));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testCreateQuestionGroupFailure() throws Exception {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE);
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        doThrow(new ApplicationException("DB Write Failure")).when(questionnaireServiceFacade).createQuestionGroup(Matchers.<QuestionGroupForm>anyObject());
        String result = questionnaireController.defineQuestionGroup(questionGroupForm, requestContext);
        assertThat(result, is("failure"));
        verify(questionnaireServiceFacade).createQuestionGroup(Matchers.<QuestionGroupForm>anyObject());
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.serivce.failure")));
    }

    @Test
    public void shouldGetAllQuestions(){
        when(questionnaireServiceFacade.getAllQuestions()).thenReturn(asList(getQuestion("title1"), getQuestion("title2")));
        String view = questionnaireController.getAllQuestions(model, httpServletRequest);
        assertThat(view, is("viewQuestions"));
        verify(questionnaireServiceFacade).getAllQuestions();
        verify(model).addAttribute(eq("questions"),argThat(new ListOfTitlesMatcher("title1", "title2")));
    }

    @Test
    public void shouldGetAllQuestionGroups() {
        when(questionnaireServiceFacade.getAllQuestionGroups()).thenReturn(asList(getQuestionGroupForm("title1"), getQuestionGroupForm("title2")));
        String view = questionnaireController.getAllQuestionGroups(model, httpServletRequest);
        assertThat(view, is("viewQuestionGroups"));
        verify(questionnaireServiceFacade).getAllQuestionGroups();
        verify(model).addAttribute(eq("questionGroups"), argThat(new ListOfTitlesMatcher("title1", "title2")));
    }

    private QuestionForm getQuestionForm(String title, String type) {
        QuestionForm questionForm = new QuestionForm();
        questionForm.setTitle(title);
        questionForm.setType(type);
        return questionForm;
    }

    private QuestionGroupForm getQuestionGroupForm(String title) {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setTitle(title);
        return questionGroupForm;
    }

    private Question getQuestion(String title) {
        Question question = new Question();
        question.setTitle(title);
        return question;
    }

    private class MessageMatcher extends ArgumentMatcher<MessageResolver> {
        private String errorCode;

        public MessageMatcher(String errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public boolean matches(Object argument) {
            if (!(argument instanceof MessageResolver)) {
                return false;
            }
            DefaultMessageResolver messageResolver = (DefaultMessageResolver) argument;
            String[] codes = messageResolver.getCodes();
            return codes.length == 1 && StringUtils.equals(codes[0], errorCode);
        }
    }

    private class ListOfTitlesMatcher extends ArgumentMatcher<List> {
        private String[] titles;

        public ListOfTitlesMatcher(String... titles) {
            this.titles = titles;
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof List) {
                List questionList = (List) argument;
                for (int i = 0, questionListSize = questionList.size(); i < questionListSize; i++) {
                    Object obj = questionList.get(i);
                    if (obj instanceof Question) {
                        Question question = (Question) obj;
                        if (!StringUtils.equalsIgnoreCase(question.getTitle(), titles[i])) return false;
                    }
                    else if (obj instanceof QuestionGroupForm) {
                        QuestionGroupForm questionGroupForm = (QuestionGroupForm) obj;
                        if (!StringUtils.equalsIgnoreCase(questionGroupForm.getTitle(), titles[i])) return false;
                    }
                }
                return true;
            }
            return true;
        }
    }

    private class QuestionGroupFormMatcher extends ArgumentMatcher<QuestionGroupForm> {
        private String title;

        public QuestionGroupFormMatcher(String title) {
            this.title = title;
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof QuestionGroupForm) {
                QuestionGroupForm questionGroupForm = (QuestionGroupForm) argument;
                return StringUtils.equalsIgnoreCase(this.title, questionGroupForm.getTitle());
            }
            return false;

        }
    }
}
