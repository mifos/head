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
import org.mifos.platform.questionnaire.QuestionnaireConstants;
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
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
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
        questionForm.setQuestions(asList(getQuestion("0", TITLE, "Number")));
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
        verify(questionnaireServiceFacade).createQuestionGroup(argThat(new QuestionGroupFormTitleMatcher(TITLE)));
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
    public void shouldGetAllQuestions() {
        when(questionnaireServiceFacade.getAllQuestions()).thenReturn(asList(getQuestion("1", "title1", "Number"), getQuestion("2", "title2", "Number")));
        String view = questionnaireController.getAllQuestions(model, httpServletRequest);
        assertThat(view, is("viewQuestions"));
        verify(questionnaireServiceFacade).getAllQuestions();
        verify(model).addAttribute(eq("questions"), argThat(new ListOfQuestionsMatcher(getQuestion("1", "title1", "Number"), getQuestion("2", "title2", "Number"))));
    }

    @Test
    public void shouldGetAllQuestionGroups() {
        when(questionnaireServiceFacade.getAllQuestionGroups()).thenReturn(asList(getQuestionGroupForm("1", "title1"), getQuestionGroupForm("2", "title2")));
        String view = questionnaireController.getAllQuestionGroups(model, httpServletRequest);
        assertThat(view, is("viewQuestionGroups"));
        verify(questionnaireServiceFacade).getAllQuestionGroups();
        verify(model).addAttribute(eq("questionGroups"), argThat(new ListOfQuestionGroupFormMatcher(getQuestionGroupForm("1", "title1"), getQuestionGroupForm("2", "title2"))));
    }

    private QuestionGroupForm getQuestionGroupForm(String id, String title) {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setId(id);
        questionGroupForm.setTitle(title);
        return questionGroupForm;
    }

    @Test
    public void shouldGetQuestionById() throws ApplicationException {
        int questionId = 1;
        Question question = getQuestion(Integer.toString(questionId), TITLE, "Number");
        when(questionnaireServiceFacade.getQuestion(questionId)).thenReturn(question);
        when(httpServletRequest.getParameter("questionId")).thenReturn(Integer.toString(questionId));
        String view = questionnaireController.getQuestion(model, httpServletRequest);
        assertThat(view, is("viewQuestionDetail"));
        verify(questionnaireServiceFacade).getQuestion(questionId);
        verify(httpServletRequest, times(1)).getParameter("questionId");
        verify(model).addAttribute(eq("questionDetail"), argThat(new QuestionMatcher(getQuestion(Integer.toString(questionId), TITLE, "Number"))));
    }

    @Test
    public void testGetQuestionWhenNotPresentInDb() throws ApplicationException {
        int questionId = 1;
        when(questionnaireServiceFacade.getQuestion(questionId)).thenThrow(new ApplicationException(QuestionnaireConstants.QUESTION_NOT_FOUND));
        when(httpServletRequest.getParameter("questionId")).thenReturn(Integer.toString(questionId));
        String view = questionnaireController.getQuestion(model, httpServletRequest);
        assertThat(view, is("viewQuestionDetail"));
        verify(questionnaireServiceFacade).getQuestion(questionId);
        verify(httpServletRequest, times(1)).getParameter("questionId");
        verify(model).addAttribute("error_message_code", QuestionnaireConstants.QUESTION_NOT_FOUND);
    }

    @Test
    public void testGetQuestionWhenIdIsNull() throws ApplicationException {
        when(httpServletRequest.getParameter("questionId")).thenReturn(null);
        String view = questionnaireController.getQuestion(model, httpServletRequest);
        assertThat(view, is("viewQuestionDetail"));
        verify(httpServletRequest, times(1)).getParameter("questionId");
        verify(questionnaireServiceFacade, times(0)).getQuestion(anyInt());
        verify(model).addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_ID);
    }

    @Test
    public void testGetQuestionWhenIdIsNotInteger() throws ApplicationException {
        when(httpServletRequest.getParameter("questionId")).thenReturn("1A");
        String view = questionnaireController.getQuestion(model, httpServletRequest);
        assertThat(view, is("viewQuestionDetail"));
        verify(httpServletRequest, times(1)).getParameter("questionId");
        verify(questionnaireServiceFacade, times(0)).getQuestion(anyInt());
        verify(model).addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_ID);
    }


    @Test
    public void shouldGetQuestionGroupById() throws ApplicationException {
        int questionGroupId = 1;
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(questionGroupId, TITLE);
        when(questionnaireServiceFacade.getQuestionGroup(questionGroupId)).thenReturn(questionGroupForm);
        when(httpServletRequest.getParameter("questionGroupId")).thenReturn(Integer.toString(questionGroupId));
        String view = questionnaireController.getQuestionGroup(model, httpServletRequest);
        assertThat(view, is("viewQuestionGroupDetail"));
        verify(questionnaireServiceFacade).getQuestionGroup(questionGroupId);
        verify(httpServletRequest, times(1)).getParameter("questionGroupId");
        verify(model).addAttribute(eq("questionGroupDetail"), argThat(new QuestionGroupFormMatcher(getQuestionGroupForm(questionGroupId, TITLE))));
    }

    private QuestionGroupForm getQuestionGroupForm(int questionGroupId, String title) {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setId(Integer.toString(questionGroupId));
        questionGroupForm.setTitle(title);
        return questionGroupForm;
    }

    @Test
    public void testGetQuestionGroupWhenNotPresentInDb() throws ApplicationException {
        int questionGroupId = 1;
        when(questionnaireServiceFacade.getQuestionGroup(questionGroupId)).thenThrow(new ApplicationException(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        when(httpServletRequest.getParameter("questionGroupId")).thenReturn("1");
        String view = questionnaireController.getQuestionGroup(model, httpServletRequest);
        assertThat(view, is("viewQuestionGroupDetail"));
        verify(questionnaireServiceFacade).getQuestionGroup(questionGroupId);
        verify(httpServletRequest, times(1)).getParameter("questionGroupId");
        verify(model).addAttribute("error_message_code", QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND);
    }

    @Test
    public void testGetQuestionGroupWhenIdIsNull() throws ApplicationException {
        when(httpServletRequest.getParameter("questionGroupId")).thenReturn(null);
        String view = questionnaireController.getQuestionGroup(model, httpServletRequest);
        assertThat(view, is("viewQuestionGroupDetail"));
        verify(httpServletRequest, times(1)).getParameter("questionGroupId");
        verify(questionnaireServiceFacade, times(0)).getQuestionGroup(anyInt());
        verify(model).addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_GROUP_ID);
    }

    @Test
    public void testGetQuestionGroupWhenIdIsNotInteger() throws ApplicationException {
        when(httpServletRequest.getParameter("questionGroupId")).thenReturn("1A");
        String view = questionnaireController.getQuestionGroup(model, httpServletRequest);
        assertThat(view, is("viewQuestionGroupDetail"));
        verify(httpServletRequest, times(1)).getParameter("questionGroupId");
        verify(questionnaireServiceFacade, times(0)).getQuestionGroup(anyInt());
        verify(model).addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_GROUP_ID);
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

    private Question getQuestion(String id, String title, String type) {
        Question question = new Question();
        question.setTitle(title);
        question.setId(id);
        question.setType(type);
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

    private class ListOfQuestionsMatcher extends ArgumentMatcher<List> {
        private Question[] questions;

        public ListOfQuestionsMatcher(Question... questions) {
            this.questions = questions;
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof List) {
                List questionList = (List) argument;
                for (int i = 0, questionListSize = questionList.size(); i < questionListSize; i++) {
                    Object obj = questionList.get(i);
                    if (obj instanceof Question) {
                        Question question = (Question) obj;
                        if (!(
                                equalsIgnoreCase(question.getId(), questions[i].getId())
                                        && equalsIgnoreCase(question.getTitle(), questions[i].getTitle())
                                        && equalsIgnoreCase(question.getType(), questions[i].getType())
                        )) return false;
                    } else {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

    private class QuestionMatcher extends ArgumentMatcher<Question> {
        private Question question;

        public QuestionMatcher(Question question) {
            this.question = question;
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof Question) {
                Question question = (Question) argument;
                return (equalsIgnoreCase(this.question.getType(), question.getType())
                        && equalsIgnoreCase(this.question.getId(), question.getId())
                        && equalsIgnoreCase(this.question.getTitle(), question.getTitle()));
            }
            return false;
        }
    }

    private class QuestionGroupFormMatcher extends ArgumentMatcher<QuestionGroupForm> {
        private QuestionGroupForm questionGroupForm;

        public QuestionGroupFormMatcher(QuestionGroupForm questionGroupForm) {
            this.questionGroupForm = questionGroupForm;
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof QuestionGroupForm) {
                QuestionGroupForm questionGroupForm = (QuestionGroupForm) argument;
                return (equalsIgnoreCase(this.questionGroupForm.getTitle(), questionGroupForm.getTitle())
                        && equalsIgnoreCase(this.questionGroupForm.getId(), questionGroupForm.getId())
                );
            }
            return false;
        }
    }

    private class QuestionGroupFormTitleMatcher extends ArgumentMatcher<QuestionGroupForm> {
        private String title;

        public QuestionGroupFormTitleMatcher(String title) {
            this.title = title;
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof QuestionGroupForm) {
                QuestionGroupForm questionGroupForm = (QuestionGroupForm) argument;
                return equalsIgnoreCase(this.title, questionGroupForm.getTitle());
            }
            return false;
        }
    }

    private class ListOfQuestionGroupFormMatcher extends ArgumentMatcher<List> {
        private QuestionGroupForm[] questionGroupForms;

        public ListOfQuestionGroupFormMatcher(QuestionGroupForm... questionGroupForms) {
            this.questionGroupForms = questionGroupForms;
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof List) {
                List questionGroupForms = (List) argument;
                for (int i = 0, questionGroupFormsSize = questionGroupForms.size(); i < questionGroupFormsSize; i++) {
                    Object obj = questionGroupForms.get(i);
                    if (obj instanceof QuestionGroupForm) {
                        QuestionGroupForm questionGroupForm = (QuestionGroupForm) obj;
                        if (!(equalsIgnoreCase(questionGroupForm.getTitle(), this.questionGroupForms[i].getTitle())
                                && equalsIgnoreCase(questionGroupForm.getId(), this.questionGroupForms[i].getId())
                        )) return false;
                    } else {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }
}
