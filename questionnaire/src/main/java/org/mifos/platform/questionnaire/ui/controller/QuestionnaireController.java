/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;

@SuppressWarnings("PMD")
public class QuestionnaireController {

    @Autowired
    protected QuestionnaireServiceFacade questionnaireServiceFacade;

    protected QuestionnaireController() {
    }

    protected QuestionnaireController(QuestionnaireServiceFacade questionnaireServiceFacade) {
        this.questionnaireServiceFacade = questionnaireServiceFacade;
    }

    protected void constructAndLogSystemError(MessageContext messageContext, SystemException e) {
        constructErrorMessage(messageContext, e.getKey(), "id", "There is an unexpected failure. Please retry or contact technical support");
        //TODO: move mifosLogManager to common after dependency resolution
        //MifosLogManager.getLogger(LoggerConstants.ROOTLOGGER).error(e.getMessage(), e);
    }

    protected void constructErrorMessage(MessageContext context, String code, String source, String message) {
        MessageResolver messageResolver = new MessageBuilder().error().code(code).source(source).defaultText(message).build();
        context.addMessage(messageResolver);
    }

    protected void constructErrorMessage(String code, String message, MessageContext context, Object... args) {
        MessageResolver messageResolver = new MessageBuilder().error().code(code).defaultText(message).args(args).build();
        context.addMessage(messageResolver);
    }

    protected boolean isInvalidNumber(String id) {
        return (StringUtils.isEmpty(id) || !isInteger(id));
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
