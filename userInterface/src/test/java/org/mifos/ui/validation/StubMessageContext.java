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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageCriteria;
import org.springframework.binding.message.MessageResolver;
import org.springframework.context.MessageSource;

public class StubMessageContext implements MessageContext {

    private List<Message> messages = new ArrayList<Message>();

    private MessageSource messageSource;

    private final Locale locale = Locale.ENGLISH;

    public StubMessageContext() {
        messageSource = new StubMessageSource();
    }

    @Override
    public Message[] getAllMessages() {
        return messages.toArray(new Message[0]);
    }

    @Override
    public Message[] getMessagesBySource(Object source) {
        return (Message[]) messages.toArray();
    }

    @Override
    public Message[] getMessagesByCriteria(MessageCriteria criteria) {
        return (Message[]) messages.toArray();
    }

    @Override
    public boolean hasErrorMessages() {
        return messages.size() > 0;
    }

    @Override
    public void addMessage(MessageResolver messageResolver) {
        messages.add(messageResolver.resolveMessage(messageSource, locale));
    }

    @Override
    public void clearMessages() {
        messages.clear();
    }
}