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

import org.apache.commons.lang.StringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.matchers.QuestionGroupDetailMatcher;
import org.mifos.platform.questionnaire.service.*;
import org.mifos.platform.questionnaire.ui.model.Question;
import org.mifos.platform.questionnaire.ui.model.QuestionForm;
import org.mifos.platform.questionnaire.ui.model.QuestionGroupForm;
import org.mifos.platform.questionnaire.ui.model.SectionDetailForm;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.binding.message.DefaultMessageResolver;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.ui.ModelMap;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.notNullValue;
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

    LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();

    @Before
    public void setUp() throws Exception {
        questionnaireController = new QuestionnaireController(questionnaireServiceFacade);
        validator.afterPropertiesSet();
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
        questions.add(getQuestion("1", title,"Free text"));
        questionnaireController.removeQuestion(questionForm, "");
        questionnaireController.removeQuestion(questionForm, "junk");        
        Assert.assertThat(questionForm.getQuestions().size(), Is.is(1));
        questionnaireController.removeQuestion(questionForm, title);
        Assert.assertThat(questionForm.getQuestions().size(), Is.is(0));
    }

    @Test
    public void testAddQuestionForSuccess() throws Exception {
        QuestionForm questionForm = getQuestionForm(TITLE, "Numeric");
        Mockito.when(questionnaireServiceFacade.isDuplicateQuestion(TITLE)).thenReturn(false);
        Mockito.when(messageContext.getAllMessages()).thenReturn(new Message[] {});
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionnaireController.addQuestion(questionForm, requestContext);
        List<Question> questions = questionForm.getQuestions();
        Mockito.verify(questionnaireServiceFacade).isDuplicateQuestion(TITLE);
        Assert.assertThat(questions, Is.is(notNullValue()));
        Assert.assertThat(questions.size(), Is.is(1));
        Assert.assertThat(result, Is.is("success"));
    }

    @Test
    public void testAddQuestionForFailureWhenQuestionTitleNotProvided() throws Exception {
        QuestionForm questionForm = new QuestionForm();
        questionForm.setValidator(validator);
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        Mockito.when(messageContext.hasErrorMessages()).thenReturn(true);
        String result = questionnaireController.addQuestion(questionForm, requestContext);
        Assert.assertThat(questionForm.getQuestions().size(), Is.is(0));
        Assert.assertThat(result, Is.is(notNullValue()));
        Assert.assertThat(result, Is.is("failure"));
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("NotEmpty.QuestionForm.currentQuestion.title")));
    }

    @Test
    public void testAddQuestionForFailureWhenQuestionTitleProvidedWithAllBlanks() throws Exception {
        QuestionForm qform = new QuestionForm();
        qform.setValidator(validator);
        qform.getCurrentQuestion().setTitle("      ");
        qform.getCurrentQuestion().setType("Free Text");
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        Mockito.when(messageContext.hasErrorMessages()).thenReturn(true);
        String result = questionnaireController.addQuestion(qform, requestContext);
        Assert.assertThat(qform.getQuestions().size(), Is.is(0));
        Assert.assertThat(result, Is.is(notNullValue()));
        Assert.assertThat(result, Is.is("failure"));
        Mockito.verify(requestContext).getMessageContext();
        // TODO: Assert for message code content
//        verify(messageContext).addMessage(argThat(new MessageMatcher("NotEmpty.QuestionForm.currentQuestion.title")));
    }

    @Test
    public void testAddQuestionForFailureWhenQuestionTitleIsDuplicateInDB() throws Exception {
        QuestionForm questionForm = new QuestionForm();
        questionForm.getCurrentQuestion().setTitle(TITLE);
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        Mockito.when(questionnaireServiceFacade.isDuplicateQuestion(TITLE)).thenReturn(true);
        String result = questionnaireController.addQuestion(questionForm, requestContext);
        Assert.assertThat(questionForm.getQuestions().size(), Is.is(0));
        Assert.assertThat(result, Is.is("failure"));
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.question.duplicate")));
    }

    @Test
    public void testAddQuestionForFailureWhenQuestionTitleIsDuplicateInForm() throws Exception {
        QuestionForm questionForm = new QuestionForm();
        questionForm.getCurrentQuestion().setTitle("  " + TITLE + "    ");
        questionForm.setQuestions(asList(getQuestion("0", TITLE, "Number")));
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionnaireController.addQuestion(questionForm, requestContext);
        Assert.assertThat(questionForm.getQuestions().size(), Is.is(1));
        Assert.assertThat(result, Is.is(notNullValue()));
        Assert.assertThat(result, Is.is("failure"));
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.question.duplicate")));
    }

    @Test
    public void testDeleteSection() {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        String sectionName = "sectionName";
        questionGroup.setSectionName(sectionName);
        questionGroup.addCurrentSection();
        Assert.assertThat(questionGroup.getSections().size(), Is.is(1));
        questionnaireController.deleteSection(questionGroup, sectionName);
        Assert.assertThat(questionGroup.getSections().size(), Is.is(0));
    }

    @Test
    public void testAddSectionForSuccess() throws Exception {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setQuestionPool(new ArrayList<SectionQuestionDetail>(asList(getSectionQuestionDetail(1, "Q1"), getSectionQuestionDetail(2, "Q2"))));
        questionGroup.setSelectedQuestionIds(asList("1"));
        questionGroup.setTitle("title");
        questionGroup.setSectionName("sectionName");
        String result = questionnaireController.addSection(questionGroup, requestContext);
        Assert.assertThat(questionGroup.getSections().size(), Is.is(1));
        Assert.assertThat(questionGroup.getSections().get(0).getName(), Is.is("sectionName"));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), Is.is(1));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getTitle(), Is.is("Q1"));
        Assert.assertThat(result, Is.is("success"));
    }

    @Test
    public void testAddSectionsSuccessWhenSectionNameIsNotProvided() {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setQuestionPool(new ArrayList<SectionQuestionDetail>(asList(getSectionQuestionDetail(1, "Q1"), getSectionQuestionDetail(2, "Q2"))));
        questionGroup.setSelectedQuestionIds(asList("2"));
        String result = questionnaireController.addSection(questionGroup, requestContext);
        Assert.assertThat(questionGroup.getSections().size(), Is.is(1));
        Assert.assertThat(questionGroup.getSections().get(0).getName(), Is.is("Misc"));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), Is.is(1));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getTitle(), Is.is("Q2"));
        Assert.assertThat(result, Is.is("success"));
    }

    @Test
    public void testAddSectionForSuccessWhenQuestionTitleProvidedWithAllBlanks() throws Exception {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setQuestionPool(new ArrayList<SectionQuestionDetail>(asList(getSectionQuestionDetail(1, "Q1"), getSectionQuestionDetail(2, "Q2"))));
        questionGroup.setSelectedQuestionIds(asList("1", "2"));
        questionGroup.setSectionName("        ");
        String result = questionnaireController.addSection(questionGroup, requestContext);
        Assert.assertThat(questionGroup.getSections().size(), Is.is(1));
        Assert.assertThat(questionGroup.getSections().get(0).getName(), Is.is("Misc"));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), Is.is(2));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getTitle(), Is.is("Q1"));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(1).getTitle(), Is.is("Q2"));
        Assert.assertThat(result, Is.is("success"));
    }

    @Test
    public void testAddSectionWithoutQuestions() throws Exception {
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setTitle("title");
        questionGroup.setSectionName("sectionName");
        String result = questionnaireController.addSection(questionGroup, requestContext);
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.no.question.in.section")));
        Assert.assertThat(result, Is.is("failure"));
    }

    @Test
    public void testRemoveQuestionFromSection() {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        List<SectionDetailForm> sections = new ArrayList<SectionDetailForm>();
        sections.add(getSectionSectionDetailForm("sectionName", new ArrayList<SectionQuestionDetail>(asList(getSectionQuestionDetail(1, "Q1"), getSectionQuestionDetail(2, "Q2")))));
        questionGroup.setSections(sections);

        Assert.assertThat(questionGroup.getSections().size(), CoreMatchers.is(1));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), CoreMatchers.is(2));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getTitle(), CoreMatchers.is("Q1"));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(1).getTitle(), CoreMatchers.is("Q2"));

        questionnaireController.deleteQuestion(questionGroup, "sectionName", "1");

        Assert.assertThat(questionGroup.getSections().size(), CoreMatchers.is(1));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), CoreMatchers.is(1));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getTitle(), CoreMatchers.is("Q2"));

        questionnaireController.deleteQuestion(questionGroup, "sectionName", "2");

        Assert.assertThat(questionGroup.getSections().size(), CoreMatchers.is(0));
    }

    private SectionDetailForm getSectionSectionDetailForm(String sectionName, List<SectionQuestionDetail> questions) {
        SectionDetailForm section = new SectionDetailForm();
        section.setName(sectionName);
        section.setQuestionDetails(questions);
        return section;
    }

    private SectionQuestionDetail getSectionQuestionDetail(int id, String title) {
        return new SectionQuestionDetail(id, title, true);
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testCreateQuestionFailure() throws Exception {
        QuestionForm questionForm = getQuestionForm(TITLE, "Numeric");
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        Mockito.doThrow(new SystemException("db.write.failure")).when(questionnaireServiceFacade).createQuestions(Matchers.<List<QuestionDetail>>anyObject());
        String result = questionnaireController.createQuestions(questionForm, requestContext);
        Assert.assertThat(result, Is.is("failure"));
        Mockito.verify(questionnaireServiceFacade).createQuestions(Matchers.<List<QuestionDetail>>anyObject());
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("db.write.failure")));
    }

    @Test
    public void testCreateQuestionSuccess() throws Exception {
        QuestionForm questionForm = getQuestionForm(TITLE, "Date");
        String result = questionnaireController.createQuestions(questionForm, requestContext);
        Assert.assertThat(result, Is.is("success"));
        Mockito.verify(questionnaireServiceFacade).createQuestions(anyListOf(QuestionDetail.class));
    }

    @Test
    public void testCreateQuestionGroupSuccess() throws Exception {
        QuestionGroupForm questionGroup = getQuestionGroupForm("   " + TITLE + " ", "Create.Client", "S1", "S2");
        String result = questionnaireController.defineQuestionGroup(questionGroup, requestContext);
        Assert.assertThat(result, Is.is("success"));
        Mockito.verify(questionnaireServiceFacade).createQuestionGroup(argThat(new QuestionGroupDetailMatcher(getQuestionGroupDetail(TITLE, "Create", "Client", "S1", "S2"))));
    }

    private QuestionGroupDetail getQuestionGroupDetail(String title, String event, String source, String... sectionNames) {
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail();
        questionGroupDetail.setTitle(title);
        questionGroupDetail.setEventSource(new EventSource(event, source, null));
        List<SectionDetail> sectionDetails = new ArrayList<SectionDetail>();
        for (String sectionName : sectionNames) {
            SectionDetail sectionDetail = new SectionDetail();
            sectionDetail.setName(sectionName);
            sectionDetails.add(sectionDetail);
        }
        questionGroupDetail.setSectionDetails(sectionDetails);
        return questionGroupDetail;
    }

    @Test
    public void testCreateQuestionGroupForFailureWhenQuestionGroupTitleNotProvided() throws Exception {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(null, "Create.Client", "Section");
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionnaireController.defineQuestionGroup(questionGroupForm, requestContext);
        Assert.assertThat(result, Is.is(notNullValue()));
        Assert.assertThat(result, Is.is("failure"));
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.questionGroup.title.empty")));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testCreateQuestionGroupFailureWhenSectionsNotPresent() throws Exception {
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE, "Create.Client");
        String result = questionnaireController.defineQuestionGroup(questionGroupForm, requestContext);
        Assert.assertThat(result, Is.is("failure"));
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.section.atLeastOne")));
    }

    @Test
    public void testCreateQuestionGroupForFailureWhenQuestionGroupAppliesToNotProvided() throws Exception {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE, QuestionnaireConstants.DEFAULT_APPLIES_TO_OPTION, "Section");
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionnaireController.defineQuestionGroup(questionGroupForm, requestContext);
        Assert.assertThat(result, Is.is(notNullValue()));
        Assert.assertThat(result, Is.is("failure"));
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.appliesTo.mandatory")));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testCreateQuestionGroupFailure() throws Exception {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE, "Create.Client", "S1", "S2");
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        Mockito.doThrow(new SystemException("questionnaire.error.duplicate.question.found.in.section")).when(questionnaireServiceFacade).createQuestionGroup(Matchers.<QuestionGroupDetail>anyObject());
        String result = questionnaireController.defineQuestionGroup(questionGroupForm, requestContext);
        Assert.assertThat(result, Is.is("failure"));
        Mockito.verify(questionnaireServiceFacade).createQuestionGroup(Matchers.<QuestionGroupDetail>anyObject());
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.duplicate.question.found.in.section")));
    }

    @Test
    public void shouldGetAllQuestions() {
        List<QuestionDetail> questionDetailList = asList(getQuestionDetail(1, "title1", QuestionType.NUMERIC), getQuestionDetail(2, "title2", QuestionType.NUMERIC));
        Mockito.when(questionnaireServiceFacade.getAllQuestions()).thenReturn(questionDetailList);
        String view = questionnaireController.getAllQuestions(model, httpServletRequest);
        Assert.assertThat(view, Is.is("viewQuestions"));
        Mockito.verify(questionnaireServiceFacade).getAllQuestions();
        Mockito.verify(model).addAttribute(eq("questions"), eq(questionDetailList));
    }

    @Test
    public void shouldGetAllQuestionGroups() {
        List<QuestionGroupDetail> questionGroupDetails = asList(
                getQuestionGroupDetail(1, TITLE, "title1", "sectionName1"), getQuestionGroupDetail(1, TITLE, "title1", "sectionName1"));
        Mockito.when(questionnaireServiceFacade.getAllQuestionGroups()).thenReturn(questionGroupDetails);
        String view = questionnaireController.getAllQuestionGroups(model, httpServletRequest);
        Assert.assertThat(view, Is.is("viewQuestionGroups"));
        Mockito.verify(questionnaireServiceFacade).getAllQuestionGroups();
        Mockito.verify(model).addAttribute(eq("questionGroups"), argThat(new ListOfQuestionGroupDetailMatcher(questionGroupDetails)));
    }

    @Test
    public void shouldGetQuestionById() throws SystemException {
        int questionId = 1;
        QuestionDetail questionDetail = getQuestionDetail(questionId, TITLE, QuestionType.NUMERIC);
        Mockito.when(questionnaireServiceFacade.getQuestionDetail(questionId)).thenReturn(questionDetail);
        Mockito.when(httpServletRequest.getParameter("questionId")).thenReturn(Integer.toString(questionId));
        String view = questionnaireController.getQuestion(model, httpServletRequest);
        Assert.assertThat(view, Is.is("viewQuestionDetail"));
        Mockito.verify(questionnaireServiceFacade).getQuestionDetail(questionId);
        Mockito.verify(httpServletRequest, Mockito.times(1)).getParameter("questionId");
        Mockito.verify(model).addAttribute(eq("questionDetail"), argThat(new QuestionMatcher("1", TITLE, "Number")));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testGetQuestionWhenNotPresentInDb() throws SystemException {
        int questionId = 1;
        Mockito.when(questionnaireServiceFacade.getQuestionDetail(questionId)).thenThrow(new SystemException(QuestionnaireConstants.QUESTION_NOT_FOUND));
        Mockito.when(httpServletRequest.getParameter("questionId")).thenReturn(Integer.toString(questionId));
        String view = questionnaireController.getQuestion(model, httpServletRequest);
        Assert.assertThat(view, Is.is("viewQuestionDetail"));
        Mockito.verify(questionnaireServiceFacade).getQuestionDetail(questionId);
        Mockito.verify(httpServletRequest, Mockito.times(1)).getParameter("questionId");
        Mockito.verify(model).addAttribute("error_message_code", QuestionnaireConstants.QUESTION_NOT_FOUND);
    }

    @Test
    public void testGetQuestionWhenIdIsNull() throws SystemException {
        Mockito.when(httpServletRequest.getParameter("questionId")).thenReturn(null);
        String view = questionnaireController.getQuestion(model, httpServletRequest);
        Assert.assertThat(view, Is.is("viewQuestionDetail"));
        Mockito.verify(httpServletRequest, Mockito.times(1)).getParameter("questionId");
        Mockito.verify(questionnaireServiceFacade, Mockito.times(0)).getQuestionDetail(anyInt());
        Mockito.verify(model).addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_ID);
    }

    @Test
    public void testGetQuestionWhenIdIsNotInteger() throws SystemException {
        Mockito.when(httpServletRequest.getParameter("questionId")).thenReturn("1A");
        String view = questionnaireController.getQuestion(model, httpServletRequest);
        Assert.assertThat(view, Is.is("viewQuestionDetail"));
        Mockito.verify(httpServletRequest, Mockito.times(1)).getParameter("questionId");
        Mockito.verify(questionnaireServiceFacade, Mockito.times(0)).getQuestionDetail(anyInt());
        Mockito.verify(model).addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_ID);
    }

    @Test
    public void shouldGetQuestionGroupById() throws SystemException {
        int questionGroupId = 1;
        QuestionGroupDetail questionGroupDetail = getQuestionGroupDetail(questionGroupId, TITLE, "S1", "S2", "S3");
        Mockito.when(questionnaireServiceFacade.getQuestionGroupDetail(questionGroupId)).thenReturn(questionGroupDetail);
        Mockito.when(httpServletRequest.getParameter("questionGroupId")).thenReturn(Integer.toString(questionGroupId));
        String view = questionnaireController.getQuestionGroup(model, httpServletRequest);
        Assert.assertThat(view, Is.is("viewQuestionGroupDetail"));
        Mockito.verify(questionnaireServiceFacade).getQuestionGroupDetail(questionGroupId);
        Mockito.verify(questionnaireServiceFacade, Mockito.times(1)).getAllEventSources();
        Mockito.verify(httpServletRequest, Mockito.times(1)).getParameter("questionGroupId");
        Mockito.verify(model).addAttribute(eq("questionGroupDetail"), argThat(new QuestionGroupDetailFormMatcher(new QuestionGroupForm(questionGroupDetail))));
    }

    private QuestionGroupDetail getQuestionGroupDetail(int questionGroupId, String title, String... sectionNames) {
        List<SectionDetail> sectionDetails = new ArrayList<SectionDetail>();
        for (String sectionName : sectionNames) {
            SectionDetail sectionDetail = new SectionDetail();
            sectionDetail.setName(sectionName);
            sectionDetails.add(sectionDetail);
        }
        return new QuestionGroupDetail(questionGroupId, title, sectionDetails);
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testGetQuestionGroupWhenNotPresentInDb() throws SystemException {
        int questionGroupId = 1;
        Mockito.when(questionnaireServiceFacade.getQuestionGroupDetail(questionGroupId)).thenThrow(new SystemException(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        Mockito.when(httpServletRequest.getParameter("questionGroupId")).thenReturn("1");
        String view = questionnaireController.getQuestionGroup(model, httpServletRequest);
        Assert.assertThat(view, Is.is("viewQuestionGroupDetail"));
        Mockito.verify(questionnaireServiceFacade).getQuestionGroupDetail(questionGroupId);
        Mockito.verify(httpServletRequest, Mockito.times(1)).getParameter("questionGroupId");
        Mockito.verify(model).addAttribute("error_message_code", QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND);
    }

    @Test
    public void testGetQuestionGroupWhenIdIsNull() throws SystemException {
        Mockito.when(httpServletRequest.getParameter("questionGroupId")).thenReturn(null);
        String view = questionnaireController.getQuestionGroup(model, httpServletRequest);
        Assert.assertThat(view, Is.is("viewQuestionGroupDetail"));
        Mockito.verify(httpServletRequest, Mockito.times(1)).getParameter("questionGroupId");
        Mockito.verify(questionnaireServiceFacade, Mockito.times(0)).getQuestionGroupDetail(anyInt());
        Mockito.verify(model).addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_GROUP_ID);
    }

    @Test
    public void testGetQuestionGroupWhenIdIsNotInteger() throws SystemException {
        Mockito.when(httpServletRequest.getParameter("questionGroupId")).thenReturn("1A");
        String view = questionnaireController.getQuestionGroup(model, httpServletRequest);
        Assert.assertThat(view, Is.is("viewQuestionGroupDetail"));
        Mockito.verify(httpServletRequest, Mockito.times(1)).getParameter("questionGroupId");
        Mockito.verify(questionnaireServiceFacade, Mockito.times(0)).getQuestionGroupDetail(anyInt());
        Mockito.verify(model).addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_GROUP_ID);
    }

    @Test
    public void shouldGetAllQgEventSources() {
        Mockito.when(questionnaireServiceFacade.getAllEventSources()).thenReturn(asList(new EventSource("Create", "Client", "Create Client"), new EventSource("View", "Client", "View Client")));
        Map<String, String> eventSources = questionnaireController.getAllQgEventSources();
        Mockito.verify(questionnaireServiceFacade).getAllEventSources();
        Assert.assertThat(eventSources.get("Create.Client"), Is.is("Create Client"));
        Assert.assertThat(eventSources.get("View.Client"), Is.is("View Client"));
    }

    @Test
    public void shouldGetAllSectionQuestions() {
        Mockito.when(questionnaireServiceFacade.getAllQuestions()).thenReturn(asList(getQuestionDetail(1, "Q1", QuestionType.NUMERIC), getQuestionDetail(2, "Q2", QuestionType.DATE)));
        List<SectionQuestionDetail> sectionQuestions = questionnaireController.getAllSectionQuestions();
        Assert.assertThat(sectionQuestions, Is.is(notNullValue()));
        Assert.assertThat(sectionQuestions.size(), Is.is(2));
        Assert.assertThat(sectionQuestions.get(0).getQuestionId(), Is.is(1));
        Assert.assertThat(sectionQuestions.get(0).getTitle(), Is.is("Q1"));
        Assert.assertThat(sectionQuestions.get(1).getQuestionId(), Is.is(2));
        Assert.assertThat(sectionQuestions.get(1).getTitle(), Is.is("Q2"));
        Mockito.verify(questionnaireServiceFacade).getAllQuestions();
    }

    @Test
    public void shouldGetAllSectionQuestionsEmpty() {
        Mockito.when(questionnaireServiceFacade.getAllQuestions()).thenReturn(null);
        List<SectionQuestionDetail> sectionQuestions = questionnaireController.getAllSectionQuestions();
        Assert.assertThat(sectionQuestions, Is.is(notNullValue()));
        Assert.assertThat(sectionQuestions.size(), Is.is(0));
        Mockito.verify(questionnaireServiceFacade).getAllQuestions();
    }

    private QuestionForm getQuestionForm(String title, String type) {
        QuestionForm questionForm = new QuestionForm();
        questionForm.getCurrentQuestion().setTitle(title);
        questionForm.getCurrentQuestion().setType(type);
        return questionForm;
    }

    private QuestionGroupForm getQuestionGroupForm(String title, String eventSourceId, String... sectionNames) {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setTitle(title);
        questionGroup.setEventSourceId(eventSourceId);
        for (String sectionName : sectionNames) {
            questionGroup.setSectionName(sectionName);
            questionGroup.addCurrentSection();
        }
        return questionGroup;
    }

    private Question getQuestion(String id, String title, String type) {
        Question question = new Question();
        question.setTitle(title);
        question.setId(id);
        question.setType(type);
        return question;
    }

    private QuestionDetail getQuestionDetail(int id, String title, QuestionType type) {
        return new QuestionDetail(id, null, title, type);
    }

    private class MessageMatcher extends TypeSafeMatcher<MessageResolver> {
        private String errorCode;

        public MessageMatcher(String errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public boolean matchesSafely(MessageResolver messageResolver) {
            DefaultMessageResolver defaultMessageResolver = (DefaultMessageResolver) messageResolver;
            String[] codes = defaultMessageResolver.getCodes();
            for (String code : codes) {
                if (StringUtils.equals(code, errorCode)) return true;
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Messages do not match");
        }
    }

    private class QuestionMatcher extends TypeSafeMatcher<Question> {
        private String id;
        private String title;
        private String type;

        public QuestionMatcher(String id, String title, String type) {
            this.id = id;
            this.title = title;
            this.type = type;
        }

        @Override
        public boolean matchesSafely(Question question) {
            return (StringUtils.equalsIgnoreCase(type, question.getType())
                    && StringUtils.equalsIgnoreCase(id, question.getId())
                    && StringUtils.equalsIgnoreCase(title, question.getTitle()));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Question does not match");
        }
    }

    private class QuestionGroupDetailFormMatcher extends TypeSafeMatcher<QuestionGroupForm> {
        private QuestionGroupForm questionGroupForm;

        public QuestionGroupDetailFormMatcher(QuestionGroupForm questionGroupForm) {
            this.questionGroupForm = questionGroupForm;
        }

        @Override
        public boolean matchesSafely(QuestionGroupForm questionGroupForm) {
            if (StringUtils.equalsIgnoreCase(this.questionGroupForm.getTitle(), questionGroupForm.getTitle())
                    && this.questionGroupForm.getSections().size() == questionGroupForm.getSections().size()) {
                for (SectionDetailForm sectionDetailForm : this.questionGroupForm.getSections()) {
                    Assert.assertThat(questionGroupForm.getSections(), org.hamcrest.Matchers.hasItem(new SectionDetailFormMatcher(sectionDetailForm)));
                }
            } else {
                return false;
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("QuestionGroupDetailForm do not match");
        }
    }


    class SectionDetailFormMatcher extends TypeSafeMatcher<SectionDetailForm> {
        private SectionDetailForm sectionDetailForm;

        public SectionDetailFormMatcher(SectionDetailForm sectionDetailForm) {
            this.sectionDetailForm = sectionDetailForm;
        }

        @Override
        public boolean matchesSafely(SectionDetailForm sectionDetailForm) {
            return StringUtils.equals(this.sectionDetailForm.getName(), sectionDetailForm.getName());
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("SectionDetailForms do not match");
        }
    }

    private class ListOfQuestionGroupDetailMatcher extends TypeSafeMatcher<List<QuestionGroupDetail>> {
        private List<QuestionGroupDetail> questionGroupDetails;

        public ListOfQuestionGroupDetailMatcher(List<QuestionGroupDetail> questionGroupDetails) {
            this.questionGroupDetails = questionGroupDetails;
        }

        @Override
        public boolean matchesSafely(List<QuestionGroupDetail> questionGroupDetails) {
            if (this.questionGroupDetails.size() == questionGroupDetails.size()) {
                for (QuestionGroupDetail questionGroupDetail : this.questionGroupDetails) {
                    Assert.assertThat(questionGroupDetails, org.hamcrest.Matchers.hasItem(new QuestionGroupDetailMatcher(questionGroupDetail)));
                }
            } else {
                return false;
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("List of question group details do not match");
        }
    }
}
