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

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.ui.model.SectionDetailForm;
import org.mifos.platform.questionnaire.ui.model.SectionQuestionDetailForm;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SectionDetailFormTest {

    @Test
    public void shouldGetQuestions() {
        SectionDetailForm sectionDetailForm = new SectionDetailForm(getSectionDefinition());
        List<SectionQuestionDetailForm> sectionQuestions = sectionDetailForm.getSectionQuestions();
        assertThat(sectionQuestions, Matchers.notNullValue());
        assertThat(sectionQuestions.size(), Matchers.is(3));
        assertQuestionDetailForm(sectionQuestions.get(0), 121, "Question1", true);
        assertQuestionDetailForm(sectionQuestions.get(1), 122, "Question2", false);
        assertQuestionDetailForm(sectionQuestions.get(2), 123, "Question3", true);
    }

    private void assertQuestionDetailForm(SectionQuestionDetailForm questionDetailForm, int id, String title, boolean mandatory) {
        assertThat(questionDetailForm.getQuestionId(), Matchers.is(id));
        assertThat(questionDetailForm.getText(), Matchers.is(title));
        assertThat(questionDetailForm.isMandatory(), Matchers.is(mandatory));
    }

    private SectionDetail getSectionDefinition() {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.addQuestion(new SectionQuestionDetail(new QuestionDetail(121, "Question1", QuestionType.FREETEXT, true, true), true));
        sectionDetail.addQuestion(new SectionQuestionDetail(new QuestionDetail(122, "Question2", QuestionType.FREETEXT, true, true), false));
        sectionDetail.addQuestion(new SectionQuestionDetail(new QuestionDetail(123, "Question3", QuestionType.FREETEXT, true, true), true));
        return sectionDetail;
    }
}
