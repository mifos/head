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

package org.mifos.platform.questionnaire.parsers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.questionnaire.service.EventSource;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class QuestionGroupDefinitionParserTest {

    @Test
    public void shouldParseQuestionDefinitionXml() throws IOException {
        QuestionGroupDefinitionParser questionGroupDefinitionParser = new QuestionGroupDefinitionParser();
        QuestionGroupDto questionGroupDto = questionGroupDefinitionParser.parse("/org/mifos/platform/questionnaire/QuestionGroupDefinition.xml");
        assertThat(questionGroupDto, is(notNullValue()));
        assertThat(questionGroupDto.getTitle(), is("PPI India"));
        assertThat(questionGroupDto.isEditable(), is(false));
        assertThat(questionGroupDto.isPpi(), is(true));
        EventSource eventSource = questionGroupDto.getEventSource();
        assertThat(eventSource, is(notNullValue()));
        assertThat(eventSource.getEvent(), is("Create"));
        assertThat(eventSource.getSource(), is("Loan"));
        List<SectionDto> sectionDtos = questionGroupDto.getSections();
        assertThat(sectionDtos, is(notNullValue()));
        assertThat(sectionDtos.size(), is(2));
    }
}
