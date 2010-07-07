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
import org.mifos.platform.questionnaire.contract.QuestionGroupDetail;
import org.mifos.platform.questionnaire.contract.SectionDefinition;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class QuestionGroupDetailFormTest {

    @Test
    public void shouldGetEventSourceId() {
        EventSource eventSource = new EventSource("Create", "Client", "Create Client");
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(123, "Title", eventSource, new ArrayList<SectionDefinition>());
        QuestionGroupDetailForm questionGroupDetailForm = new QuestionGroupDetailForm(questionGroupDetail);
        assertThat(questionGroupDetailForm.getEventSourceId(), is("Create.Client"));
    }

    @Test
    public void shouldGetSections() {
        SectionDefinition sectionDefinition = new SectionDefinition();
        sectionDefinition.setName("Section1");
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(123, "Title", null, asList(sectionDefinition));
        QuestionGroupDetailForm questionGroupDetailForm = new QuestionGroupDetailForm(questionGroupDetail);
        List<SectionDetailForm> sections = questionGroupDetailForm.getSections();
        assertThat(sections, notNullValue());
        assertThat(sections.size(), is(1));
        assertThat(sections.get(0).getName(), is("Section1"));
    }
}
