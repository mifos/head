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

package org.mifos.ui.validation;

import java.security.Principal;

import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;

/**
 * Stub validation context class useful for testing form beans that use JSR-303
 * validation, especially those that participate in webflows.
 */
public class StubValidationContext implements ValidationContext {

    private MessageContext messageContext = new StubMessageContext();

    @Override
    public MessageContext getMessageContext() {
        return messageContext;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getUserEvent() {
        return null;
    }

    @Override
    public Object getUserValue(String property) {
        return null;
    }
}