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

package org.mifos.platform.questionnaire.service;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class SectionQuestionDetailTest {

    @Test
    public void shouldGetAnswersForNoAnswer() {
        SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail(new QuestionDetail("Title", new MultiSelectQuestionTypeDto()), true);
        sectionQuestionDetail.setValue(null);
        sectionQuestionDetail.setValues(null);
        List<String> answers = sectionQuestionDetail.getAnswers();
        assertThat(answers, is(notNullValue()));
        assertThat(answers.size(), is(0));
    }
    
    @Test
    public void shouldGetAnswersForSingleAnswer() {
        SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail(new QuestionDetail("Title", new FreeTextQuestionTypeDto()), true);
        sectionQuestionDetail.setValue("Hello");
        List<String> answers = sectionQuestionDetail.getAnswers();
        assertThat(answers, is(notNullValue()));
        assertThat(answers.size(), is(1));
        assertThat(answers.get(0), is("Hello"));
    }

    @Test
    public void shouldGetAnswersForMultipleAnswers() {
        SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail(new QuestionDetail("Title", new MultiSelectQuestionTypeDto()), true);
        sectionQuestionDetail.setValues(asList("Ans1", "Ans2", "Ans3"));
        List<String> answers = sectionQuestionDetail.getAnswers();
        assertThat(answers, is(notNullValue()));
        assertThat(answers.size(), is(3));
        assertThat(answers.get(0), is("Ans1"));
        assertThat(answers.get(1), is("Ans2"));
        assertThat(answers.get(2), is("Ans3"));
    }

    @Test
    public void shouldGetAnswerForNoAnswer() {
        SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail(new QuestionDetail("Title", new FreeTextQuestionTypeDto()), true);
        assertThat(sectionQuestionDetail.getAnswer(), is(""));
    }
    
    @Test
    public void shouldGetAnswerForSingleAnswer() {
        SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail(new QuestionDetail("Title", new FreeTextQuestionTypeDto()), true);
        sectionQuestionDetail.setValue("Hello");
        assertThat(sectionQuestionDetail.getAnswer(), is("Hello"));
    }

    @Test
    public void shouldGetAnswerForMultipleAnswers() {
        SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail(new QuestionDetail("Title", new MultiSelectQuestionTypeDto()), true);
        sectionQuestionDetail.setValues(asList("Ans1", "Ans2", "Ans3"));
        String answer = sectionQuestionDetail.getAnswer();
        assertThat(answer, is("Ans1, Ans2, Ans3"));
    }
}
