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

    public String addQuestion(QuestionForm questionForm, RequestContext requestContext, boolean createMode) {
        MessageContext context = requestContext.getMessageContext();
        questionForm.validateConstraints(context);
        String result = "success";
        String title = StringUtils.trim(questionForm.getCurrentQuestion().getTitle());

        if (context.hasErrorMessages()) {
            result = "failure";
        }

        else if (checkDuplicateTitleForCreateOperation(questionForm, createMode)) {
            constructErrorMessage(
                    context, "questionnaire.error.question.duplicate",
                    "currentQuestion.title", "The name specified already exists.");
            result = "failure";
        }

        else if (checkDuplicateTitleForEditOperation(questionForm, createMode, title)) {
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
                    "currentQuestion.numericMin", "Please ensure maximum value is greater than minimum value.");
            result = "failure";
        }

        else {
            questionForm.addCurrentQuestion();
        }

        return result;
    }

    private boolean checkDuplicateTitleForEditOperation(QuestionForm questionForm, boolean createMode, String title) {
        return !createMode && questionForm.titleHasChanged() && questionnaireServiceFacade.isDuplicateQuestion(title);
    }

    private boolean checkDuplicateTitleForCreateOperation(QuestionForm questionForm, boolean createMode) {
        return createMode && isDuplicateQuestion(questionForm);
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
