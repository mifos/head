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

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.matchers.MessageMatcher;
import org.mifos.platform.questionnaire.matchers.QuestionGroupDetailListMatcher;
import org.mifos.platform.questionnaire.matchers.QuestionGroupDetailMatcher;
import org.mifos.platform.questionnaire.service.EventSource;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.matchers.QuestionGroupDetailFormMatcher;
import org.mifos.platform.questionnaire.ui.model.QuestionGroupForm;
import org.mifos.platform.questionnaire.ui.model.SectionDetailForm;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.binding.message.MessageContext;
import org.springframework.ui.ModelMap;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Matchers.argThat;
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


    @Before
    public void setUp() {
        questionGroupController = new QuestionGroupController(questionnaireServiceFacade);
    }

    @Test
    public void shouldGetAllQuestionGroupResponses() throws UnsupportedEncodingException {
        List<QuestionGroupDetail> questionGroupDetails = Arrays.asList(new QuestionGroupDetail());
        when(questionnaireServiceFacade.getQuestionGroups(101, "Create", "Client")).thenReturn(questionGroupDetails);
        ModelMap modelMap = questionGroupController.getAllQuestionGroupResponses(101, "Create", "Client", "http://some.url");
        Assert.assertThat(modelMap, is(notNullValue()));
        Assert.assertThat((List<QuestionGroupDetail>) modelMap.get("questionGroupDetails"), is(questionGroupDetails));
        Assert.assertThat((String) modelMap.get("backPageUrl"), is("http://some.url"));
        Mockito.verify(questionnaireServiceFacade, Mockito.times(1)).getQuestionGroups(101, "Create", "Client");
    }

    @Test
    public void testDeleteSection() {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        String sectionName = "sectionName";
        questionGroup.setSectionName(sectionName);
        questionGroup.addCurrentSection();
        Assert.assertThat(questionGroup.getSections().size(), Is.is(1));
        questionGroupController.deleteSection(questionGroup, sectionName);
        Assert.assertThat(questionGroup.getSections().size(), Is.is(0));
    }

    @Test
    public void testAddSectionForSuccess() throws Exception {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setQuestionPool(new ArrayList<SectionQuestionDetail>(Arrays.asList(getSectionQuestionDetail(1, "Q1"), getSectionQuestionDetail(2, "Q2"))));
        questionGroup.setSelectedQuestionIds(Arrays.asList("1"));
        questionGroup.setTitle("title");
        questionGroup.setSectionName("sectionName");
        String result = questionGroupController.addSection(questionGroup, requestContext);
        Assert.assertThat(questionGroup.getSections().size(), Is.is(1));
        Assert.assertThat(questionGroup.getSections().get(0).getName(), Is.is("sectionName"));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), Is.is(1));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getTitle(), Is.is("Q1"));
        Assert.assertThat(result, Is.is("success"));
    }

    @Test
    public void testAddSectionsSuccessWhenSectionNameIsNotProvided() {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setQuestionPool(new ArrayList<SectionQuestionDetail>(Arrays.asList(getSectionQuestionDetail(1, "Q1"), getSectionQuestionDetail(2, "Q2"))));
        questionGroup.setSelectedQuestionIds(Arrays.asList("2"));
        String result = questionGroupController.addSection(questionGroup, requestContext);
        Assert.assertThat(questionGroup.getSections().size(), Is.is(1));
        Assert.assertThat(questionGroup.getSections().get(0).getName(), Is.is("Misc"));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), Is.is(1));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getTitle(), Is.is("Q2"));
        Assert.assertThat(result, Is.is("success"));
    }

    @Test
    public void testAddSectionForSuccessWhenQuestionTitleProvidedWithAllBlanks() throws Exception {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        questionGroup.setQuestionPool(new ArrayList<SectionQuestionDetail>(Arrays.asList(getSectionQuestionDetail(1, "Q1"), getSectionQuestionDetail(2, "Q2"))));
        questionGroup.setSelectedQuestionIds(Arrays.asList("1", "2"));
        questionGroup.setSectionName("        ");
        String result = questionGroupController.addSection(questionGroup, requestContext);
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
        String result = questionGroupController.addSection(questionGroup, requestContext);
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.no.question.in.section")));
        Assert.assertThat(result, Is.is("failure"));
    }

    @Test
    public void testRemoveQuestionFromSection() {
        QuestionGroupForm questionGroup = new QuestionGroupForm();
        List<SectionDetailForm> sections = new ArrayList<SectionDetailForm>();
        sections.add(getSectionSectionDetailForm("sectionName", new ArrayList<SectionQuestionDetail>(Arrays.asList(getSectionQuestionDetail(1, "Q1"), getSectionQuestionDetail(2, "Q2")))));
        questionGroup.setSections(sections);

        Assert.assertThat(questionGroup.getSections().size(), CoreMatchers.is(1));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), CoreMatchers.is(2));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getTitle(), CoreMatchers.is("Q1"));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(1).getTitle(), CoreMatchers.is("Q2"));

        questionGroupController.deleteQuestion(questionGroup, "sectionName", "1");

        Assert.assertThat(questionGroup.getSections().size(), CoreMatchers.is(1));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().size(), CoreMatchers.is(1));
        Assert.assertThat(questionGroup.getSections().get(0).getSectionQuestions().get(0).getTitle(), CoreMatchers.is("Q2"));

        questionGroupController.deleteQuestion(questionGroup, "sectionName", "2");

        Assert.assertThat(questionGroup.getSections().size(), CoreMatchers.is(0));
    }

    @Test
    public void testCreateQuestionGroupSuccess() throws Exception {
        QuestionGroupForm questionGroup = getQuestionGroupForm("   " + TITLE + " ", "Create.Client", "S1", "S2");
        String result = questionGroupController.defineQuestionGroup(questionGroup, requestContext);
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
        String result = questionGroupController.defineQuestionGroup(questionGroupForm, requestContext);
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
        String result = questionGroupController.defineQuestionGroup(questionGroupForm, requestContext);
        Assert.assertThat(result, Is.is("failure"));
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.section.atLeastOne")));
    }

    @Test
    public void testCreateQuestionGroupForFailureWhenQuestionGroupAppliesToNotProvided() throws Exception {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE, QuestionnaireConstants.DEFAULT_APPLIES_TO_OPTION, "Section");
        Mockito.when(requestContext.getMessageContext()).thenReturn(messageContext);
        String result = questionGroupController.defineQuestionGroup(questionGroupForm, requestContext);
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
        String result = questionGroupController.defineQuestionGroup(questionGroupForm, requestContext);
        Assert.assertThat(result, Is.is("failure"));
        Mockito.verify(questionnaireServiceFacade).createQuestionGroup(Matchers.<QuestionGroupDetail>anyObject());
        Mockito.verify(requestContext).getMessageContext();
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.duplicate.question.found.in.section")));
    }

    @Test
    public void shouldGetAllQuestionGroups() {
        List<QuestionGroupDetail> questionGroupDetails = Arrays.asList(
                getQuestionGroupDetail(1, TITLE, "title1", "sectionName1"), getQuestionGroupDetail(1, TITLE, "title1", "sectionName1"));
        Mockito.when(questionnaireServiceFacade.getAllQuestionGroups()).thenReturn(questionGroupDetails);
        String view = questionGroupController.getAllQuestionGroups(model, httpServletRequest);
        Assert.assertThat(view, Is.is("viewQuestionGroups"));
        Mockito.verify(questionnaireServiceFacade).getAllQuestionGroups();
        Mockito.verify(model).addAttribute(Matchers.eq("questionGroups"), argThat(new QuestionGroupDetailListMatcher(questionGroupDetails)));
    }

    @Test
    public void shouldGetQuestionGroupById() throws SystemException {
        int questionGroupId = 1;
        QuestionGroupDetail questionGroupDetail = getQuestionGroupDetail(questionGroupId, TITLE, "S1", "S2", "S3");
        Mockito.when(questionnaireServiceFacade.getQuestionGroupDetail(questionGroupId)).thenReturn(questionGroupDetail);
        Mockito.when(httpServletRequest.getParameter("questionGroupId")).thenReturn(Integer.toString(questionGroupId));
        String view = questionGroupController.getQuestionGroup(model, httpServletRequest);
        Assert.assertThat(view, Is.is("viewQuestionGroupDetail"));
        Mockito.verify(questionnaireServiceFacade).getQuestionGroupDetail(questionGroupId);
        Mockito.verify(questionnaireServiceFacade, Mockito.times(1)).getAllEventSources();
        Mockito.verify(httpServletRequest, Mockito.times(1)).getParameter("questionGroupId");
        Mockito.verify(model).addAttribute(Matchers.eq("questionGroupDetail"), argThat(new QuestionGroupDetailFormMatcher(new QuestionGroupForm(questionGroupDetail))));
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test
    public void testGetQuestionGroupWhenNotPresentInDb() throws SystemException {
        int questionGroupId = 1;
        Mockito.when(questionnaireServiceFacade.getQuestionGroupDetail(questionGroupId)).thenThrow(new SystemException(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        Mockito.when(httpServletRequest.getParameter("questionGroupId")).thenReturn("1");
        String view = questionGroupController.getQuestionGroup(model, httpServletRequest);
        Assert.assertThat(view, Is.is("viewQuestionGroupDetail"));
        Mockito.verify(questionnaireServiceFacade).getQuestionGroupDetail(questionGroupId);
        Mockito.verify(httpServletRequest, Mockito.times(1)).getParameter("questionGroupId");
        Mockito.verify(model).addAttribute("error_message_code", QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND);
    }

    @Test
    public void testGetQuestionGroupWhenIdIsNull() throws SystemException {
        Mockito.when(httpServletRequest.getParameter("questionGroupId")).thenReturn(null);
        String view = questionGroupController.getQuestionGroup(model, httpServletRequest);
        Assert.assertThat(view, Is.is("viewQuestionGroupDetail"));
        Mockito.verify(httpServletRequest, Mockito.times(1)).getParameter("questionGroupId");
        Mockito.verify(questionnaireServiceFacade, Mockito.times(0)).getQuestionGroupDetail(Matchers.anyInt());
        Mockito.verify(model).addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_GROUP_ID);
    }

    @Test
    public void testGetQuestionGroupWhenIdIsNotInteger() throws SystemException {
        Mockito.when(httpServletRequest.getParameter("questionGroupId")).thenReturn("1A");
        String view = questionGroupController.getQuestionGroup(model, httpServletRequest);
        Assert.assertThat(view, Is.is("viewQuestionGroupDetail"));
        Mockito.verify(httpServletRequest, Mockito.times(1)).getParameter("questionGroupId");
        Mockito.verify(questionnaireServiceFacade, Mockito.times(0)).getQuestionGroupDetail(Matchers.anyInt());
        Mockito.verify(model).addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_GROUP_ID);
    }

    @Test
    public void shouldGetAllQgEventSources() {
        Mockito.when(questionnaireServiceFacade.getAllEventSources()).thenReturn(Arrays.asList(new EventSource("Create", "Client", "Create Client"), new EventSource("View", "Client", "View Client")));
        Map<String, String> eventSources = questionGroupController.getAllQgEventSources();
        Mockito.verify(questionnaireServiceFacade).getAllEventSources();
        Assert.assertThat(eventSources.get("Create.Client"), Is.is("Create Client"));
        Assert.assertThat(eventSources.get("View.Client"), Is.is("View Client"));
    }

    @Test
    public void shouldGetAllSectionQuestions() {
        Mockito.when(questionnaireServiceFacade.getAllQuestions()).thenReturn(Arrays.asList(getQuestionDetail(1, "Q1", QuestionType.NUMERIC), getQuestionDetail(2, "Q2", QuestionType.DATE)));
        List<SectionQuestionDetail> sectionQuestions = questionGroupController.getAllSectionQuestions();
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
        List<SectionQuestionDetail> sectionQuestions = questionGroupController.getAllSectionQuestions();
        Assert.assertThat(sectionQuestions, Is.is(notNullValue()));
        Assert.assertThat(sectionQuestions.size(), Is.is(0));
        Mockito.verify(questionnaireServiceFacade).getAllQuestions();
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

    private SectionDetailForm getSectionSectionDetailForm(String sectionName, List<SectionQuestionDetail> questions) {
        SectionDetailForm section = new SectionDetailForm();
        section.setName(sectionName);
        section.setQuestionDetails(questions);
        return section;
    }

    private SectionQuestionDetail getSectionQuestionDetail(int id, String title) {
        return new SectionQuestionDetail(new QuestionDetail(id, title, title, QuestionType.FREETEXT), true);
    }

    private QuestionDetail getQuestionDetail(int id, String title, QuestionType type) {
        return new QuestionDetail(id, title, title, type);
    }


}
