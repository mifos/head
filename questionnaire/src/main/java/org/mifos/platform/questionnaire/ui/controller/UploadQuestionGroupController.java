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

import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.exceptions.ValidationException;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.ui.model.UploadQuestionGroupForm;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Controller;
import org.springframework.webflow.execution.RequestContext;

import java.util.List;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.SELECT_ONE;

@Controller
public class UploadQuestionGroupController extends QuestionnaireController {

    public UploadQuestionGroupController() {
        super();
    }

    public UploadQuestionGroupController(QuestionnaireServiceFacade questionnaireServiceFacade) {
        super(questionnaireServiceFacade);
    }

    public List<String> getAllCountriesForPPI() {
        return questionnaireServiceFacade.getAllCountriesForPPI();
    }

    public String upload(UploadQuestionGroupForm uploadQuestionGroupForm, RequestContext requestContext) {
        String result = "success";
        if (selectedCountryNotPresent(uploadQuestionGroupForm.getSelectedCountry())) {
            constructErrorMessage(requestContext.getMessageContext(), "questionnaire.error.ppi.country", "selectedCountry", "Please specify the Country");
            result = "failure";
        } else {
            try {
                questionnaireServiceFacade.uploadPPIQuestionGroup(uploadQuestionGroupForm.getSelectedCountry());
            } catch(ValidationException e) {
                handleValidationException(e, requestContext);
                result = "failure";
            } catch (SystemException e) {
                constructErrorMessage(requestContext.getMessageContext(), e.getKey(), "selectedCountry", e.getKey());
                result = "failure";
            } catch (Exception e) {
                constructAndLogSystemError(requestContext.getMessageContext(), new SystemException(e.getMessage(), e));
                result = "failure";
            }
        }
        return result;
    }

    private void handleValidationException(ValidationException validationException, RequestContext requestContext) {
        if (validationException.containsChildExceptions()) {
            MessageContext messageContext = requestContext.getMessageContext();
            for (ValidationException childException : validationException.getChildExceptions()) {
                constructErrorMessage(messageContext, childException.getKey(), "selectedCountry", childException.getKey());
            }
        }
    }

    private boolean selectedCountryNotPresent(String selectedCountry) {
        return isEmpty(selectedCountry) || equalsIgnoreCase(SELECT_ONE, selectedCountry);
    }
}
