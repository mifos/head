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

package org.mifos.platform.questionnaire.ui.controller;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.ui.model.Question;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.is;

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
        Assert.assertThat(question.getChoices().size(), is(4));
        Assert.assertEquals(question.getChoices(), Arrays.asList("choice1", "choice2", "choice1", "choice3"));
        Assert.assertTrue(StringUtils.equals(question.getCommaSeparateChoices(), "choice1, choice2, choice1, choice3"));
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
        Assert.assertEquals(question.getChoices(), Arrays.asList("choice1", "choice2", "choice1"));
        question.removeChoice(0);
        Assert.assertEquals(question.getChoices(), Arrays.asList("choice2", "choice1"));
        question.removeChoice(1);
        Assert.assertEquals(question.getChoices(), Arrays.asList("choice2"));
    }

    @Test
    public void shouldGetTitleAndType() {
        assertQuestion("Question Title1", QuestionType.NUMERIC, "Number", new LinkedList<String>());
        assertQuestion("Question Title2", QuestionType.FREETEXT, "Free Text", new LinkedList<String>());
        assertQuestion("Question Title3", QuestionType.DATE, "Date", new LinkedList<String>());
        assertQuestion("Question Title4", QuestionType.SINGLE_SELECT, "Single Select", Arrays.asList("choice-1", "choice-2"));
        assertQuestion("Question Title5", QuestionType.MULTI_SELECT, "Multi Select", Arrays.asList("choice1", "choice2"));
    }

    @Test
    public void testQuestionTypeConversion() {
        Question question = new Question(new QuestionDetail());
        question.setType("Number");
        Assert.assertThat(question.getType(), is("Number"));
        question.setType("Free Text");
        Assert.assertThat(question.getType(), is("Free Text"));
        question.setType("Date");
        Assert.assertThat(question.getType(), is("Date"));
        question.setType("Single Select");
        Assert.assertThat(question.getType(), is("Single Select"));
        question.setType("Number");
        Assert.assertThat(question.getType(), is("Number"));
        question.setType("Multi Select");
        Assert.assertThat(question.getType(), is("Multi Select"));
        question.setType("Multi Selects");
        Assert.assertNull(question.getType());
    }


    private void assertQuestion(String shortName, QuestionType questionType, String questionTypeString, List<String> choices) {
        Question question = new Question(new QuestionDetail(123, "Question Text", shortName, questionType, choices));
        Assert.assertThat(question.getTitle(), is(shortName));
        Assert.assertThat(question.getType(), is(questionTypeString));
        Assert.assertEquals(question.getChoices(), choices);
    }
}
