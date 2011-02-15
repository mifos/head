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
package org.mifos.platform.questionnaire.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
@SuppressWarnings("PMD")
public class EventSourcesMatcher extends TypeSafeMatcher<List<EventSourceDto>> {

    private List<EventSourceDto> eventSourceDtos;

    public EventSourcesMatcher(List<EventSourceDto> eventSourceDtos) {
        this.eventSourceDtos = eventSourceDtos;
    }

    @Override
    public boolean matchesSafely(List<EventSourceDto> eventSourceDtos) {
        if (eventSourceDtos.size() == this.eventSourceDtos.size()) {
            for (EventSourceDto eventSourceDto : this.eventSourceDtos) {
                assertThat(eventSourceDtos, hasItem(new EventSourceMatcher(eventSourceDto)));
            }
            return true;
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("EventSources did not match");
    }

}
