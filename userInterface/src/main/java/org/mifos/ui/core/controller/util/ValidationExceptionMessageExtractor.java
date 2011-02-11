/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.ui.core.controller.util;

import static java.text.MessageFormat.format;

import java.util.List;

import org.mifos.platform.questionnaire.exceptions.BadNumericResponseException;
import org.mifos.platform.questionnaire.exceptions.MandatoryAnswerNotFoundException;
import org.mifos.platform.validations.ValidationException;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;

/**
 * Extract validation error information from ValidationException and populate an instance 
 * of MessageContext with the information.
 * 
 * Adapted from QuestionGroupController.
 */
public class ValidationExceptionMessageExtractor  {
    
    public void extract(MessageContext context, ValidationException e) {
        if (e.hasChildExceptions()) {
            List<ValidationException> children = e.getChildExceptions();
            for (ValidationException child : children) {
                if (child instanceof BadNumericResponseException) {
                    extractBadNumericResponseException(context, (BadNumericResponseException) child);
                } else if (child instanceof MandatoryAnswerNotFoundException) {
                    extractMandatoryAnswerNotFoundException(context, (MandatoryAnswerNotFoundException) child);
                } else if (child instanceof ValidationException) {
                    extractDefault(context, child);
                } else {
                    throw new IllegalArgumentException("Unhandled exception type: " + child.getClass());
                }
            }
        }
    }

    private void constructErrorMessage(String code, String defaultMessage,
            MessageContext context, Object... args) {

        MessageResolver messageResolver = new MessageBuilder().error()
                .code(code).defaultText(defaultMessage).args(args).build();
        context.addMessage(messageResolver);
    }
    
    private void extractBadNumericResponseException(MessageContext context, BadNumericResponseException e) {
        String title = e.getIdentifier();
        String code, message;
        Integer allowedMinValue = e.getAllowedMinValue();
        Integer allowedMaxValue = e.getAllowedMaxValue();
        if (e.areMinMaxBoundsPresent()) {
            code = "questionnaire.invalid.numeric.range.response";
            message = format("Please specify a number between {0} and {1} for {2}", allowedMinValue, allowedMaxValue, title);
            constructErrorMessage(code, message, context, allowedMinValue, allowedMaxValue, title);
        } else if (e.isMinBoundPresent()) {
            code = "questionnaire.invalid.numeric.min.response";
            message = format("Please specify a number greater than {0} for {1}", allowedMinValue, title);
            constructErrorMessage(code, message, context, allowedMinValue, title);
        } else if (e.isMaxBoundPresent()) {
            code = "questionnaire.invalid.numeric.max.response";
            message = format("Please specify a number lesser than {0} for {1}", allowedMaxValue, title);
            constructErrorMessage(code, message, context, allowedMaxValue, title);
        } else {
            code = "questionnaire.invalid.numeric.response";
            message = format("Please specify a number for {0}", title);
            constructErrorMessage(code, message, context, title);
        }
    }
    
    private void extractDefault(MessageContext context, ValidationException e) {
        String code = e.getKey();
        String source = e.getIdentifier();
        String defaultMessage = e.getLocalizedMessage();
        constructErrorMessage(code, defaultMessage, context, source);
    }
    
    private void extractMandatoryAnswerNotFoundException(MessageContext context, MandatoryAnswerNotFoundException e) {
        String title = e.getIdentifier();
        constructErrorMessage("questionnaire.noresponse", format("Please specify {0}", title), context, title);
    }
}
