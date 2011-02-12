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

package org.mifos.platform.questionnaire.ui.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.ui.model.Question;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class QuestionTest {

    @Test
    public void testAddAnswerChoice() {
        Question question = new Question(new QuestionDetail());
        question.setCurrentChoice("choice1");
        question.addAnswerChoice();
        question.setCurrentChoice("choice2");
        question.addAnswerChoice();
        question.setCurrentChoice("choice1");
        question.addAnswerChoice();
        question.setCurrentChoice("choice3");
        question.addAnswerChoice();
        assertThat(question.getChoices().size(), is(4));
        assertThat(question.getCurrentSmartChoiceTags().size(), is(4));
        assertEquals("choice1", question.getChoices().get(0).getValue());
        assertEquals("choice2", question.getChoices().get(1).getValue());
        assertEquals("choice1", question.getChoices().get(2).getValue());
        assertEquals("choice3", question.getChoices().get(3).getValue());
        assertEquals("choice1, choice2, choice1, choice3", question.getCommaSeparateChoices());
    }

    @Test
    public void testRemoveAnswerChoice() {
        Question question = new Question(new QuestionDetail());
        question.setCurrentChoice("choice1");
        question.addAnswerChoice();
        question.setCurrentChoice("choice2");
        question.addAnswerChoice();
        question.setCurrentChoice("choice1");
        question.addAnswerChoice();
        assertEquals("choice1", question.getChoices().get(0).getValue());
        assertEquals("choice2", question.getChoices().get(1).getValue());
        assertEquals("choice1", question.getChoices().get(2).getValue());
        question.removeChoice(0);
        assertEquals("choice2", question.getChoices().get(0).getValue());
        assertEquals("choice1", question.getChoices().get(1).getValue());
        question.removeChoice(1);
        assertEquals("choice2", question.getChoices().get(0).getValue());
    }

    @Test
    public void shouldGetTitleAndType() {
        assertQuestion("Question Title1", QuestionType.NUMERIC, "number", new LinkedList<String>());
        assertQuestion("Question Title2", QuestionType.FREETEXT, "freeText", new LinkedList<String>());
        assertQuestion("Question Title3", QuestionType.DATE, "date", new LinkedList<String>());
        assertQuestion("Question Title4", QuestionType.SINGLE_SELECT, "singleSelect", Arrays.asList("choice-1", "choice-2"));
        assertQuestion("Question Title5", QuestionType.MULTI_SELECT, "multiSelect", Arrays.asList("choice1", "choice2"));
    }

    @Test
    public void testQuestionTypeConversion() {
        Question question = new Question(new QuestionDetail());
        question.setType("number");
        assertThat(question.getType(), is("number"));
        question.setType("freeText");
        assertThat(question.getType(), is("freeText"));
        question.setType("date");
        assertThat(question.getType(), is("date"));
        question.setType("singleSelect");
        assertThat(question.getType(), is("singleSelect"));
        question.setType("number");
        assertThat(question.getType(), is("number"));
        question.setType("multiSelect");
        assertThat(question.getType(), is("multiSelect"));
        question.setType("Multi Selects");
        Assert.assertNull(question.getType());
    }

    @Test
    public void testAddSmartChoice() {
        Question question = new Question(new QuestionDetail());
        question.setCurrentSmartChoice("Choice1");
        question.addAnswerSmartChoice();
        assertThat(question.getCurrentSmartChoice(), is(nullValue()));
        assertThat(question.getCurrentSmartChoiceTags().size(), is(1));
        assertThat(question.getCurrentSmartChoiceTags().get(0), is(""));
        question.setCurrentSmartChoice("Choice2");
        question.addAnswerSmartChoice();
        assertThat(question.getCurrentSmartChoice(), is(nullValue()));
        assertThat(question.getCurrentSmartChoiceTags().size(), is(2));
        assertThat(question.getCurrentSmartChoiceTags().get(0), is(""));
        assertThat(question.getCurrentSmartChoiceTags().get(1), is(""));
    }

    @Test
    public void testAddSmartChoiceTag() {
        QuestionDetail questionDetail = new QuestionDetail();
        Question question = new Question(questionDetail);
        question.setCurrentSmartChoice("Choice1");
        question.addAnswerSmartChoice();
        question.setCurrentSmartChoice("Choice2");
        question.addAnswerSmartChoice();
        question.getCurrentSmartChoiceTags().set(1, "Tag1");
        question.addSmartChoiceTag(0);
        assertThat(questionDetail.getAnswerChoices().get(0).getTags().size(), is(0));
        assertThat(questionDetail.getAnswerChoices().get(1).getTags().size(), is(0));
        question.addSmartChoiceTag(1);
        assertThat(questionDetail.getAnswerChoices().get(1).getTags().get(0), is("Tag1"));
        assertThat(question.getCurrentSmartChoiceTags().get(1), is(""));
    }

    @Test
    public void testAddSmartChoiceTagUptoFiveTags() {
        QuestionDetail questionDetail = new QuestionDetail();
        Question question = new Question(questionDetail);
        question.setCurrentSmartChoice("Choice1");
        question.addAnswerSmartChoice();
        question.getCurrentSmartChoiceTags().set(0, "Tag_1");
        question.addSmartChoiceTag(0);
        question.getCurrentSmartChoiceTags().set(0, "Tag_2");
        question.addSmartChoiceTag(0);
        question.getCurrentSmartChoiceTags().set(0, "Tag_3");
        question.addSmartChoiceTag(0);
        question.getCurrentSmartChoiceTags().set(0, "Tag_4");
        question.addSmartChoiceTag(0);
        question.getCurrentSmartChoiceTags().set(0, "Tag_5");
        question.addSmartChoiceTag(0);
        assertThat(questionDetail.getAnswerChoices().get(0).getTags().size(), is(5));
        question.getCurrentSmartChoiceTags().set(0, "Tag_6");
        question.addSmartChoiceTag(0);
        assertThat(questionDetail.getAnswerChoices().get(0).getTags().size(), is(5));
        assertThat(question.getCurrentSmartChoiceTags().get(0), is(""));
    }

    @Test
    public void testAddSmartChoiceTagDoesnotAllowDuplicates() {
        QuestionDetail questionDetail = new QuestionDetail();
        Question question = new Question(questionDetail);
        question.setCurrentSmartChoice("Choice1");
        question.addAnswerSmartChoice();
        question.getCurrentSmartChoiceTags().set(0, "Tag_1");
        question.addSmartChoiceTag(0);
        question.getCurrentSmartChoiceTags().set(0, "Tag_1");
        question.addSmartChoiceTag(0);
        question.getCurrentSmartChoiceTags().set(0, "Tag_2");
        question.addSmartChoiceTag(0);
        question.getCurrentSmartChoiceTags().set(0, "TAg_2");
        question.addSmartChoiceTag(0);
        assertThat(questionDetail.getAnswerChoices().get(0).getTags().size(), is(2));
    }

    @Test
    public void testRemoveChoiceTag() {
        Question question = new Question(new QuestionDetail());
        question.setCurrentSmartChoice("Choice1");
        question.addAnswerSmartChoice();
        question.getCurrentSmartChoiceTags().set(0, "Tag1");
        question.addSmartChoiceTag(0);
        question.getCurrentSmartChoiceTags().set(0, "Tag2");
        question.addSmartChoiceTag(0);
        question.getCurrentSmartChoiceTags().set(0, "Tag3");
        question.addSmartChoiceTag(0);
        question.getCurrentSmartChoiceTags().set(0, "Tag4");
        question.addSmartChoiceTag(0);
        question.removeChoiceTag("0_2");
        assertThat(question.getQuestionDetail().getAnswerChoices().size(), is(1));
        assertThat(question.getQuestionDetail().getAnswerChoices().get(0).getTags().size(), is(3));
        assertThat(question.getQuestionDetail().getAnswerChoices().get(0).getTags().get(0), is("Tag1"));
        assertThat(question.getQuestionDetail().getAnswerChoices().get(0).getTags().get(1), is("Tag2"));
        assertThat(question.getQuestionDetail().getAnswerChoices().get(0).getTags().get(2), is("Tag4"));
        question.removeChoiceTag("0_0");
        question.removeChoiceTag("0_0");
        question.removeChoiceTag("0_0");
        assertThat(question.getQuestionDetail().getAnswerChoices().size(), is(1));
        assertThat(question.getQuestionDetail().getAnswerChoices().get(0).getTags().size(), is(0));
    }

    private void assertQuestion(String text, QuestionType questionType, String questionTypeString, List<String> choices) {
        QuestionDetail questionDetail = new QuestionDetail(123, text, questionType, true, true);
        List<ChoiceDto> choiceDtos = getChoiceDetails(choices);
        questionDetail.setAnswerChoices(choiceDtos);
        Question question = new Question(questionDetail);
        assertThat(question.getText(), is(text));
        assertThat(question.getType(), is(questionTypeString));
        assertEquals(question.getChoices(), choiceDtos);
    }

    private List<ChoiceDto> getChoiceDetails(List<String> choices) {
        List<ChoiceDto> choiceDtos = new ArrayList<ChoiceDto>();
        for (String choice : choices) choiceDtos.add(new ChoiceDto(choice));
        return choiceDtos;
    }
}
