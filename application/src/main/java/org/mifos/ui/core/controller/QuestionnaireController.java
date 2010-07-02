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
import org.mifos.platform.questionnaire.contract.EventSource;
import org.mifos.platform.questionnaire.contract.QuestionnaireServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
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
                QuestionGroup questionGroup = questionnaireServiceFacade.getQuestionGroup(Integer.valueOf(questionGroupId));
                model.addAttribute("questionGroupDetail", questionGroup);
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
                Question question = questionnaireServiceFacade.
                        getQuestion(Integer.valueOf(questionId));
                model.addAttribute("questionDetail", question);
            }
        } catch (ApplicationException e) {
            MifosLogManager.getLogger(LoggerConstants.ROOTLOGGER).error(e.getMessage(), e);
            model.addAttribute("error_message_code", QuestionnaireConstants.QUESTION_NOT_FOUND);
        }
        return "viewQuestionDetail";
    }

    public String addQuestion(QuestionForm questionForm, RequestContext requestContext) {
        if (questionFormHasErrors(questionForm, requestContext)) {
            return "failure";
        }
        questionForm.addCurrentQuestion();
        return "success";
    }

    public String createQuestions(QuestionForm questionForm, RequestContext requestContext) {
        try {
            questionnaireServiceFacade.createQuestions(questionForm.getQuestions());
        } catch (ApplicationException e) {
            constructAndLogSystemError(requestContext, e);
            return "failure";
        }
        return "success";
    }

    public String defineQuestionGroup(QuestionGroup questionGroup, RequestContext requestContext) {
        if (questionGroupHasErrors(questionGroup, requestContext)) {
            return "failure";
        }
        try {
            questionGroup.trimTitle();
            questionnaireServiceFacade.createQuestionGroup(questionGroup);
        } catch (ApplicationException e) {
            constructAndLogSystemError(requestContext, e);
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

    public String addSection(QuestionGroup questionGroup) {
        questionGroup.addCurrentSection();
        return "success";
    }

    public String deleteSection(QuestionGroup questionGroup, String sectionName) {
        questionGroup.removeSection(sectionName);
        return "success";
    }

    private String getEventSourceId(EventSource evtSrc) {
        return evtSrc.getEvent().trim().concat(".").concat(evtSrc.getSource().trim());
    }

    private void constructAndLogSystemError(RequestContext requestContext, ApplicationException e) {
        constructErrorMessage(requestContext, "questionnaire.serivce.failure", "id", "There is an unexpected failure. Please retry or contact technical support");
        MifosLogManager.getLogger(LoggerConstants.ROOTLOGGER).error(e.getMessage(), e);
    }

    private boolean isDuplicateQuestion(QuestionForm questionForm) {
        String title = StringUtils.trim(questionForm.getTitle());
        return questionForm.isDuplicateTitle(title) || questionnaireServiceFacade.isDuplicateQuestion(title);
    }

    private boolean isInvalidTitle(String title) {
        return isEmpty(StringUtils.trimToNull(title));
    }

    private void constructErrorMessage(RequestContext requestContext, String code, String source, String message) {
        MessageResolver messageResolver = new MessageBuilder().error().code(code).source(source).defaultText(message).build();
        requestContext.getMessageContext().addMessage(messageResolver);
    }

    private boolean questionGroupHasErrors(QuestionGroup questionGroup, RequestContext requestContext) {
        boolean result = false;
        if (isInvalidTitle(questionGroup.getTitle())) {
            constructErrorMessage(requestContext, "questionnaire.error.emptytitle", "title", "Please specify Question Group text");
            result = true;
        }
        if (sectionsNotPresent(questionGroup.getSections())) {
            constructErrorMessage(requestContext, "questionnaire.error.no.sections.in.group", "sectionName", "Please specify at least one section or question");
            result = true;
        }
        if (appliesToNotPresent(questionGroup.getEventSourceId())) {
            constructErrorMessage(requestContext, "questionnaire.error.empty.appliesTo", "eventSourceId", "Please choose a valid 'Applies To' value");
            result = true;
        }
        return result;
    }

    private boolean appliesToNotPresent(String eventSourceId) {
        return StringUtils.isEmpty(eventSourceId) || "--select one--".equals(eventSourceId);
    }

    private boolean sectionsNotPresent(List<SectionForm> sections) {
        return CollectionUtils.isEmpty(sections);
    }

    private boolean questionFormHasErrors(QuestionForm questionForm, RequestContext requestContext) {
        if (isInvalidTitle(questionForm.getTitle())) {
            constructErrorMessage(requestContext, "questionnaire.error.emptytitle", "title", "Please specify Question text");
            return true;
        }
        if (isDuplicateQuestion(questionForm)) {
            constructErrorMessage(requestContext, "questionnaire.error.duplicate.question.title", "title", "The name specified already exists.");
            return true;
        }
        return false;
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
}
