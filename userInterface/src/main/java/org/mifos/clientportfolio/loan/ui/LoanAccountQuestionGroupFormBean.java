/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.clientportfolio.loan.ui;

import java.io.Serializable;
import java.util.List;

import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.validations.ValidationException;
import org.mifos.ui.core.controller.util.ValidationExceptionMessageExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanAccountQuestionGroupFormBean implements Serializable {

    @Autowired
    private transient QuestionnaireServiceFacade questionnaireServiceFacade;
    
    private List<QuestionGroupDetail> questionGroups;

    public void validateAnswerQuestionGroupStep(ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        try {
            questionnaireServiceFacade.validateResponses(this.questionGroups);
        } catch (ValidationException e) {
            ValidationExceptionMessageExtractor extractor = new ValidationExceptionMessageExtractor();
            extractor.extract(messages, e);
        }
    }
    
    public List<QuestionGroupDetail> getQuestionGroups() {
        return questionGroups;
    }

    public void setQuestionGroups(List<QuestionGroupDetail> questionGroups) {
        this.questionGroups = questionGroups;
    }
}