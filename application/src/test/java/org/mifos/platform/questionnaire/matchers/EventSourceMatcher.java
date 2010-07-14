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
package org.mifos.platform.questionnaire.matchers;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mifos.platform.questionnaire.contract.EventSource;

public class EventSourceMatcher extends TypeSafeMatcher<EventSource> {

    private EventSource eventSource;

    public EventSourceMatcher(EventSource eventSource) {
        this.eventSource = eventSource;
    }

    public EventSourceMatcher(String event, String source, String desc) {
        this.eventSource = new EventSource(event, source, desc);
    }

    @Override
    public boolean matchesSafely(EventSource eventSource) {
        return StringUtils.endsWithIgnoreCase(this.eventSource.getDesciption(), eventSource.getDesciption()) &&
                StringUtils.endsWithIgnoreCase(this.eventSource.getSource(), eventSource.getSource()) &&
                StringUtils.endsWithIgnoreCase(this.eventSource.getEvent(), eventSource.getEvent());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Event sources do not match");
    }

}
