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
package org.mifos.platform.questionnaire.ui.controller;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.matchers.MessageMatcher;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.ui.model.Question;
import org.mifos.platform.questionnaire.ui.model.QuestionForm;
import org.mifos.platform.validation.MifosBeanValidator;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageContext;
import org.springframework.ui.ModelMap;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.argThat;

@RunWith(MockitoJUnitRunner.class)
public class QuestionControllerTest {

    private QuestionController questionController;

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

    MifosBeanValidator validator = new MifosBeanValidator();
    LocalValidatorFactoryBean targetValidator = new LocalValidatorFactoryBean();

    @Before
    public void setUp() throws Exception {
        questionController = new QuestionController(questionnaireServiceFacade);
        targetValidator.afterPropertiesSet();
        validator.setTargetValidator(targetValidator);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testRemoveQuestion() {
        QuestionForm questionForm = new QuestionForm();
        ArrayList<Question> questions = new ArrayList<Question>();
        questionForm.setQuestions(questions);
        String title = "title";
        questions.add(getQuestion("1", title,"Free Text"));
        questionController.removeQuestion(questionForm, "");
        questionController.removeQuestion(questionForm, "junk");
        Assert.assertThat(questionForm.getQuestions().size(), is(1));
        questionController.removeQuestion(questionForm, title);
        Assert.assertThat(questionForm.getQuestions().size(), is(0));
    }

    @Test
    public void testAddQuestionForSuccess() throws Exception {
        QuestionForm questionForm = getQuestionForm(TITLE, "Numeric");
        Mockito.when(questionnaireServiceFacade.isDuplicateQuestion(TITLE)).thenReturn(false);
        Mockito.when(messageContext.getAllMessages()).thenReturn(new Message[] {});
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionController.addQuestion(questionForm, requestContext, true);
        List<Question> questions = questionForm.getQuestions();
        Mockito.verify(questionnaireServiceFacade).isDuplicateQuestion(TITLE);
        Assert.assertThat(questions, is(notNullValue()));
        Assert.assertThat(questions.size(), is(1));
        Assert.assertThat(result, is("success"));
    }

    @Test
    public void testAddQuestionForFailureWhenQuestionTitleNotProvided() throws Exception {
        QuestionForm questionForm = new QuestionForm();
        questionForm.setValidator(validator);
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        Mockito.when(messageContext.hasErrorMessages()).thenReturn(true);
        String result = questionController.addQuestion(questionForm, requestContext, true);
        Assert.assertThat(questionForm.getQuestions().size(), is(0));
        Assert.assertThat(result, is(notNullValue()));
        Assert.assertThat(result, is("failure"));
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("NotNull.QuestionForm.currentQuestion.title")));
    }
    
    @Test
    public void testAddQuestionForFailureWhenInvalidNumericBoundsGiven() throws Exception {
        QuestionForm questionForm = getQuestionForm(TITLE, "Number");
        questionForm.getCurrentQuestion().setNumericMin(100);
        questionForm.getCurrentQuestion().setNumericMax(10);
        questionForm.setValidator(validator);
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        Mockito.when(messageContext.hasErrorMessages()).thenReturn(false);
        String result = questionController.addQuestion(questionForm, requestContext, true);
        Assert.assertThat(questionForm.getQuestions().size(), is(0));
        Assert.assertThat(result, is("failure"));
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher(QuestionnaireConstants.INVALID_NUMERIC_BOUNDS)));
    }

    @Test
    public void testAddQuestionForFailureWhenQuestionTitleProvidedWithAllBlanks() throws Exception {
        QuestionForm qform = new QuestionForm();
        qform.setValidator(validator);
        qform.getCurrentQuestion().setTitle("   ");
        qform.getCurrentQuestion().setType("Free Text");
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        Mockito.when(messageContext.hasErrorMessages()).thenReturn(true);
        String result = questionController.addQuestion(qform, requestContext, true);
        Assert.assertThat(qform.getQuestions().size(), is(0));
        Assert.assertThat(result, is(notNullValue()));
        Assert.assertThat(result, is("failure"));
        Mockito.verify(requestContext).getMessageContext();
        // TODO: Assert for message code content
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("Pattern.QuestionForm.currentQuestion.title")));
        //verify(messageContext).addMessage(argThat(new MessageMatcher("NotNull.QuestionForm.currentQuestion.type")));

    }

    @Test
    public void testAddQuestionForFailureWhenQuestionTitleIsDuplicateInDB() throws Exception {
        QuestionForm questionForm = new QuestionForm();
        questionForm.getCurrentQuestion().setTitle(TITLE);
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        Mockito.when(questionnaireServiceFacade.isDuplicateQuestion(TITLE)).thenReturn(true);
        String result = questionController.addQuestion(questionForm, requestContext, true);
        Assert.assertThat(questionForm.getQuestions().size(), is(0));
        Assert.assertThat(result, is("failure"));
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.question.duplicate")));
    }

    @Test
    public void testAddQuestionForFailureWhenQuestionTitleIsDuplicateInForm() throws Exception {
        QuestionForm questionForm = new QuestionForm();
        questionForm.getCurrentQuestion().setTitle("  " + TITLE + "    ");
        questionForm.setQuestions(Arrays.asList(getQuestion("0", TITLE, "Number")));
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionController.addQuestion(questionForm, requestContext, true);
        Assert.assertThat(questionForm.getQuestions().size(), is(1));
        Assert.assertThat(result, is(notNullValue()));
        Assert.assertThat(result, is("failure"));
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.question.duplicate")));
    }

    @Test
    public void testAddQuestionForFailureWhenLessThanTwoChoicesAreGivenForMultiSelect() throws Exception {
        QuestionForm questionForm = new QuestionForm();
        questionForm.getCurrentQuestion().setTitle("  " + TITLE + "    ");
        questionForm.getCurrentQuestion().setType("Multi Select");
        questionForm.getCurrentQuestion().setCurrentChoice("C1");
        questionForm.getCurrentQuestion().addAnswerChoice();
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionController.addQuestion(questionForm, requestContext, true);
        Assert.assertThat(result, is(notNullValue()));
        Assert.assertThat(result, is("failure"));
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.question.choices")));
    }

    @Test
    public void testAddQuestionForSuccessWhenTwoChoicesAreGivenForSigleSelect() throws Exception {
        QuestionForm questionForm = new QuestionForm();
        questionForm.getCurrentQuestion().setTitle("  " + TITLE + "    ");
        questionForm.getCurrentQuestion().setType("Single Select");
        questionForm.getCurrentQuestion().setCurrentChoice("C1");
        questionForm.getCurrentQuestion().addAnswerChoice();
        questionForm.getCurrentQuestion().setCurrentChoice("C2");
        questionForm.getCurrentQuestion().addAnswerChoice();
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionController.addQuestion(questionForm, requestContext, true);
        Assert.assertThat(result, is(notNullValue()));
        Assert.assertThat(result, is("success"));
        Assert.assertThat(questionForm.getQuestions().size(), is(1));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testCreateQuestionFailure() throws Exception {
        QuestionForm questionForm = getQuestionForm(TITLE, "Numeric");
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        Mockito.doThrow(new SystemException("db.write.failure")).when(questionnaireServiceFacade).createQuestions(Matchers.<List<QuestionDetail>>anyObject());
        String result = questionController.createQuestions(questionForm, requestContext);
        Assert.assertThat(result, is("failure"));
        Mockito.verify(questionnaireServiceFacade).createQuestions(Matchers.<List<QuestionDetail>>anyObject());
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("db.write.failure")));
    }

    @Test
    public void testCreateQuestionSuccess() throws Exception {
        QuestionForm questionForm = getQuestionForm(TITLE, "Date");
        String result = questionController.createQuestions(questionForm, requestContext);
        Assert.assertThat(result, is("success"));
        Mockito.verify(questionnaireServiceFacade).createQuestions(anyListOf(QuestionDetail.class));
    }

    @Test
    public void shouldGetAllQuestions() {
        List<QuestionDetail> questionDetailList = Arrays.asList(getQuestionDetail(1, "title1", QuestionType.NUMERIC), getQuestionDetail(2, "title2", QuestionType.NUMERIC));
        Mockito.when(questionnaireServiceFacade.getAllQuestions()).thenReturn(questionDetailList);
        String view = questionController.getAllQuestions(model, httpServletRequest);
        Assert.assertThat(view, is("viewQuestions"));
        Mockito.verify(questionnaireServiceFacade).getAllQuestions();
        Mockito.verify(model).addAttribute(Mockito.eq("questions"), Mockito.eq(questionDetailList));
    }

    private Question getQuestion(String id, String title, String type) {
        Question question = new Question(new QuestionDetail());
        question.setTitle(title);
        question.setId(id);
        question.setType(type);
        return question;
    }

    private QuestionDetail getQuestionDetail(int id, String title, QuestionType type) {
        return new QuestionDetail(id, title, title, type, true);
    }

    private QuestionForm getQuestionForm(String title, String type) {
        QuestionForm questionForm = new QuestionForm();
        questionForm.getCurrentQuestion().setTitle(title);
        questionForm.getCurrentQuestion().setType(type);
        return questionForm;
    }

}
