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

package org.mifos.ui.core.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.questionnaire.contract.EventSource;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class QuestionGroupTest {

    @Test
    public void shouldGetEventSource() {
        QuestionGroup questionGroup;

        questionGroup = new QuestionGroup();
        questionGroup.setEventSourceId("event.source");
        assertEventSource(questionGroup.getEventSource(), "event", "source");

        questionGroup = new QuestionGroup();
        questionGroup.setEventSourceId(null);
        assertThat(questionGroup.getEventSource(), is(nullValue()));

        questionGroup = new QuestionGroup();
        questionGroup.setEventSourceId("");
        assertThat(questionGroup.getEventSource(), is(nullValue()));
    }

    @Test
    public void shouldSetEventSource() {
        QuestionGroup questionGroup;

        questionGroup = new QuestionGroup();
        questionGroup.setEventSource(new EventSource("Create", "Client", null));
        assertThat(questionGroup.getEventSourceId(), is("Create.Client"));

        questionGroup = new QuestionGroup();
        questionGroup.setEventSource(null);
        assertThat(questionGroup.getEventSourceId(), is(nullValue()));

        questionGroup = new QuestionGroup();
        questionGroup.setEventSource(new EventSource("", null, null));
        assertThat(questionGroup.getEventSourceId(), is(nullValue()));
    }

    private void assertEventSource(EventSource eventSource, String event, String source) {
        assertThat(eventSource, is(not(nullValue())));
        assertThat(eventSource.getEvent(), is(event));
        assertThat(eventSource.getSource(), is(source));
    }
}
