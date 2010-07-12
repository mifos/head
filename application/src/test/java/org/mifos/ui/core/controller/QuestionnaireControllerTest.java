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
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.contract.*;
import org.mifos.platform.questionnaire.matchers.QuestionGroupDetailMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
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
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.DEFAULT_APPLIES_TO_OPTION;
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
    public void testAddQuestionForSuccess() throws Exception {
        QuestionForm questionForm = getQuestionForm(TITLE, "Numeric");
        when(questionnaireServiceFacade.isDuplicateQuestion(TITLE)).thenReturn(false);
        when(messageContext.getAllMessages()).thenReturn(new Message[] {});
        when(requestContext.getMessageContext()).thenReturn(messageContext);
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
        questionForm.setValidator(validator);
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        when(messageContext.hasErrorMessages()).thenReturn(true);
        String result = questionnaireController.addQuestion(questionForm, requestContext);
        assertThat(questionForm.getQuestions().size(), is(0));
        assertThat(result, is(notNullValue()));
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("NotEmpty.QuestionForm.currentQuestion.title")));
    }

    @Test
    public void testAddQuestionForFailureWhenQuestionTitleProvidedWithAllBlanks() throws Exception {
        QuestionForm questionForm = new QuestionForm();
        questionForm.setValidator(validator);
        questionForm.getCurrentQuestion().setTitle("      ");
        questionForm.getCurrentQuestion().setType("Free Text");
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        when(messageContext.hasErrorMessages()).thenReturn(true);
        String result = questionnaireController.addQuestion(questionForm, requestContext);
        assertThat(questionForm.getQuestions().size(), is(0));
        assertThat(result, is(notNullValue()));
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        // TODO: Assert for message code content
//        verify(messageContext).addMessage(argThat(new MessageMatcher("NotEmpty.QuestionForm.currentQuestion.title")));
    }

    @Test
    public void testAddQuestionForFailureWhenQuestionTitleIsDuplicateInDB() throws Exception {
        QuestionForm questionForm = new QuestionForm();
        questionForm.getCurrentQuestion().setTitle(TITLE);
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        when(questionnaireServiceFacade.isDuplicateQuestion(TITLE)).thenReturn(true);
        String result = questionnaireController.addQuestion(questionForm, requestContext);
        assertThat(questionForm.getQuestions().size(), is(0));
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.question.duplicate")));
    }

    @Test
    public void testAddQuestionForFailureWhenQuestionTitleIsDuplicateInForm() throws Exception {
        QuestionForm questionForm = new QuestionForm();
        questionForm.getCurrentQuestion().setTitle("  " + TITLE + "    ");
        questionForm.setQuestions(asList(getQuestion("0", TITLE, "Number")));
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionnaireController.addQuestion(questionForm, requestContext);
        assertThat(questionForm.getQuestions().size(), is(1));
        assertThat(result, is(notNullValue()));
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.question.duplicate")));
    }

    @Test
    public void testDeleteSection() {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        String sectionName = "sectionName";
        questionGroup.setSectionName(sectionName);
        questionGroup.addCurrentSection();
        assertThat(questionGroup.getSections().size(), is(1));
        questionnaireController.deleteSection(questionGroup, sectionName);
        assertThat(questionGroup.getSections().size(), is(0));
    }

    @Test
    public void testAddSectionForSuccess() throws Exception {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setQuestionPool(new ArrayList<SectionQuestionDetail>(asList(getSectionQuestionDetail(1, "Q1"), getSectionQuestionDetail(2, "Q2"))));
        questionGroup.setSelectedQuestionIds(asList("1"));
        questionGroup.setTitle("title");
        questionGroup.setSectionName("sectionName");
        String result = questionnaireController.addSection(questionGroup, requestContext);
        assertThat(questionGroup.getSections().size(), is(1));
        assertThat(questionGroup.getSections().get(0).getName(), is("sectionName"));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), is(1));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getTitle(), is("Q1"));
        assertThat(result, is("success"));
    }

    @Test
    public void testAddSectionsSuccessWhenSectionNameIsNotProvided() {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setQuestionPool(new ArrayList<SectionQuestionDetail>(asList(getSectionQuestionDetail(1, "Q1"), getSectionQuestionDetail(2, "Q2"))));
        questionGroup.setSelectedQuestionIds(asList("2"));
        String result = questionnaireController.addSection(questionGroup, requestContext);
        assertThat(questionGroup.getSections().size(), is(1));
        assertThat(questionGroup.getSections().get(0).getName(), is("Misc"));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), is(1));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getTitle(), is("Q2"));
        assertThat(result, is("success"));
    }

    @Test
    public void testAddSectionForSuccessWhenQuestionTitleProvidedWithAllBlanks() throws Exception {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setQuestionPool(new ArrayList<SectionQuestionDetail>(asList(getSectionQuestionDetail(1, "Q1"), getSectionQuestionDetail(2, "Q2"))));
        questionGroup.setSelectedQuestionIds(asList("1", "2"));
        questionGroup.setSectionName("        ");
        String result = questionnaireController.addSection(questionGroup, requestContext);
        assertThat(questionGroup.getSections().size(), is(1));
        assertThat(questionGroup.getSections().get(0).getName(), is("Misc"));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), is(2));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getTitle(), is("Q1"));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(1).getTitle(), is("Q2"));
        assertThat(result, is("success"));
    }

    @Test
    public void testAddSectionWithoutQuestions() throws Exception {
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setTitle("title");
        questionGroup.setSectionName("sectionName");
        String result = questionnaireController.addSection(questionGroup, requestContext);
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.no.question.in.section")));
        assertThat(result, is("failure"));
    }

    @Test
    public void testRemoveQuestion() {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        List<SectionDetailForm> sections = new ArrayList<SectionDetailForm>();
        sections.add(getSectionSectionDetailForm("sectionName", new ArrayList<SectionQuestionDetail>(asList(getSectionQuestionDetail(1, "Q1"), getSectionQuestionDetail(2, "Q2")))));
        questionGroup.setSections(sections);

        assertThat(questionGroup.getSections().size(), CoreMatchers.is(1));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), CoreMatchers.is(2));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getTitle(), CoreMatchers.is("Q1"));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(1).getTitle(), CoreMatchers.is("Q2"));

        questionnaireController.deleteQuestion(questionGroup, "sectionName", "1");

        assertThat(questionGroup.getSections().size(), CoreMatchers.is(1));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), CoreMatchers.is(1));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getTitle(), CoreMatchers.is("Q2"));

        questionnaireController.deleteQuestion(questionGroup, "sectionName", "2");

        assertThat(questionGroup.getSections().size(), CoreMatchers.is(0));
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
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        doThrow(new ApplicationException("db.write.failure")).when(questionnaireServiceFacade).createQuestions(Matchers.<List<QuestionDetail>>anyObject());
        String result = questionnaireController.createQuestions(questionForm, requestContext);
        assertThat(result, is("failure"));
        verify(questionnaireServiceFacade).createQuestions(Matchers.<List<QuestionDetail>>anyObject());
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("db.write.failure")));
    }

    @Test
    public void testCreateQuestionSuccess() throws Exception {
        QuestionForm questionForm = getQuestionForm(TITLE, "Date");
        String result = questionnaireController.createQuestions(questionForm, requestContext);
        assertThat(result, is("success"));
        verify(questionnaireServiceFacade).createQuestions(anyListOf(QuestionDetail.class));
    }

    @Test
    public void testCreateQuestionGroupSuccess() throws Exception {
        QuestionGroupForm questionGroup = getQuestionGroupForm("   " + TITLE + " ", "Create.Client", "S1", "S2");
        String result = questionnaireController.defineQuestionGroup(questionGroup, requestContext);
        assertThat(result, is("success"));
        verify(questionnaireServiceFacade).createQuestionGroup(argThat(new QuestionGroupDetailMatcher(getQuestionGroupDetail(TITLE, "Create", "Client", "S1", "S2"))));
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
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionnaireController.defineQuestionGroup(questionGroupForm, requestContext);
        assertThat(result, is(notNullValue()));
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.questionGroup.title.empty")));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testCreateQuestionGroupFailureWhenSectionsNotPresent() throws Exception {
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE, "Create.Client");
        String result = questionnaireController.defineQuestionGroup(questionGroupForm, requestContext);
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.section.atLeastOne")));
    }

    @Test
    public void testCreateQuestionGroupForFailureWhenQuestionGroupAppliesToNotProvided() throws Exception {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE, DEFAULT_APPLIES_TO_OPTION, "Section");
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionnaireController.defineQuestionGroup(questionGroupForm, requestContext);
        assertThat(result, is(notNullValue()));
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.appliesTo.mandatory")));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testCreateQuestionGroupFailure() throws Exception {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE, "Create.Client", "S1", "S2");
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        doThrow(new ApplicationException("questionnaire.error.duplicate.question.found.in.section")).when(questionnaireServiceFacade).createQuestionGroup(Matchers.<QuestionGroupDetail>anyObject());
        String result = questionnaireController.defineQuestionGroup(questionGroupForm, requestContext);
        assertThat(result, is("failure"));
        verify(questionnaireServiceFacade).createQuestionGroup(Matchers.<QuestionGroupDetail>anyObject());
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.duplicate.question.found.in.section")));
    }

    @Test
    public void shouldGetAllQuestions() {
        List<QuestionDetail> questionDetailList = asList(getQuestionDetail(1, "title1", QuestionType.NUMERIC), getQuestionDetail(2, "title2", QuestionType.NUMERIC));
        when(questionnaireServiceFacade.getAllQuestions()).thenReturn(questionDetailList);
        String view = questionnaireController.getAllQuestions(model, httpServletRequest);
        assertThat(view, is("viewQuestions"));
        verify(questionnaireServiceFacade).getAllQuestions();
        verify(model).addAttribute(eq("questions"), eq(questionDetailList));
    }

    @Test
    public void shouldGetAllQuestionGroups() {
        List<QuestionGroupDetail> questionGroupDetails = asList(
                getQuestionGroupDetail(1, TITLE, "title1", "sectionName1"), getQuestionGroupDetail(1, TITLE, "title1", "sectionName1"));
        when(questionnaireServiceFacade.getAllQuestionGroups()).thenReturn(questionGroupDetails);
        String view = questionnaireController.getAllQuestionGroups(model, httpServletRequest);
        assertThat(view, is("viewQuestionGroups"));
        verify(questionnaireServiceFacade).getAllQuestionGroups();
        verify(model).addAttribute(eq("questionGroups"), argThat(new ListOfQuestionGroupDetailMatcher(questionGroupDetails)));
    }

    @Test
    public void shouldGetQuestionById() throws ApplicationException {
        int questionId = 1;
        QuestionDetail questionDetail = getQuestionDetail(questionId, TITLE, QuestionType.NUMERIC);
        when(questionnaireServiceFacade.getQuestionDetail(questionId)).thenReturn(questionDetail);
        when(httpServletRequest.getParameter("questionId")).thenReturn(Integer.toString(questionId));
        String view = questionnaireController.getQuestion(model, httpServletRequest);
        assertThat(view, is("viewQuestionDetail"));
        verify(questionnaireServiceFacade).getQuestionDetail(questionId);
        verify(httpServletRequest, times(1)).getParameter("questionId");
        verify(model).addAttribute(eq("questionDetail"), argThat(new QuestionMatcher("1", TITLE, "Number")));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testGetQuestionWhenNotPresentInDb() throws ApplicationException {
        int questionId = 1;
        when(questionnaireServiceFacade.getQuestionDetail(questionId)).thenThrow(new ApplicationException(QuestionnaireConstants.QUESTION_NOT_FOUND));
        when(httpServletRequest.getParameter("questionId")).thenReturn(Integer.toString(questionId));
        String view = questionnaireController.getQuestion(model, httpServletRequest);
        assertThat(view, is("viewQuestionDetail"));
        verify(questionnaireServiceFacade).getQuestionDetail(questionId);
        verify(httpServletRequest, times(1)).getParameter("questionId");
        verify(model).addAttribute("error_message_code", QuestionnaireConstants.QUESTION_NOT_FOUND);
    }

    @Test
    public void testGetQuestionWhenIdIsNull() throws ApplicationException {
        when(httpServletRequest.getParameter("questionId")).thenReturn(null);
        String view = questionnaireController.getQuestion(model, httpServletRequest);
        assertThat(view, is("viewQuestionDetail"));
        verify(httpServletRequest, times(1)).getParameter("questionId");
        verify(questionnaireServiceFacade, times(0)).getQuestionDetail(anyInt());
        verify(model).addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_ID);
    }

    @Test
    public void testGetQuestionWhenIdIsNotInteger() throws ApplicationException {
        when(httpServletRequest.getParameter("questionId")).thenReturn("1A");
        String view = questionnaireController.getQuestion(model, httpServletRequest);
        assertThat(view, is("viewQuestionDetail"));
        verify(httpServletRequest, times(1)).getParameter("questionId");
        verify(questionnaireServiceFacade, times(0)).getQuestionDetail(anyInt());
        verify(model).addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_ID);
    }

    @Test
    public void shouldGetQuestionGroupById() throws ApplicationException {
        int questionGroupId = 1;
        QuestionGroupDetail questionGroupDetail = getQuestionGroupDetail(questionGroupId, TITLE, "S1", "S2", "S3");
        when(questionnaireServiceFacade.getQuestionGroupDetail(questionGroupId)).thenReturn(questionGroupDetail);
        when(httpServletRequest.getParameter("questionGroupId")).thenReturn(Integer.toString(questionGroupId));
        String view = questionnaireController.getQuestionGroup(model, httpServletRequest);
        assertThat(view, is("viewQuestionGroupDetail"));
        verify(questionnaireServiceFacade).getQuestionGroupDetail(questionGroupId);
        verify(questionnaireServiceFacade, times(1)).getAllEventSources();
        verify(httpServletRequest, times(1)).getParameter("questionGroupId");
        verify(model).addAttribute(eq("questionGroupDetail"), argThat(new QuestionGroupDetailFormMatcher(new QuestionGroupForm(questionGroupDetail))));
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
    public void testGetQuestionGroupWhenNotPresentInDb() throws ApplicationException {
        int questionGroupId = 1;
        when(questionnaireServiceFacade.getQuestionGroupDetail(questionGroupId)).thenThrow(new ApplicationException(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        when(httpServletRequest.getParameter("questionGroupId")).thenReturn("1");
        String view = questionnaireController.getQuestionGroup(model, httpServletRequest);
        assertThat(view, is("viewQuestionGroupDetail"));
        verify(questionnaireServiceFacade).getQuestionGroupDetail(questionGroupId);
        verify(httpServletRequest, times(1)).getParameter("questionGroupId");
        verify(model).addAttribute("error_message_code", QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND);
    }

    @Test
    public void testGetQuestionGroupWhenIdIsNull() throws ApplicationException {
        when(httpServletRequest.getParameter("questionGroupId")).thenReturn(null);
        String view = questionnaireController.getQuestionGroup(model, httpServletRequest);
        assertThat(view, is("viewQuestionGroupDetail"));
        verify(httpServletRequest, times(1)).getParameter("questionGroupId");
        verify(questionnaireServiceFacade, times(0)).getQuestionGroupDetail(anyInt());
        verify(model).addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_GROUP_ID);
    }

    @Test
    public void testGetQuestionGroupWhenIdIsNotInteger() throws ApplicationException {
        when(httpServletRequest.getParameter("questionGroupId")).thenReturn("1A");
        String view = questionnaireController.getQuestionGroup(model, httpServletRequest);
        assertThat(view, is("viewQuestionGroupDetail"));
        verify(httpServletRequest, times(1)).getParameter("questionGroupId");
        verify(questionnaireServiceFacade, times(0)).getQuestionGroupDetail(anyInt());
        verify(model).addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_GROUP_ID);
    }

    @Test
    public void shouldGetAllQgEventSources() {
        when(questionnaireServiceFacade.getAllEventSources()).thenReturn(asList(new EventSource("Create", "Client", "Create Client"), new EventSource("View", "Client", "View Client")));
        Map<String, String> eventSources = questionnaireController.getAllQgEventSources();
        verify(questionnaireServiceFacade).getAllEventSources();
        assertThat(eventSources.get("Create.Client"), is("Create Client"));
        assertThat(eventSources.get("View.Client"), is("View Client"));
    }

    @Test
    public void shouldGetAllSectionQuestions() {
        when(questionnaireServiceFacade.getAllQuestions()).thenReturn(asList(getQuestionDetail(1, "Q1", QuestionType.NUMERIC), getQuestionDetail(2, "Q2", QuestionType.DATE)));
        List<SectionQuestionDetail> sectionQuestions = questionnaireController.getAllSectionQuestions();
        assertThat(sectionQuestions, is(notNullValue()));
        assertThat(sectionQuestions.size(), is(2));
        assertThat(sectionQuestions.get(0).getQuestionId(), is(1));
        assertThat(sectionQuestions.get(0).getTitle(), is("Q1"));
        assertThat(sectionQuestions.get(1).getQuestionId(), is(2));
        assertThat(sectionQuestions.get(1).getTitle(), is("Q2"));
        verify(questionnaireServiceFacade).getAllQuestions();
    }

    @Test
    public void shouldGetAllSectionQuestionsEmpty() {
        when(questionnaireServiceFacade.getAllQuestions()).thenReturn(null);
        List<SectionQuestionDetail> sectionQuestions = questionnaireController.getAllSectionQuestions();
        assertThat(sectionQuestions, is(notNullValue()));
        assertThat(sectionQuestions.size(), is(0));
        verify(questionnaireServiceFacade).getAllQuestions();
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
            return (equalsIgnoreCase(type, question.getType())
                    && equalsIgnoreCase(id, question.getId())
                    && equalsIgnoreCase(title, question.getTitle()));
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
            if (equalsIgnoreCase(this.questionGroupForm.getTitle(), questionGroupForm.getTitle())
                    && this.questionGroupForm.getSections().size() == questionGroupForm.getSections().size()) {
                for (SectionDetailForm sectionDetailForm : this.questionGroupForm.getSections()) {
                    assertThat(questionGroupForm.getSections(), hasItem(new SectionDetailFormMatcher(sectionDetailForm)));
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
                    assertThat(questionGroupDetails, hasItem(new QuestionGroupDetailMatcher(questionGroupDetail)));
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
