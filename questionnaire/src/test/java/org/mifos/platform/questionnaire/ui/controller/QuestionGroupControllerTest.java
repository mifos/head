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

package org.mifos.platform.questionnaire.ui.controller;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.validations.ValidationException;
import org.mifos.platform.matchers.MessageMatcher;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.exceptions.BadNumericResponseException;
import org.mifos.platform.questionnaire.exceptions.MandatoryAnswerNotFoundException;
import org.mifos.platform.questionnaire.matchers.QuestionGroupDetailFormMatcher;
import org.mifos.platform.questionnaire.matchers.QuestionGroupDetailMatcher;
import org.mifos.platform.questionnaire.matchers.QuestionGroupDetailsMatcher;
import org.mifos.platform.questionnaire.matchers.QuestionGroupsGroupByEventSourceMatcher;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.ui.model.QuestionGroupForm;
import org.mifos.platform.questionnaire.ui.model.SectionDetailForm;
import org.mifos.platform.validation.MifosBeanValidator;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.binding.message.MessageContext;
import org.springframework.ui.ModelMap;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.GENERIC_VALIDATION;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD")
public class QuestionGroupControllerTest {

    private static final String TITLE = "Title";

    @Mock
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    private QuestionGroupController questionGroupController;

    @Mock
    private RequestContext requestContext;

    @Mock
    private MessageContext messageContext;

    @Mock
    private ModelMap model;

    @Mock
    private HttpServletRequest httpServletRequest;

    MifosBeanValidator validator = new MifosBeanValidator();
    LocalValidatorFactoryBean targetValidator = new LocalValidatorFactoryBean();

    @Before
    public void setUp() {
        questionGroupController = new QuestionGroupController(questionnaireServiceFacade);
        targetValidator.afterPropertiesSet();
        validator.setTargetValidator(targetValidator);
    }

    @Test
    public void testDeleteSection() {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        String sectionName = "sectionName";
        questionGroup.setSectionName(sectionName);
        questionGroup.addCurrentSection();
        assertThat(questionGroup.getSections().size(), Is.is(1));
        questionGroupController.deleteSection(questionGroup, sectionName);
        assertThat(questionGroup.getSections().size(), Is.is(0));
    }

    @Test
    public void testAddSectionForSuccess() throws Exception {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setQuestionPool(new ArrayList<SectionQuestionDetail>(asList(getSectionQuestionDetail(1, "Q1"), getSectionQuestionDetail(2, "Q2"))));
        questionGroup.setSelectedQuestionIds(asList("1"));
        questionGroup.setTitle("title");
        questionGroup.setSectionName("sectionName");
        String result = questionGroupController.addSection(questionGroup, requestContext);
        assertThat(questionGroup.getSections().size(), Is.is(1));
        assertThat(questionGroup.getSections().get(0).getName(), Is.is("sectionName"));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), Is.is(1));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getText(), Is.is("Q1"));
        assertThat(result, Is.is("success"));
    }

    @Test
    public void testAddSectionsSuccessWhenSectionNameIsNotProvided() {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setQuestionPool(new ArrayList<SectionQuestionDetail>(asList(getSectionQuestionDetail(1, "Q1"), getSectionQuestionDetail(2, "Q2"))));
        questionGroup.setSelectedQuestionIds(asList("2"));
        String result = questionGroupController.addSection(questionGroup, requestContext);
        assertThat(questionGroup.getSections().size(), Is.is(1));
        assertThat(questionGroup.getSections().get(0).getName(), Is.is("Misc"));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), Is.is(1));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getText(), Is.is("Q2"));
        assertThat(result, Is.is("success"));
    }

    @Test
    public void testAddSectionForSuccessWhenQuestionTitleProvidedWithAllBlanks() throws Exception {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setQuestionPool(new ArrayList<SectionQuestionDetail>(asList(getSectionQuestionDetail(1, "Q1"), getSectionQuestionDetail(2, "Q2"))));
        questionGroup.setSelectedQuestionIds(asList("1", "2"));
        questionGroup.setSectionName("        ");
        String result = questionGroupController.addSection(questionGroup, requestContext);
        assertThat(questionGroup.getSections().size(), Is.is(1));
        assertThat(questionGroup.getSections().get(0).getName(), Is.is("Misc"));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), Is.is(2));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getText(), Is.is("Q1"));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(1).getText(), Is.is("Q2"));
        assertThat(result, Is.is("success"));
    }

    @Test
    public void testAddSectionWithoutQuestions() throws Exception {
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setTitle("title");
        questionGroup.setSectionName("sectionName");
        String result = questionGroupController.addSection(questionGroup, requestContext);
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.no.question.in.section")));
        assertThat(result, Is.is("failure"));
    }

    @Test
    public void testRemoveQuestionFromSection() {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        List<SectionDetailForm> sections = new ArrayList<SectionDetailForm>();
        sections.add(getSectionSectionDetailForm("sectionName", new ArrayList<SectionQuestionDetail>(asList(getSectionQuestionDetail(1, "Q1"), getSectionQuestionDetail(2, "Q2")))));
        questionGroup.setSections(sections);

        assertThat(questionGroup.getSections().size(), CoreMatchers.is(1));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), CoreMatchers.is(2));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getText(), CoreMatchers.is("Q1"));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(1).getText(), CoreMatchers.is("Q2"));

        questionGroupController.deleteQuestion(questionGroup, "sectionName", "1");

        assertThat(questionGroup.getSections().size(), CoreMatchers.is(1));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), CoreMatchers.is(1));
        assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getText(), CoreMatchers.is("Q2"));

        questionGroupController.deleteQuestion(questionGroup, "sectionName", "2");

        assertThat(questionGroup.getSections().size(), CoreMatchers.is(0));
    }

    @Test
    public void testCreateQuestionGroupSuccess() throws Exception {
        QuestionGroupForm questionGroup = getQuestionGroupForm("   " + TITLE + " ", "Create.Client", "S1", "S2");
        String result = questionGroupController.defineQuestionGroup(questionGroup, requestContext, true);
        assertThat(result, Is.is("success"));
        QuestionGroupDetail questionGroupDetail = getQuestionGroupDetail(TITLE, "Create", "Client", "S1", "S2");
        questionGroupDetail.setActive(true);
        verify(questionnaireServiceFacade).createActiveQuestionGroup(argThat(new QuestionGroupDetailMatcher(questionGroupDetail)));
    }

    @Test
    public void testEditQuestionGroupSuccess() throws Exception {
        QuestionGroupForm questionGroup = getQuestionGroupForm("   " + TITLE + " ", "Create.Client", "S1", "S2");
        String result = questionGroupController.defineQuestionGroup(questionGroup, requestContext, false);
        assertThat(result, Is.is("success"));
        QuestionGroupDetail questionGroupDetail = getQuestionGroupDetail(TITLE, "Create", "Client", "S1", "S2");
        questionGroupDetail.setActive(false);
        verify(questionnaireServiceFacade).createQuestionGroup(argThat(new QuestionGroupDetailMatcher(questionGroupDetail)));
    }

    private QuestionGroupDetail getQuestionGroupDetail(String title, String event, String source, String... sectionNames) {
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail();
        questionGroupDetail.setTitle(title);
        questionGroupDetail.setEventSources(Arrays.asList(new EventSourceDto(event, source, null)));
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
        String result = questionGroupController.defineQuestionGroup(questionGroupForm, requestContext, true);
        assertThat(result, Is.is(notNullValue()));
        assertThat(result, Is.is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.questionGroup.title.empty")));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testCreateQuestionGroupFailureWhenSectionsNotPresent() throws Exception {
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE, "Create.Client");
        String result = questionGroupController.defineQuestionGroup(questionGroupForm, requestContext, true);
        assertThat(result, Is.is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.section.atLeastOne")));
    }

    @Test
    public void testCreateQuestionGroupForFailureWhenQuestionGroupAppliesToNotProvided() throws Exception {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE, QuestionnaireConstants.DEFAULT_APPLIES_TO_OPTION, "Section");
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionGroupController.defineQuestionGroup(questionGroupForm, requestContext, true);
        assertThat(result, Is.is(notNullValue()));
        assertThat(result, Is.is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.appliesTo.mandatory")));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testCreateQuestionGroupFailure() throws Exception {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE, "Create.Client", "S1", "S2");
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        doThrow(new SystemException("questionnaire.error.duplicate.question.found.in.section")).when(questionnaireServiceFacade).createActiveQuestionGroup(Matchers.<QuestionGroupDetail>anyObject());
        String result = questionGroupController.defineQuestionGroup(questionGroupForm, requestContext, true);
        assertThat(result, Is.is("failure"));
        verify(questionnaireServiceFacade).createActiveQuestionGroup(Matchers.<QuestionGroupDetail>anyObject());
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.duplicate.question.found.in.section")));
    }

    //TODO Question Groups added to testing data should have different titles, otherwise QuestionGroupSectionMatcher will throw exceptions. Matchers shouldn't throw exception when matching fail but return 'false'
    @Test
    public void shouldGetAllQuestionGroups() {
        QuestionGroupDetail questionGroupDetail1 = getQuestionGroupDetail(1, TITLE + "1", "View", "Loan", true, true, "title1", "sectionName1");
        QuestionGroupDetail questionGroupDetail2 = getQuestionGroupDetail(2, TITLE + "2", "View", "Loan", true, true, "title2", "sectionName2");
        QuestionGroupDetail questionGroupDetail3 = getQuestionGroupDetail(3, TITLE + "3", "Create", "Loan", true, true, "title3", "sectionName3");
        List<QuestionGroupDetail> questionGroupDetails = asList(questionGroupDetail1, questionGroupDetail2, questionGroupDetail3);
        Map <String,List <QuestionGroupDetail>> questionGroupsCategoriesSplit = new HashMap<String, List<QuestionGroupDetail>>();
        questionGroupsCategoriesSplit.put("View Loan", asList(questionGroupDetail1, questionGroupDetail2));
        questionGroupsCategoriesSplit.put("Create Loan", asList(questionGroupDetail3));
        when(questionnaireServiceFacade.getAllQuestionGroups()).thenReturn(questionGroupDetails);
        when(questionnaireServiceFacade.getAllEventSources()).thenReturn(asList(questionGroupDetail2.getEventSources().get(0), questionGroupDetail3.getEventSources().get(0)));
        String view = questionGroupController.getAllQuestionGroups(model, httpServletRequest);
        assertThat(view, Is.is("viewQuestionGroups"));
        verify(questionnaireServiceFacade).getAllQuestionGroups();
        verify(questionnaireServiceFacade).getAllEventSources();
        verify(model).addAttribute(Matchers.eq("questionGroups"), argThat(new QuestionGroupsGroupByEventSourceMatcher(questionGroupsCategoriesSplit)));
    }

    @Test
    public void shouldGetQuestionGroupById() throws SystemException {
        int questionGroupId = 1;
        QuestionGroupDetail questionGroupDetail = getQuestionGroupDetail(questionGroupId, TITLE, "S1", "S2", "S3");
        when(questionnaireServiceFacade.getQuestionGroupDetail(questionGroupId)).thenReturn(questionGroupDetail);
        when(httpServletRequest.getParameter("questionGroupId")).thenReturn(Integer.toString(questionGroupId));
        String view = questionGroupController.getQuestionGroup(model, httpServletRequest);
        assertThat(view, Is.is("viewQuestionGroupDetail"));
        verify(questionnaireServiceFacade).getQuestionGroupDetail(questionGroupId);
        verify(questionnaireServiceFacade, times(1)).getAllEventSources();
        verify(httpServletRequest, times(1)).getParameter("questionGroupId");
        verify(model).addAttribute(Matchers.eq("questionGroupDetail"), argThat(new QuestionGroupDetailFormMatcher(new QuestionGroupForm(questionGroupDetail))));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testGetQuestionGroupWhenNotPresentInDb() throws SystemException {
        int questionGroupId = 1;
        when(questionnaireServiceFacade.getQuestionGroupDetail(questionGroupId)).thenThrow(new SystemException(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        when(httpServletRequest.getParameter("questionGroupId")).thenReturn("1");
        String view = questionGroupController.getQuestionGroup(model, httpServletRequest);
        assertThat(view, Is.is("viewQuestionGroupDetail"));
        verify(questionnaireServiceFacade).getQuestionGroupDetail(questionGroupId);
        verify(httpServletRequest, times(1)).getParameter("questionGroupId");
        verify(model).addAttribute("error_message_code", QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND);
    }

    @Test
    public void testGetQuestionGroupWhenIdIsNull() throws SystemException {
        when(httpServletRequest.getParameter("questionGroupId")).thenReturn(null);
        String view = questionGroupController.getQuestionGroup(model, httpServletRequest);
        assertThat(view, Is.is("viewQuestionGroupDetail"));
        verify(httpServletRequest, times(1)).getParameter("questionGroupId");
        verify(questionnaireServiceFacade, times(0)).getQuestionGroupDetail(Matchers.anyInt());
        verify(model).addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_GROUP_ID);
    }

    @Test
    public void testGetQuestionGroupWhenIdIsNotInteger() throws SystemException {
        when(httpServletRequest.getParameter("questionGroupId")).thenReturn("1A");
        String view = questionGroupController.getQuestionGroup(model, httpServletRequest);
        assertThat(view, Is.is("viewQuestionGroupDetail"));
        verify(httpServletRequest, times(1)).getParameter("questionGroupId");
        verify(questionnaireServiceFacade, times(0)).getQuestionGroupDetail(Matchers.anyInt());
        verify(model).addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_GROUP_ID);
    }

    @Test
    public void shouldGetAllQgEventSources() {
        when(questionnaireServiceFacade.getAllEventSources()).thenReturn(asList(new EventSourceDto("Create", "Client", "Create Client"), new EventSourceDto("View", "Client", "View Client")));
        Map<String, String> eventSources = questionGroupController.getAllQgEventSources();
        verify(questionnaireServiceFacade).getAllEventSources();
        assertThat(eventSources.get("Create.Client"), Is.is("Create Client"));
        assertThat(eventSources.get("View.Client"), Is.is("View Client"));
    }

    @Test
    public void shouldGetAllSectionQuestions() {
        when(questionnaireServiceFacade.getAllActiveQuestions()).thenReturn(asList(getQuestionDetail(1, "Q1", QuestionType.NUMERIC), getQuestionDetail(2, "Q2", QuestionType.DATE)));
        List<SectionQuestionDetail> sectionQuestions = questionGroupController.getAllSectionQuestions();
        assertThat(sectionQuestions, Is.is(notNullValue()));
        assertThat(sectionQuestions.size(), Is.is(2));
        assertThat(sectionQuestions.get(0).getQuestionId(), Is.is(1));
        assertThat(sectionQuestions.get(0).getText(), Is.is("Q1"));
        assertThat(sectionQuestions.get(1).getQuestionId(), Is.is(2));
        assertThat(sectionQuestions.get(1).getText(), Is.is("Q2"));
        verify(questionnaireServiceFacade).getAllActiveQuestions();
    }

    @Test
    public void shouldGetAllSectionQuestionsWithoutExcludedQuestions() {
        List<Integer> excludedQuestions = asList(3);
        List<QuestionDetail> questionDetails = asList(getQuestionDetail(1, "Q1", QuestionType.NUMERIC), getQuestionDetail(2, "Q2", QuestionType.DATE));
        when(questionnaireServiceFacade.getAllActiveQuestions(excludedQuestions)).thenReturn(questionDetails);
        QuestionGroupForm questionGroupForm = getQuestionGroupFormWithOneSectionQuestion("QG1", 3);
        List<SectionQuestionDetail> sectionQuestions = questionGroupController.getAllSectionQuestions(questionGroupForm);
        assertThat(sectionQuestions, Is.is(notNullValue()));
        assertThat(sectionQuestions.size(), Is.is(2));
        assertThat(sectionQuestions.get(0).getQuestionId(), Is.is(1));
        assertThat(sectionQuestions.get(0).getText(), Is.is("Q1"));
        assertThat(sectionQuestions.get(1).getQuestionId(), Is.is(2));
        assertThat(sectionQuestions.get(1).getText(), Is.is("Q2"));
        verify(questionnaireServiceFacade).getAllActiveQuestions(excludedQuestions);
    }

    private QuestionGroupForm getQuestionGroupFormWithOneSectionQuestion(String qgTitle, int questionId) {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(qgTitle, "Create.Client");
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName("Sec1");
        sectionDetail.setQuestionDetails(asList(new SectionQuestionDetail(getQuestionDetail(questionId, "Q3", QuestionType.FREETEXT), true)));
        questionGroupForm.getQuestionGroupDetail().getSectionDetails().add(sectionDetail);
        return questionGroupForm;
    }

    @Test
    public void shouldGetAllSectionQuestionsEmpty() {
        when(questionnaireServiceFacade.getAllActiveQuestions()).thenReturn(null);
        List<SectionQuestionDetail> sectionQuestions = questionGroupController.getAllSectionQuestions();
        assertThat(sectionQuestions, Is.is(notNullValue()));
        assertThat(sectionQuestions.size(), Is.is(0));
        verify(questionnaireServiceFacade).getAllActiveQuestions();
    }

    @Test
    public void testSaveQuestionnaireSuccess() {
        String result = questionGroupController.saveQuestionnaire(
                getQuestionGroupDetails(), 1, requestContext);
        verify(questionnaireServiceFacade, times(1)).saveResponses(argThat(new QuestionGroupDetailsMatcher(
                new QuestionGroupDetails(1, 2, 3, asList(
                        getQuestionGroupDetail(TITLE, "View", "Client", "S1", "S3"))))));
        assertThat(result, is("success"));
    }

    @Test
    public void testSaveQuestionnaireFailure() {
        ValidationException validationException = new ValidationException(GENERIC_VALIDATION);
        validationException.addChildException(new MandatoryAnswerNotFoundException("q1"));
        doThrow(validationException).when(questionnaireServiceFacade).saveResponses(Mockito.<QuestionGroupDetails>any());
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionGroupController.saveQuestionnaire(getQuestionGroupDetails(), 1, requestContext);
        assertThat(result, is("failure"));
        verify(requestContext, times(1)).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.noresponse")));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testSaveQuestionnaireFailureForNumericResponseWithoutBounds() {
        ValidationException validationException = new ValidationException(GENERIC_VALIDATION);
        validationException.addChildException(new BadNumericResponseException("q1", null, null));
        doThrow(validationException).when(questionnaireServiceFacade).saveResponses(Mockito.<QuestionGroupDetails>any());
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionGroupController.saveQuestionnaire(getQuestionGroupDetails(), 1, requestContext);
        assertThat(result, is("failure"));
        verify(requestContext, times(1)).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.invalid.numeric.response")));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testSaveQuestionnaireFailureForNumericResponseNotWithinBounds() {
        ValidationException validationException = new ValidationException(GENERIC_VALIDATION);
        validationException.addChildException(new BadNumericResponseException("q1", 10, 100));
        doThrow(validationException).when(questionnaireServiceFacade).saveResponses(Mockito.<QuestionGroupDetails>any());
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionGroupController.saveQuestionnaire(getQuestionGroupDetails(), 1, requestContext);
        assertThat(result, is("failure"));
        verify(requestContext, times(1)).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.invalid.numeric.range.response")));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testSaveQuestionnaireFailureForNumericResponseLessThanMinBound() {
        ValidationException validationException = new ValidationException(GENERIC_VALIDATION);
        validationException.addChildException(new BadNumericResponseException("q1", 10, null));
        doThrow(validationException).when(questionnaireServiceFacade).saveResponses(Mockito.<QuestionGroupDetails>any());
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionGroupController.saveQuestionnaire(getQuestionGroupDetails(), 1, requestContext);
        assertThat(result, is("failure"));
        verify(requestContext, times(1)).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.invalid.numeric.min.response")));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testSaveQuestionnaireFailureForNumericResponseGreaterThanMaxBound() {
        ValidationException validationException = new ValidationException(GENERIC_VALIDATION);
        validationException.addChildException(new BadNumericResponseException("q1", null, 100));
        doThrow(validationException).when(questionnaireServiceFacade).saveResponses(Mockito.<QuestionGroupDetails>any());
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionGroupController.saveQuestionnaire(getQuestionGroupDetails(), 1, requestContext);
        assertThat(result, is("failure"));
        verify(requestContext, times(1)).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.invalid.numeric.max.response")));
    }

    @Test
    public void testAddQuestion() {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE, "Create.Client", "Default");
        questionGroupForm.getCurrentQuestion().setText("Question1");
        questionGroupForm.getCurrentQuestion().setType("freeText");
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionGroupController.addQuestion(questionGroupForm, requestContext);
        assertThat(result, is("success"));
        verify(requestContext, times(1)).getMessageContext();
    }

    @Test
    public void testAddQuestionForEmptyTitle() {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE, "Create.Client", "Default");
        questionGroupForm.setValidator(validator);
        questionGroupForm.getCurrentQuestion().setText(null);
        questionGroupForm.getCurrentQuestion().setType("freeText");
        when(messageContext.hasErrorMessages()).thenReturn(true);
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionGroupController.addQuestion(questionGroupForm, requestContext);
        assertThat(result, is("failure"));
        verify(requestContext, times(1)).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("NotNull.QuestionGroupForm.currentQuestion.text")));
    }

    @Test
    public void testAddQuestionForEmptyType() {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE, "Create.Client", "Default");
        questionGroupForm.setValidator(validator);
        questionGroupForm.getCurrentQuestion().setText(TITLE);
        questionGroupForm.getCurrentQuestion().setType(null);
        when(messageContext.hasErrorMessages()).thenReturn(true);
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionGroupController.addQuestion(questionGroupForm, requestContext);
        assertThat(result, is("failure"));
        verify(requestContext, times(1)).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("NotNull.QuestionGroupForm.currentQuestion.type")));
    }

    @Test
    public void testAddQuestionForDuplicateTitle() {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE, "Create.Client", "Default");
        questionGroupForm.setValidator(validator);
        questionGroupForm.getCurrentQuestion().setText(TITLE);
        questionGroupForm.getCurrentQuestion().setType("freeText");
        when(questionnaireServiceFacade.isDuplicateQuestion(TITLE)).thenReturn(true);
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionGroupController.addQuestion(questionGroupForm, requestContext);
        assertThat(result, is("failure"));
        verify(requestContext, times(1)).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.question.duplicate")));
    }

    @Test
    public void testAddQuestionForInvalidAnswerChoices() {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE, "Create.Client", "Default");
        questionGroupForm.setValidator(validator);
        questionGroupForm.getCurrentQuestion().setText(TITLE);
        questionGroupForm.getCurrentQuestion().setType("singleSelect");
        questionGroupForm.getCurrentQuestion().setCurrentChoice("Choice1");
        questionGroupForm.getCurrentQuestion().addAnswerChoice();
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionGroupController.addQuestion(questionGroupForm, requestContext);
        assertThat(result, is("failure"));
        verify(requestContext, times(1)).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.question.choices")));
    }

    @Test
    public void testAddQuestionForInvalidNumericBounds() {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE, "Create.Client", "Default");
        questionGroupForm.setValidator(validator);
        questionGroupForm.getCurrentQuestion().setText(TITLE);
        questionGroupForm.getCurrentQuestion().setType("number");
        questionGroupForm.getCurrentQuestion().setNumericMin(100);
        questionGroupForm.getCurrentQuestion().setNumericMax(10);
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionGroupController.addQuestion(questionGroupForm, requestContext);
        assertThat(result, is("failure"));
        verify(requestContext, times(1)).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher(QuestionnaireConstants.INVALID_NUMERIC_BOUNDS)));
    }

    private QuestionGroupDetails getQuestionGroupDetails() {
        QuestionGroupDetails questionGroupDetails = new QuestionGroupDetails(1, 2, 3, asList(
                getQuestionGroupDetail(TITLE, "View", "Client", "S1", "S2"),
                getQuestionGroupDetail(TITLE, "View", "Client", "S1", "S3")
        ));
        return questionGroupDetails;
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
    private QuestionGroupDetail getQuestionGroupDetail(int questionGroupId, String title,String event, String source, boolean active, boolean editable, String... sectionNames) {
        List<SectionDetail> sectionDetails = new ArrayList<SectionDetail>();
        for (String sectionName : sectionNames) {
            SectionDetail sectionDetail = new SectionDetail();
            sectionDetail.setName(sectionName);
            sectionDetails.add(sectionDetail);
        }
        return new QuestionGroupDetail(questionGroupId, title, Arrays.asList(new EventSourceDto(event, source, null)), sectionDetails,editable,active);
    }

    private QuestionGroupForm getQuestionGroupForm(String title, String eventSourceId, String... sectionNames) {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setTitle(title);
        questionGroup.setEventSourceIds(Arrays.asList(eventSourceId));
        for (String sectionName : sectionNames) {
            questionGroup.setSectionName(sectionName);
            questionGroup.addCurrentSection();
        }
        return questionGroup;
    }

    private SectionDetailForm getSectionSectionDetailForm(String sectionName, List<SectionQuestionDetail> questions) {
        SectionDetailForm section = new SectionDetailForm();
        section.setName(sectionName);
        section.setQuestionDetails(questions);
        return section;
    }

    private SectionQuestionDetail getSectionQuestionDetail(int id, String title) {
        return new SectionQuestionDetail(new QuestionDetail(id, title, QuestionType.FREETEXT, true, true), true);
    }

    private QuestionDetail getQuestionDetail(int id, String title, QuestionType type) {
        return new QuestionDetail(id, title, type, true, true);
    }


}
