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

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

class StubMessageSource implements MessageSource {

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage,
            Locale locale) {
        return code + defaultMessage;
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale)
            throws NoSuchMessageException {
        return code;
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale)
            throws NoSuchMessageException {
        return resolvable.getCodes().length > 0 ? resolvable.getCodes()[0]
                : resolvable.getDefaultMessage();
    }
}