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
import org.springframework.webflow.execution.RequestContext;

@Controller
public class QuestionnaireController {

    @Autowired
    private QuestionnaireServiceFacade questionnaireServiceFacade;

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
        if (questionFormHasErrors(questionForm, requestContext)) return "failure";
        try {
            questionForm.addCurrentQuestion();
            questionnaireServiceFacade.createQuestions(questionForm.getQuestions());
        } catch (ApplicationException e) {
            constructMessage(requestContext, "questionnaire.serivce.failure", "title", "There is an unexpected failure. Please retry or contact technical support");
            MifosLogManager.getLogger(LoggerConstants.ROOTLOGGER).error(e.getMessage(), e);
            return "failure";
        }
        return "success";
    }

    private boolean isDuplicateQuestion(QuestionForm questionForm) {
        String title = questionForm.getTitle();
        return questionForm.isDuplicateTitle(title) || questionnaireServiceFacade.isDuplicateQuestion(title);
    }

    private boolean isInvalid(QuestionForm questionForm) {
        return StringUtils.isEmpty(questionForm.getTitle());
    }

    private void constructMessage(RequestContext requestContext, String code, String source, String message) {
        MessageBuilder messageBuilder = new MessageBuilder().error().code(code).source(source).defaultText(message);
        MessageResolver messageResolver = messageBuilder.build();
        requestContext.getMessageContext().addMessage(messageResolver);
    }

    private boolean questionFormHasErrors(QuestionForm questionForm, RequestContext requestContext) {
        if (isInvalid(questionForm)) {
            constructMessage(requestContext, "questionnaire.error.emptytitle", "title", "Please specify Question text");
            return true;
        }
        if (isDuplicateQuestion(questionForm)) {
            constructMessage(requestContext, "questionnaire.error.duplicate.question.title", "title", "The name specified already exists.");
            return true;
        }
        return false;
    }

}
