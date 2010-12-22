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
package org.mifos.platform.matchers;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.binding.message.DefaultMessageResolver;
import org.springframework.binding.message.MessageResolver;

@SuppressWarnings("PMD")
public class MessageMatcher extends TypeSafeMatcher<MessageResolver> {
    private String errorCode;

    public MessageMatcher(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public boolean matchesSafely(MessageResolver messageResolver) {
        DefaultMessageResolver defaultMessageResolver = (DefaultMessageResolver) messageResolver;
        String[] codes = defaultMessageResolver.getCodes();
        for (String code : codes) {
            if (StringUtils.equals(code, errorCode)) return true;
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(errorCode);
    }
}
