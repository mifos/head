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
package org.mifos.platform.questionnaire.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SectionDetailTest {
    @Test
    public void shouldNotFindActiveQuestions() {
        SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail(getQuestionDetail(false), true);
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.addQuestion(sectionQuestionDetail);
        assertThat(sectionDetail.hasNoActiveQuestions(), is(true));
    }

    @Test
    public void shouldFindActiveQuestions() {
        SectionQuestionDetail sectionQuestionDetail1 = new SectionQuestionDetail(getQuestionDetail(false), true);
        SectionQuestionDetail sectionQuestionDetail2 = new SectionQuestionDetail(getQuestionDetail(true), true);
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.addQuestion(sectionQuestionDetail1);
        sectionDetail.addQuestion(sectionQuestionDetail2);
        assertThat(sectionDetail.hasNoActiveQuestions(), is(false));
    }

    private QuestionDetail getQuestionDetail(boolean active) {
        QuestionDetail questionDetail = new QuestionDetail("title", QuestionType.SINGLE_SELECT);
        questionDetail.setActive(active);
        return questionDetail;
    }
}
