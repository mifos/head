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

package org.mifos.platform.questionnaire;

import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.contract.*;
import org.mifos.ui.core.controller.Question;
import org.mifos.ui.core.controller.QuestionGroupForm;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class QuestionnaireServiceFacadeImpl implements QuestionnaireServiceFacade {

    @Autowired
    private QuestionnaireService questionnaireService;

    public QuestionnaireServiceFacadeImpl(QuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public QuestionnaireServiceFacadeImpl() {
    }

    @Override
    public void createQuestions(List<Question> questions) throws ApplicationException {
        for (Question question: questions){
            questionnaireService.defineQuestion(new QuestionDefinition(question.getTitle(), QuestionType.FREETEXT));
        }
    }

    @Override
    public boolean isDuplicateQuestion(String title) {
        return questionnaireService.isDuplicateQuestion(new QuestionDefinition(title));
    }

    @Override
    public void createQuestionGroup(QuestionGroupForm questionGroupForm) throws ApplicationException {
        questionnaireService.defineQuestionGroup(new QuestionGroupDefinition(questionGroupForm.getTitle()));
    }

    @Override
    public List<QuestionDetail> viewAllQuestions() {
        return questionnaireService.getAllQuestions();
    }
}
