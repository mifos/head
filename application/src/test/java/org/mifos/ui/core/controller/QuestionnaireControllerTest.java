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
import org.mifos.platform.questionnaire.contract.EventSource;
import org.mifos.platform.questionnaire.contract.QuestionGroupDetail;
import org.mifos.platform.questionnaire.contract.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.contract.SectionDefinition;
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
        QuestionGroup questionGroup = new QuestionGroup();
        String sectionName = "sectionName";
        questionGroup.setSectionName(sectionName);
        questionGroup.addCurrentSection();
        assertThat(questionGroup.getSections().size(), is(1));
        questionnaireController.deleteSection(questionGroup, sectionName);
        assertThat(questionGroup.getSections().size(), is(0));
    }

    @Test
    public void testAddSectionForSuccess() throws Exception {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setQuestionPool(new ArrayList(asList(getQuestion("1", "Q1", "Free text"), getQuestion("2", "Q2", "Date"))));
        questionGroup.setSelectedQuestionIds(asList("1"));
        questionGroup.setTitle("title");
        questionGroup.setSectionName("sectionName");
        String result = questionnaireController.addSection(questionGroup, requestContext);
        assertThat(questionGroup.getSections().size(), is(1));
        assertThat(questionGroup.getSections().get(0).getName(), is("sectionName"));
        assertThat(questionGroup.getSections().get(0).getQuestions().size(), is(1));
        assertThat(questionGroup.getSections().get(0).getQuestions().get(0).getTitle(), is("Q1"));
        assertThat(result, is("success"));
    }

    @Test
    public void testAddSectionsSuccessWhenSectionNameIsNotProvided() {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setQuestionPool(new ArrayList(asList(getQuestion("1", "Q1", "Free text"), getQuestion("2", "Q2", "Date"))));
        questionGroup.setSelectedQuestionIds(asList("2"));
        String result = questionnaireController.addSection(questionGroup, requestContext);
        assertThat(questionGroup.getSections().size(), is(1));
        assertThat(questionGroup.getSections().get(0).getName(), is("Misc"));
        assertThat(questionGroup.getSections().get(0).getQuestions().size(), is(1));
        assertThat(questionGroup.getSections().get(0).getQuestions().get(0).getTitle(), is("Q2"));
        assertThat(result, is("success"));
    }

    @Test
    public void testAddSectionForSuccessWhenQuestionTitleProvidedWithAllBlanks() throws Exception {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setQuestionPool(new ArrayList(asList(getQuestion("1", "Q1", "Free text"), getQuestion("2", "Q2", "Date"))));
        questionGroup.setSelectedQuestionIds(asList("1", "2"));
        questionGroup.setSectionName("        ");
        String result = questionnaireController.addSection(questionGroup, requestContext);
        assertThat(questionGroup.getSections().size(), is(1));
        assertThat(questionGroup.getSections().get(0).getName(), is("Misc"));
        assertThat(questionGroup.getSections().get(0).getQuestions().size(), is(2));
        assertThat(questionGroup.getSections().get(0).getQuestions().get(0).getTitle(), is("Q1"));
        assertThat(questionGroup.getSections().get(0).getQuestions().get(1).getTitle(), is("Q2"));
        assertThat(result, is("success"));
    }

    @Test
    public void testAddSectionWithoutQuestions() throws Exception {
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setTitle("title");
        questionGroup.setSectionName("sectionName");
        String result = questionnaireController.addSection(questionGroup, requestContext);
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.no.question.in.section")));
        assertThat(result, is("failure"));
    }

    @Test
    public void testRemoveQuestion() {
        QuestionGroup questionGroup = new QuestionGroup();
        ArrayList<SectionForm> sections = new ArrayList<SectionForm>();
        sections.add(getSection("sectionName", new ArrayList(asList(getQuestion("1", "Q1", "Free Text"), getQuestion("2", "Q2", "Date")))));
        questionGroup.setSections(sections);

        assertThat(questionGroup.getSections().size(), CoreMatchers.is(1));
        assertThat(questionGroup.getSections().get(0).getQuestions().size(), CoreMatchers.is(2));
        assertThat(questionGroup.getSections().get(0).getQuestions().get(0).getTitle(), CoreMatchers.is("Q1"));
        assertThat(questionGroup.getSections().get(0).getQuestions().get(1).getTitle(), CoreMatchers.is("Q2"));

        questionnaireController.deleteQuestion(questionGroup, "sectionName", "1");

        assertThat(questionGroup.getSections().size(), CoreMatchers.is(1));
        assertThat(questionGroup.getSections().get(0).getQuestions().size(), CoreMatchers.is(1));
        assertThat(questionGroup.getSections().get(0).getQuestions().get(0).getTitle(), CoreMatchers.is("Q2"));

        questionnaireController.deleteQuestion(questionGroup, "sectionName", "2");

        assertThat(questionGroup.getSections().size(), CoreMatchers.is(0));
    }

    @Test
    public void testCreateQuestionFailure() throws Exception {
        QuestionForm questionForm = getQuestionForm(TITLE, "Numeric");
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        doThrow(new ApplicationException("db.write.failure")).when(questionnaireServiceFacade).createQuestions(Matchers.<List<Question>>anyObject());
        String result = questionnaireController.createQuestions(questionForm, requestContext);
        assertThat(result, is("failure"));
        verify(questionnaireServiceFacade).createQuestions(Matchers.<List<Question>>anyObject());
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("db.write.failure")));
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
        QuestionGroup questionGroup = getQuestionGroup("   " + TITLE + " ", "Create.Client", "S1", "S2");
        String result = questionnaireController.defineQuestionGroup(questionGroup, requestContext);
        assertThat(result, is("success"));
        verify(questionnaireServiceFacade).createQuestionGroup(argThat(new QuestionGroupMatcher(getQuestionGroup(TITLE, "Create.Client", "S1", "S2"))));
    }

    @Test
    public void testCreateQuestionGroupForFailureWhenQuestionGroupTitleNotProvided() throws Exception {
        QuestionGroup questionGroup = getQuestionGroup(null, "Create.Client", "Section");
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionnaireController.defineQuestionGroup(questionGroup, requestContext);
        assertThat(result, is(notNullValue()));
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.questionGroup.title.empty")));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testCreateQuestionGroupFailureWhenSectionsNotPresent() throws Exception {
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        QuestionGroup questionGroup = getQuestionGroup(TITLE, "Create.Client");
        String result = questionnaireController.defineQuestionGroup(questionGroup, requestContext);
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.section.atLeastOne")));
    }

    @Test
    public void testCreateQuestionGroupForFailureWhenQuestionGroupAppliesToNotProvided() throws Exception {
        QuestionGroup questionGroup = getQuestionGroup(TITLE, "--select one--", "Section");
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionnaireController.defineQuestionGroup(questionGroup, requestContext);
        assertThat(result, is(notNullValue()));
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.appliesTo.mandatory")));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testCreateQuestionGroupFailure() throws Exception {
        QuestionGroup questionGroup = getQuestionGroup(TITLE, "Create.Client", "S1", "S2");
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        doThrow(new ApplicationException("questionnaire.error.duplicate.question.found.in.section")).when(questionnaireServiceFacade).createQuestionGroup(Matchers.<QuestionGroup>anyObject());
        String result = questionnaireController.defineQuestionGroup(questionGroup, requestContext);
        assertThat(result, is("failure"));
        verify(questionnaireServiceFacade).createQuestionGroup(Matchers.<QuestionGroup>anyObject());
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.duplicate.question.found.in.section")));
    }

    @Test
    public void shouldGetAllQuestions() {
        when(questionnaireServiceFacade.getAllQuestions()).thenReturn(asList(getQuestion("1", "title1", "Number"), getQuestion("2", "title2", "Number")));
        String view = questionnaireController.getAllQuestions(model, httpServletRequest);
        assertThat(view, is("viewQuestions"));
        verify(questionnaireServiceFacade).getAllQuestions();
        verify(model).addAttribute(eq("questions"), argThat(new ListOfQuestionsMatcher(asList(getQuestion("1", "title1", "Number"), getQuestion("2", "title2", "Number")))));
    }

    @Test
    public void shouldGetAllQuestionGroups() {
        when(questionnaireServiceFacade.getAllQuestionGroups()).thenReturn(asList(
                getQuestionGroup(1, "title1", asList(getSection("sectionName1",
                        asList(getQuestion("1", "Q1", "Free Text"), getQuestion("2", "Q2", "Date"))))),
                getQuestionGroup(2, "title2", asList(getSection("sectionName2",
                        asList(getQuestion("3", "Q3", "Free Text"), getQuestion("4", "Q4", "Date"), getQuestion("5", "Q5", "Date")))))));
        String view = questionnaireController.getAllQuestionGroups(model, httpServletRequest);
        assertThat(view, is("viewQuestionGroups"));
        verify(questionnaireServiceFacade).getAllQuestionGroups();
        verify(model).addAttribute(eq("questionGroups"), argThat(new ListOfQuestionGroupMatcher(asList(
                getQuestionGroup(1, "title1", asList(getSection("sectionName1",
                        asList(getQuestion("1", "Q1", "Free Text"), getQuestion("2", "Q2", "Date"))))),
                getQuestionGroup(2, "title2", asList(getSection("sectionName2",
                        asList(getQuestion("3", "Q3", "Free Text"), getQuestion("4", "Q4", "Date"), getQuestion("5", "Q5", "Date")))))))));
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
        QuestionGroupDetail questionGroupDetail = getQuestionGroupDetail(questionGroupId, "S1", "S2", "S3");
        when(questionnaireServiceFacade.getQuestionGroupDetail(questionGroupId)).thenReturn(questionGroupDetail);
        when(httpServletRequest.getParameter("questionGroupId")).thenReturn(Integer.toString(questionGroupId));
        String view = questionnaireController.getQuestionGroup(model, httpServletRequest);
        assertThat(view, is("viewQuestionGroupDetail"));
        verify(questionnaireServiceFacade).getQuestionGroupDetail(questionGroupId);
        verify(questionnaireServiceFacade, times(1)).getAllEventSources();
        verify(httpServletRequest, times(1)).getParameter("questionGroupId");
        verify(model).addAttribute(eq("questionGroupDetail"), argThat(new QuestionGroupDetailFormMatcher(new QuestionGroupDetailForm(questionGroupDetail))));
    }

    private QuestionGroupDetail getQuestionGroupDetail(int questionGroupId, String... sectionNames) {
        List<SectionDefinition> sectionDefinitions = new ArrayList<SectionDefinition>();
        for (String sectionName : sectionNames) {
            SectionDefinition sectionDefinition = new SectionDefinition();
            sectionDefinition.setName(sectionName);
            sectionDefinitions.add(sectionDefinition);
        }
        return new QuestionGroupDetail(questionGroupId, TITLE, sectionDefinitions);
    }

    private QuestionGroup getQuestionGroup(int questionGroupId, String title, String... sectionNames) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setId(Integer.toString(questionGroupId));
        questionGroup.setTitle(title);
        for (String sectionName : sectionNames) {
            questionGroup.setSectionName(sectionName);
            questionGroup.addCurrentSection();
        }
        return questionGroup;
    }

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

    private QuestionGroup getQuestionGroup(int questionGroupId, String title, List<SectionForm> sections) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setId(Integer.toString(questionGroupId));
        questionGroup.setTitle(title);
        questionGroup.setSections(sections);
        return questionGroup;
    }


    private QuestionForm getQuestionForm(String title, String type) {
        QuestionForm questionForm = new QuestionForm();
        questionForm.getCurrentQuestion().setTitle(title);
        questionForm.getCurrentQuestion().setType(type);
        return questionForm;
    }

    private QuestionGroup getQuestionGroup(String title, String eventSourceId, String... sectionNames) {
        QuestionGroup questionGroup = new QuestionGroup();
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

    private SectionForm getSection(String sectionName, List<Question> questions) {
        SectionForm section = new SectionForm();
        section.setName(sectionName);
        section.setQuestions(questions);
        return section;
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

    private class ListOfQuestionsMatcher extends TypeSafeMatcher<List<Question>> {
        private List<Question> questions;

        public ListOfQuestionsMatcher(List<Question> questions) {
            this.questions = questions;
        }

        @Override
        public boolean matchesSafely(List<Question> questions) {
            if (questions.size() == this.questions.size()) {
                for (Question question : this.questions) {
                    assertThat(questions, hasItem(new QuestionMatcher(question)));
                }
            } else {
                return false;
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Question lists does not match");
        }
    }

    private class QuestionMatcher extends TypeSafeMatcher<Question> {
        private Question question;

        public QuestionMatcher(Question question) {
            this.question = question;
        }

        @Override
        public boolean matchesSafely(Question question) {
            return (equalsIgnoreCase(this.question.getType(), question.getType())
                    && equalsIgnoreCase(this.question.getId(), question.getId())
                    && equalsIgnoreCase(this.question.getTitle(), question.getTitle()));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Question does not match");
        }
    }

    private class QuestionGroupMatcher extends TypeSafeMatcher<QuestionGroup> {
        private QuestionGroup questionGroup;

        public QuestionGroupMatcher(QuestionGroup questionGroup) {
            this.questionGroup = questionGroup;
        }

        @Override
        public boolean matchesSafely(QuestionGroup questionGroup) {
            if (equalsIgnoreCase(this.questionGroup.getTitle(), questionGroup.getTitle())
                    && equalsIgnoreCase(this.questionGroup.getId(), questionGroup.getId())
                    && this.questionGroup.getSections().size() == questionGroup.getSections().size()
                    ) {
                for (SectionForm sectionForm : this.questionGroup.getSections()) {
                    assertThat(questionGroup.getSections(), hasItem(new SectionFormMatcher(sectionForm)));
                }
            } else {
                return false;
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("QuestionGroup do not match");
        }
    }

    private class QuestionGroupDetailFormMatcher extends TypeSafeMatcher<QuestionGroupDetailForm> {
        private QuestionGroupDetailForm questionGroupDetailForm;

        public QuestionGroupDetailFormMatcher(QuestionGroupDetailForm questionGroupDetailForm) {
            this.questionGroupDetailForm = questionGroupDetailForm;
        }

        @Override
        public boolean matchesSafely(QuestionGroupDetailForm questionGroupDetailForm) {
            if (equalsIgnoreCase(this.questionGroupDetailForm.getTitle(), questionGroupDetailForm.getTitle())
                    && this.questionGroupDetailForm.getSections().size() == questionGroupDetailForm.getSections().size()) {
                for (SectionDetailForm sectionDetailForm : this.questionGroupDetailForm.getSections()) {
                    assertThat(questionGroupDetailForm.getSections(), hasItem(new SectionDetailFormMatcher(sectionDetailForm)));
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

    class SectionFormMatcher extends TypeSafeMatcher<SectionForm> {
        private SectionForm sectionForm;

        public SectionFormMatcher(SectionForm sectionForm) {
            this.sectionForm = sectionForm;
        }

        @Override
        public boolean matchesSafely(SectionForm sectionForm) {
            if (StringUtils.equals(this.sectionForm.getName(), sectionForm.getName()) &&
                    this.sectionForm.getQuestions().size() == sectionForm.getQuestions().size()) {
                for (Question question : this.sectionForm.getQuestions()) {
                    assertThat(sectionForm.getQuestions(), hasItem(new QuestionMatcher(question)));
                }
            } else {
                return false;
            }
            return true;

        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Sections do not match");
        }
    }

    private class ListOfQuestionGroupMatcher extends TypeSafeMatcher<List<QuestionGroup>> {
        private List<QuestionGroup> questionGroups;

        public ListOfQuestionGroupMatcher(List<QuestionGroup> questionGroups) {
            this.questionGroups = questionGroups;
        }

        @Override
        public boolean matchesSafely(List<QuestionGroup> questionGroups) {
            if (this.questionGroups.size() == questionGroups.size()) {
                for (QuestionGroup questionGroup : this.questionGroups) {
                    assertThat(questionGroups, hasItem(new QuestionGroupMatcher(questionGroup)));
                }
            } else {
                return false;
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("List of question groups do not match");
        }
    }
}
