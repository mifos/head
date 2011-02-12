/*
 * Copyright Grameen Foundation USA
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

import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;

import static org.junit.Assert.assertThat;

public class QuestionGroupDetailsMatcher extends TypeSafeMatcher<QuestionGroupDetails> {
    private QuestionGroupDetails questionGroupDetails;

    public QuestionGroupDetailsMatcher(QuestionGroupDetails questionGroupDetails) {
        this.questionGroupDetails = questionGroupDetails;
    }

    @Override
    public boolean matchesSafely(QuestionGroupDetails questionGroupDetails) {
        if (this.questionGroupDetails.getCreatorId() == questionGroupDetails.getCreatorId() &&
                this.questionGroupDetails.getEntityId() == questionGroupDetails.getEntityId()) {
            assertThat(this.questionGroupDetails.getDetails(), Matchers.is(new QuestionGroupDetailListMatcher(questionGroupDetails.getDetails())));
            return true;
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("QuestionGroupDetails do not match.");
    }
}
