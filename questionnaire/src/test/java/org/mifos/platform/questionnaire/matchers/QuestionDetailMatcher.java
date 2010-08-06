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
package org.mifos.platform.questionnaire.matchers;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mifos.platform.questionnaire.service.ChoiceDetail;
import org.mifos.platform.questionnaire.service.QuestionDetail;

import java.util.List;

import static junit.framework.Assert.assertEquals;

@SuppressWarnings("PMD")
public class QuestionDetailMatcher extends TypeSafeMatcher<QuestionDetail> {

    private final QuestionDetail questionDetail;

    public QuestionDetailMatcher(QuestionDetail questionDetail) {
        this.questionDetail = questionDetail;
    }

    @Override
    public boolean matchesSafely(QuestionDetail questionDetail) {
        if (StringUtils.equals(questionDetail.getShortName(), this.questionDetail.getShortName())
                && StringUtils.equals(questionDetail.getText(), this.questionDetail.getText())
                && StringUtils.equals(questionDetail.getTitle(), this.questionDetail.getTitle())
                && this.questionDetail.getType().equals(questionDetail.getType())) {
            List<ChoiceDetail> choiceDetails = this.questionDetail.getAnswerChoices();
            for (int i = 0, choiceDetailsSize = choiceDetails.size(); i < choiceDetailsSize; i++) {
                ChoiceDetail choiceDetail = choiceDetails.get(i);
                assertEquals(choiceDetail.getChoiceText(), questionDetail.getAnswerChoices().get(i).getChoiceText());
            }
            return true;
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Question details do not match");
    }

}
