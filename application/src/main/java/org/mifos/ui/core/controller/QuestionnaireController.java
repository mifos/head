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
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.contract.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isEmpty;

@Controller
public class QuestionnaireController {

    @Autowired
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @SuppressWarnings({"UnusedDeclaration"})
    public QuestionnaireController() {
    }

    public QuestionnaireController(QuestionnaireServiceFacade questionnaireServiceFacade) {
        this.questionnaireServiceFacade = questionnaireServiceFacade;
    }

    @RequestMapping("/viewQuestions.ftl")
    public String getAllQuestions(ModelMap model, HttpServletRequest request) {
        model.addAttribute("questions", questionnaireServiceFacade.getAllQuestions());
        return "viewQuestions";
    }

    @RequestMapping("/viewQuestionGroups.ftl")
    public String getAllQuestionGroups(ModelMap model, HttpServletRequest request) {
        model.addAttribute("questionGroups", questionnaireServiceFacade.getAllQuestionGroups());
        return "viewQuestionGroups";
    }

    @RequestMapping("/viewQuestionGroupDetail.ftl")
    public String getQuestionGroup(ModelMap model, HttpServletRequest httpServletRequest) {
        String questionGroupId = httpServletRequest.getParameter("questionGroupId");
        try {
            if (invalid(questionGroupId)) {
                model.addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_GROUP_ID);
            } else {
                QuestionGroupDetail questionGroupDetail = questionnaireServiceFacade.getQuestionGroupDetail(Integer.valueOf(questionGroupId));
                QuestionGroupForm questionGroupForm = new QuestionGroupForm(questionGroupDetail);
                model.addAttribute("questionGroupDetail", questionGroupForm);
                model.addAttribute("eventSources", getAllQgEventSources());
            }
        } catch (ApplicationException e) {
            MifosLogManager.getLogger(LoggerConstants.ROOTLOGGER).error(e.getMessage(), e);
            model.addAttribute("error_message_code", QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND);
        }
        return "viewQuestionGroupDetail";
    }


    @RequestMapping("/viewQuestionDetail.ftl")
    public String getQuestion(ModelMap model, HttpServletRequest httpServletRequest) {
        String questionId = httpServletRequest.getParameter("questionId");
        try {
            if (invalid(questionId)) {
                model.addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_ID);
            } else {
                QuestionDetail questionDetail = questionnaireServiceFacade.getQuestionDetail(Integer.valueOf(questionId));
                model.addAttribute("questionDetail", new Question(questionDetail));
            }
        } catch (ApplicationException e) {
            MifosLogManager.getLogger(LoggerConstants.ROOTLOGGER).error(e.getMessage(), e);
            model.addAttribute("error_message_code", QuestionnaireConstants.QUESTION_NOT_FOUND);
        }
        return "viewQuestionDetail";
    }

    public String addQuestion(QuestionForm questionForm, RequestContext requestContext) {
        MessageContext context = requestContext.getMessageContext();
        questionForm.validateConstraints(context);

        if (context.hasErrorMessages()) {
            return "failure";
        }

        if (isDuplicateQuestion(questionForm)) {
            constructErrorMessage(
                    context, "questionnaire.error.question.duplicate",
                    "currentQuestion.title", "The name specified already exists.");
            return "failure";
        }
        questionForm.addCurrentQuestion();
        return "success";
    }

    public void removeQuestion(QuestionForm questionForm, String questionTitle) {
        questionForm.removeQuestion(questionTitle);
    }

    public String createQuestions(QuestionForm questionForm, RequestContext requestContext) {
        try {
            questionnaireServiceFacade.createQuestions(getQuestionDetails(questionForm));
        } catch (ApplicationException e) {
            constructAndLogSystemError(requestContext.getMessageContext(), e);
            return "failure";
        }
        return "success";
    }

    private List<QuestionDetail> getQuestionDetails(QuestionForm questionForm) {
        List<QuestionDetail> questionDetails = new ArrayList<QuestionDetail>();
        for (Question question : questionForm.getQuestions()) {
            questionDetails.add(question.getQuestionDetail());
        }
        return questionDetails;
    }

    public String defineQuestionGroup(QuestionGroupForm questionGroupForm, RequestContext requestContext) {
        if (questionGroupHasErrors(questionGroupForm, requestContext)) {
            return "failure";
        }
        try {
            questionGroupForm.trimTitle();
            questionnaireServiceFacade.createQuestionGroup(questionGroupForm.getQuestionGroupDetail());
        } catch (ApplicationException e) {
            constructAndLogSystemError(requestContext.getMessageContext(), e);
            return "failure";
        }
        return "success";
    }

    public Map<String, String> getAllQgEventSources() {
        List<EventSource> eventSources = questionnaireServiceFacade.getAllEventSources();
        Map<String, String> evtSourcesMap = new HashMap<String, String>();
        for (EventSource evtSrc : eventSources) {
            evtSourcesMap.put(getEventSourceId(evtSrc), evtSrc.getDesciption());
        }
        return evtSourcesMap;
    }

    public String addSection(QuestionGroupForm questionGroupForm, RequestContext requestContext) {
        if(questionGroupForm.hasQuestionsInCurrentSection()){
            constructErrorMessage(requestContext.getMessageContext(),
                    "questionnaire.error.no.question.in.section", "currentSectionTitle", "Section should have at least one question.");
            return "failure";
        }
        questionGroupForm.addCurrentSection();
        return "success";
    }

    public String deleteSection(QuestionGroupForm questionGroupForm, String sectionName) {
        questionGroupForm.removeSection(sectionName);
        return "success";
    }

    public String deleteQuestion(QuestionGroupForm questionGroupForm, String sectionName, String questionId) {
        questionGroupForm.removeQuestion(sectionName,questionId);
        return "success";
    }

    private String getEventSourceId(EventSource evtSrc) {
        return evtSrc.getEvent().trim().concat(".").concat(evtSrc.getSource().trim());
    }

    private void constructAndLogSystemError(MessageContext messageContext, ApplicationException e) {
        constructErrorMessage(messageContext, e.getKey(), "id", "There is an unexpected failure. Please retry or contact technical support");
        MifosLogManager.getLogger(LoggerConstants.ROOTLOGGER).error(e.getMessage(), e);
    }

    private boolean isDuplicateQuestion(QuestionForm questionForm) {
        String title = StringUtils.trim(questionForm.getCurrentQuestion().getTitle());
        return questionForm.isDuplicateTitle(title) || questionnaireServiceFacade.isDuplicateQuestion(title);
    }

    private boolean isInvalidTitle(String title) {
        return isEmpty(StringUtils.trimToNull(title));
    }

    private void constructErrorMessage(MessageContext context, String code, String source, String message) {
        MessageResolver messageResolver = new MessageBuilder().error().code(code).source(source).defaultText(message).build();
        context.addMessage(messageResolver);
    }

    private boolean questionGroupHasErrors(QuestionGroupForm questionGroup, RequestContext requestContext) {
        boolean result = false;
        if (isInvalidTitle(questionGroup.getTitle())) {
            constructErrorMessage(requestContext.getMessageContext(), "questionnaire.error.questionGroup.title.empty", "title", "Please specify Question Group title");
            result = true;
        }
        if (sectionsNotPresent(questionGroup.getSections())) {
            constructErrorMessage(requestContext.getMessageContext(), "questionnaire.error.section.atLeastOne", "sectionName", "Please specify at least one section or question");
            result = true;
        }
        if (appliesToNotPresent(questionGroup.getEventSourceId())) {
            constructErrorMessage(requestContext.getMessageContext(), "questionnaire.error.appliesTo.mandatory", "eventSourceId", "Please choose a valid 'Applies To' value");
            result = true;
        }
        return result;
    }

    private boolean appliesToNotPresent(String eventSourceId) {
        return StringUtils.isEmpty(eventSourceId) || "--select one--".equals(eventSourceId);
    }

    private boolean sectionsNotPresent(List<SectionDetailForm> sections) {
        return CollectionUtils.isEmpty(sections);
    }

    private boolean invalid(String id) {
        return (isEmpty(id) || !isInteger(id));
    }

    private boolean isInteger(String id) {
        try {
            Integer.parseInt(id);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public List<SectionQuestionDetail> getAllSectionQuestions() {
        List<SectionQuestionDetail> sectionQuestionDetails = new ArrayList<SectionQuestionDetail>();
        List<QuestionDetail> questionDetails = questionnaireServiceFacade.getAllQuestions();
        if (questionDetails != null) {
            for (QuestionDetail questionDetail : questionDetails) {
                sectionQuestionDetails.add(new SectionQuestionDetail(questionDetail.getId(), questionDetail.getTitle(), false, QuestionType.FREETEXT));
            }
        }
        return sectionQuestionDetails;
    }
}
