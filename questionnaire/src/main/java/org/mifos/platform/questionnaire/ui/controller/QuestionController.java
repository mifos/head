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
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.ui.model.Question;
import org.mifos.platform.questionnaire.ui.model.QuestionForm;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@SuppressWarnings("PMD")
public class QuestionController extends QuestionnaireController {

    @SuppressWarnings({"UnusedDeclaration"})
    public QuestionController() {
        super();
    }

    public QuestionController(QuestionnaireServiceFacade questionnaireServiceFacade) {
        super(questionnaireServiceFacade);
    }

    @RequestMapping("/viewQuestions.ftl")
    public String getAllQuestions(ModelMap model, HttpServletRequest request) {
        model.addAttribute("questions", questionnaireServiceFacade.getAllQuestions());
        return "viewQuestions";
    }

    @RequestMapping("/viewQuestionDetail.ftl")
    public String getQuestion(ModelMap model, HttpServletRequest httpServletRequest) {
        String questionId = httpServletRequest.getParameter("questionId");
        try {
            if (isInvalidNumber(questionId)) {
                model.addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_ID);
            } else {
                QuestionDetail questionDetail = questionnaireServiceFacade.getQuestionDetail(Integer.valueOf(questionId));
                model.addAttribute("questionDetail", new Question(questionDetail));
            }
        } catch (SystemException e) {
            //TODO: move mifosLogManager to common after dependency resolution
            //MifosLogManager.getLogger(LoggerConstants.ROOTLOGGER).error(e.getMessage(), e);
            model.addAttribute("error_message_code", QuestionnaireConstants.QUESTION_NOT_FOUND);
        }
        return "viewQuestionDetail";
    }

    public String addQuestion(QuestionForm questionForm, RequestContext requestContext) {
        MessageContext context = requestContext.getMessageContext();
        questionForm.validateConstraints(context);
        String result = "success";

        if (context.hasErrorMessages()) {
            result = "failure";
        }

        else if (isDuplicateQuestion(questionForm)) {
            constructErrorMessage(
                    context, "questionnaire.error.question.duplicate",
                    "currentQuestion.title", "The name specified already exists.");
            result = "failure";
        }

        else if(questionForm.answerChoicesAreInvalid()) {
            constructErrorMessage(
                    context, "questionnaire.error.question.choices",
                    "currentQuestion.choice", "Please specify at least 2 choices.");
            result = "failure";
        }

        else if (questionForm.numericBoundsAreInvalid()) {
            constructErrorMessage(
                    context, QuestionnaireConstants.INVALID_NUMERIC_BOUNDS,
                    "currentQuestion.numericMin", "Please specify valid numeric bounds.");
            result = "failure";
        }

        else {
            questionForm.addCurrentQuestion();
        }

        return result;
    }

    public void removeQuestion(QuestionForm questionForm, String questionTitle) {
        questionForm.removeQuestion(questionTitle);
    }

    public String createQuestions(QuestionForm questionForm, RequestContext requestContext) {
        String result = "success";
        try {
            questionnaireServiceFacade.createQuestions(getQuestionDetails(questionForm));
        } catch (SystemException e) {
            constructAndLogSystemError(requestContext.getMessageContext(), e);
            result = "failure";
        }
        return result;
    }

    private List<QuestionDetail> getQuestionDetails(QuestionForm questionForm) {
        List<QuestionDetail> questionDetails = new ArrayList<QuestionDetail>();
        for (Question question : questionForm.getQuestions()) {
            questionDetails.add(question.getQuestionDetail());
        }
        return questionDetails;
    }

    private boolean isDuplicateQuestion(QuestionForm questionForm) {
        String title = StringUtils.trim(questionForm.getCurrentQuestion().getTitle());
        return questionForm.isDuplicateTitle(title) || questionnaireServiceFacade.isDuplicateQuestion(title);
    }

}
