/*
 * Copyright Grameen Foundation USA
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QuestionGroupDefinitionParserTest {

    @Test
    public void shouldParseQuestionDefinitionXml() throws Exception {
        QuestionGroupDefinitionParser questionGroupDefinitionParser = new QuestionGroupDefinitionParserImpl();
        String questionGroupDefXml = "/org/mifos/platform/questionnaire/QuestionGroupDefinition.xml";
        InputStream inputStream = getClass().getResourceAsStream(questionGroupDefXml);
        QuestionGroupDto questionGroupDto = questionGroupDefinitionParser.parse(inputStream);
        assertQuestionGroupDto(questionGroupDto);
        EventSourceDto eventSourceDto = questionGroupDto.getEventSourceDtos().get(0);
        assertEventSource(eventSourceDto);
        List<SectionDto> sectionDtos = questionGroupDto.getSections();
        assertSections(sectionDtos);
    }

    private void assertSections(List<SectionDto> sectionDtos) {
        assertThat(sectionDtos, is(notNullValue()));
        assertThat(sectionDtos.size(), is(2));
        assertFirstSection(sectionDtos.get(0));
        assertSecondSection(sectionDtos.get(1));
    }

    private void assertFirstSection(SectionDto sectionDto) {
        assertThat(sectionDto.getName(), is("default"));
        assertThat(sectionDto.getOrder(), is(1));
        List<QuestionDto> questions = sectionDto.getQuestions();
        assertThat(questions, is(notNullValue()));
        assertThat(questions.size(), is(2));
        QuestionDto questionDto = questions.get(0);
        assertThat(questionDto.getNickname(), is("date_of_birth"));
        assertThat(questionDto.getText(), is("Your DOB"));
        assertThat(questionDto.getType(), is(QuestionType.DATE));
        assertThat(questionDto.getOrder(), is(1));
        questionDto = questions.get(1);
        assertThat(questionDto.getNickname(), is("num_family_members"));
        assertThat(questionDto.getText(), is("How many family members"));
        assertThat(questionDto.getType(), is(QuestionType.NUMERIC));
        assertThat(questionDto.getOrder(), is(2));
        assertThat(questionDto.getMinValue(), is(3));
        assertThat(questionDto.getMaxValue(), is(10));
        assertThat(questionDto.isMandatory(), is(true));
    }

    private void assertSecondSection(SectionDto sectionDto) {
        assertThat(sectionDto.getName(), is("misc"));
        assertThat(sectionDto.getOrder(), is(2));
        List<QuestionDto> questions = sectionDto.getQuestions();
        assertThat(questions, is(notNullValue()));
        assertThat(questions.size(), is(3));
        QuestionDto questionDto = questions.get(0);
        assertThat(questionDto.getNickname(), is("father_name"));
        assertThat(questionDto.getText(), is("Father's name'"));
        assertThat(questionDto.getType(), is(QuestionType.FREETEXT));
        assertThat(questionDto.getOrder(), is(1));
        questionDto = questions.get(1);
        assertThat(questionDto.getNickname(), is("num_dependents"));
        assertThat(questionDto.getText(), is("No of dependents"));
        assertThat(questionDto.getType(), is(QuestionType.SINGLE_SELECT));
        assertThat(questionDto.getOrder(), is(2));
        assertThat(questionDto.isMandatory(), is(true));
        List<ChoiceDto> choices = questionDto.getChoices();
        assertThat(choices, is(notNullValue()));
        assertThat(choices.size(), is(3));
        assertChoiceDetail(choices.get(0), "Less than 2", 1);
        assertChoiceDetail(choices.get(1), "Less than 5", 2);
        assertChoiceDetail(choices.get(2), "Less than 10", 3);
        questionDto = questions.get(2);
        assertThat(questionDto.getNickname(), is("previous_loans"));
        assertThat(questionDto.getText(), is("Previous Loans taken for"));
        assertThat(questionDto.getType(), is(QuestionType.SMART_SELECT));
        assertThat(questionDto.getOrder(), is(3));
        assertThat(questionDto.isMandatory(), is(true));
        choices = questionDto.getChoices();
        assertThat(choices, is(notNullValue()));
        assertThat(choices.size(), is(4));
        assertFirstChoiceWithTags(choices.get(0));
        assertSecondChoiceWithTags(choices.get(1));
        assertThirdChoiceWithTags(choices.get(2));
        assertFourthChoiceWithTags(choices.get(3));
    }

    private void assertFirstChoiceWithTags(ChoiceDto choiceDto) {
        assertChoiceDetail(choiceDto, "No Product", 1);
        List<String> tags = choiceDto.getTags();
        assertThat(tags, is(notNullValue()));
        assertThat(tags.size(), is(1));
        assertThat(tags.get(0), is("Never"));
    }

    private void assertSecondChoiceWithTags(ChoiceDto choiceDto) {
        assertChoiceDetail(choiceDto, "Product 1", 2);
        List<String> tags = choiceDto.getTags();
        assertThat(tags, is(notNullValue()));
        assertThat(tags.size(), is(1));
        assertThat(tags.get(0), is("Agriculture"));
    }

    private void assertThirdChoiceWithTags(ChoiceDto choiceDto) {
        assertChoiceDetail(choiceDto, "Product 2", 3);
        List<String> tags = choiceDto.getTags();
        assertThat(tags, is(notNullValue()));
        assertThat(tags.size(), is(2));
        assertThat(tags.get(0), is("Fishing"));
        assertThat(tags.get(1), is("Farming"));
    }

    private void assertFourthChoiceWithTags(ChoiceDto choiceDto) {
        assertChoiceDetail(choiceDto, "Product 3", 4);
        List<String> tags = choiceDto.getTags();
        assertThat(tags, is(notNullValue()));
        assertThat(tags.size(), is(1));
        assertThat(tags.get(0), is("Construction"));
    }

    private void assertChoiceDetail(ChoiceDto choiceDto, String value, int order) {
        assertThat(choiceDto.getValue(), is(value));
        assertThat(choiceDto.getOrder(), is(order));
    }

    private void assertEventSource(EventSourceDto eventSourceDto) {
        assertThat(eventSourceDto, is(notNullValue()));
        assertThat(eventSourceDto.getEvent(), is("Create"));
        assertThat(eventSourceDto.getSource(), is("Loan"));
    }

    private void assertQuestionGroupDto(QuestionGroupDto questionGroupDto) {
        assertThat(questionGroupDto, is(notNullValue()));
        assertThat(questionGroupDto.getTitle(), is("PPI India"));
        assertThat(questionGroupDto.isEditable(), is(false));
        assertThat(questionGroupDto.isPpi(), is(true));
    }
}
