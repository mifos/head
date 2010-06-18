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
import org.mifos.platform.questionnaire.contract.QuestionnaireServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;

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

    public String addQuestion(QuestionForm questionForm, RequestContext requestContext) {
        if (questionFormHasErrors(questionForm, requestContext)) return "failure";
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

    public String defineQuestionGroup(QuestionGroupForm questionGroupForm, RequestContext requestContext) {
        if (questionGroupFormHasErrors(questionGroupForm, requestContext)) return "failure";
        try {
            questionnaireServiceFacade.createQuestionGroup(questionGroupForm);
        } catch (ApplicationException e) {
            constructAndLogSystemError(requestContext, e);
            return "failure";
        }
        return "success";
    }

    private void constructAndLogSystemError(RequestContext requestContext, ApplicationException e) {
        constructErrorMessage(requestContext, "questionnaire.serivce.failure", "title", "There is an unexpected failure. Please retry or contact technical support");
        MifosLogManager.getLogger(LoggerConstants.ROOTLOGGER).error(e.getMessage(), e);
    }

    private boolean isDuplicateQuestion(QuestionForm questionForm) {
        String title = questionForm.getTitle();
        return questionForm.isDuplicateTitle(title) || questionnaireServiceFacade.isDuplicateQuestion(title);
    }

    private boolean isInvalidTitle(String title) {
        return StringUtils.isEmpty(title);
    }

    private void constructErrorMessage(RequestContext requestContext, String code, String source, String message) {
        MessageResolver messageResolver = new MessageBuilder().error().code(code).source(source).defaultText(message).build();
        requestContext.getMessageContext().addMessage(messageResolver);
    }

    private boolean questionGroupFormHasErrors(QuestionGroupForm questionGroupForm, RequestContext requestContext) {
        if (isInvalidTitle(questionGroupForm.getTitle())) {
            constructErrorMessage(requestContext, "questionnaire.error.emptytitle", "title", "Please specify Question Group text");
            return true;
        }
        return false;
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

    @RequestMapping("/viewQuestions.ftl")
    public String getAllQuestions(ModelMap model, HttpServletRequest request){
        model.addAttribute("questions", questionnaireServiceFacade.getAllQuestions());
        return "viewQuestions";
    }

    @RequestMapping("/viewQuestionGroups.ftl")
    public String getAllQuestionGroups(ModelMap model, HttpServletRequest request) {
        model.addAttribute("questionGroups", questionnaireServiceFacade.getAllQuestionGroups());
        return "viewQuestionGroups";
    }
}
